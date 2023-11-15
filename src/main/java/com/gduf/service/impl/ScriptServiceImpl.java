package com.gduf.service.impl;

import com.gduf.dao.CommunityDAO;
import com.gduf.dao.ScriptDAO;
import com.gduf.dao.UserDAO;
import com.gduf.pojo.community.Post;
import com.gduf.pojo.community.PostTheme;
import com.gduf.pojo.community.PostTopic;
import com.gduf.pojo.script.ScriptChoice;
import com.gduf.pojo.script.commit.ScriptCommit;
import com.gduf.pojo.script.mapper.*;
import com.gduf.pojo.script.*;
import com.gduf.pojo.script.ScriptStatus;
import com.gduf.service.ScriptService;
import com.gduf.task.RefreshScore;
import com.gduf.util.JwtUtil;
import com.gduf.util.RedisCache;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.gduf.controller.Code.SCRIPT_STATUS_REPOSITORY;

@Slf4j
@Service
public class ScriptServiceImpl implements ScriptService {

    @Autowired
    private ScriptDAO scriptDAO;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RefreshScore refreshScore;

    @Autowired
    private CommunityDAO communityDAO;

    @Autowired
    private UserDAO userDAO;

    //    增加剧本 （名称及id及乱七八糟）
    @Override
    public ScriptMsg insertOrUpdateScript(ScriptMsg scriptMsg, ScriptInfluenceName scriptInfluenceName) {
        ScriptMsg script = scriptDAO.getScriptById(scriptMsg.getScriptId());

        try {
            if (Objects.isNull(script)) {
//                如果没有目标剧本 则返回null 则添加新草稿
//                设置剧本草稿箱状态
                scriptMsg.setScriptStatus(120);
                scriptDAO.insertScript(scriptMsg);
                scriptInfluenceName.setScriptId(scriptMsg.getScriptId());
                scriptDAO.insertScriptInfluenceName(scriptInfluenceName);
            } else {
//                如果有目标剧本 则返回对象 则执行更新操作 （一般不影响剧本状态 即tb_script_status表）
                scriptDAO.updateScript(scriptMsg);
                scriptDAO.updateScriptInfluenceName(scriptInfluenceName);
            }
        } catch (Exception e) {
            return null;
        }
        return scriptMsg;
    }

    //    增加剧本节点（每个节点的信息）
    @Override
    public boolean insertOrUpdateScriptNodes(List<ScriptNode> scriptNodes) {
        for (ScriptNode scriptNode : scriptNodes) {
            Integer scriptId = scriptNode.getScriptId();
            Integer nodeId = scriptNode.getNodeId();
//            构造映射对象
            Integer leftChoiceId;
            Integer rightChoiceId;
//                定义 选项（choice）中的 对应的剧本id
            scriptNode.getLeftChoice().setScriptId(scriptId);
            scriptNode.getRightChoice().setScriptId(scriptId);
//            如果是结尾节点的话 getLeftChoice并不是空的 但是get到的choiceId是null的
            leftChoiceId = scriptNode.getLeftChoice().getChoiceId();
            rightChoiceId = scriptNode.getRightChoice().getChoiceId();

            if (Objects.isNull(leftChoiceId)) {
                scriptNode.getLeftChoice().setChoiceId(-1);
                leftChoiceId = -1;
            }
            if (Objects.isNull(rightChoiceId)) {
                scriptNode.getRightChoice().setChoiceId(-1);
                rightChoiceId = -1;
            }
//            如果上传遇到结局id的时候 这里会将其id设置为-1
            ScriptNodeMsg nodeMsg = new ScriptNodeMsg(scriptNode, leftChoiceId, rightChoiceId);
            Integer nodeExist = scriptDAO.isNodeExist(scriptId, nodeId);
//            当等于0的时候 表示不存在这个节点 此时需要新增
            if (nodeExist.equals(0)) {
//            加入节点及选项信息
                scriptDAO.insertScriptNodeMsg(nodeMsg);
            } else {
//                进入else条件的时候 表示已经存在了这个剧本 此时需要更新剧本的信息
//                如果是结尾节点的话 需要一同更新结尾节点的choiceId为-1
                scriptDAO.updateScriptNodeMsg(nodeMsg);
            }
//            trycatch包裹 如果这个节点是结尾节点的话 是会报NullPointerException的
//            因为结尾节点压根没有leftChoice或rightChoice
            updateChoice(scriptId, scriptNode.getLeftChoice());
            updateChoice(scriptId, scriptNode.getRightChoice());
        }
        return true;
    }

    private void updateChoice(Integer scriptId, ScriptChoice scriptChoice) {
        Integer choiceId = scriptChoice.getChoiceId();
        if (choiceId.equals(-1))
            return;
//        如果是结尾节点则跳过新增的环节 (结尾节点不需要新增选项)
//        判断选项是否存在 如果存在则更新 不存在则新增
        Integer isChoiceExist = scriptDAO.isChoiceExist(scriptId, scriptChoice.getChoiceId());
        if (isChoiceExist.equals(0))
            scriptDAO.insertScriptChoice(scriptChoice);
        else
            scriptDAO.updateScriptChoice(scriptChoice);
    }

    @Override
    public Integer insertOrUpdateScriptEnds(ScriptSpecialEnd scriptSpecialEnd) {
        ScriptEnd scriptEnd;
        try {
            scriptEnd = scriptSpecialEnd.getScriptEnd();
            Integer scriptId = scriptSpecialEnd.getScriptId();
            Integer endId = scriptEnd.getEndId();
//            如果之前没有记录过这个结局的话 传过来的endId是空的
            if (endId == null) {
                scriptEnd.setScriptId(scriptId);
//                    手动设置该结局 所属的剧本id
                scriptDAO.insertScriptEnd(scriptEnd);
            } else {
//            如果传过来的endId不为空 需要做一下判断 看一下是否存在 并且是否属于对应的剧本
                if (scriptDAO.selectIfEndExist(endId, scriptId) == 0)
                    return null;
//                如果没问题的话 对结局执行更新
                scriptDAO.updateScriptEnd(scriptEnd);
            }
//        至此 for循环结束 特殊结局上传完毕
        } catch (Exception e) {
            return null;
        }
        return scriptEnd.getEndId();
    }

    @Override
    public Integer insertOrUpdateScriptNormalEnds(ScriptEnds scriptEnds) {
        ScriptNormalEnd scriptNormalEnd = scriptEnds.getScriptNormalEnd();
        scriptNormalEnd.setScriptId(scriptEnds.getScriptId());
        try {
            if (!scriptNormalEnd.isEmpty()) {
                Integer ifScriptNormalEndExist = 0;
                if (!Objects.isNull(scriptNormalEnd.getNormalEndId()))
                    //            判断有没有normalid 如果没有的话 就代表新增
                    ifScriptNormalEndExist = scriptDAO.selectIfScriptNormalEndExist(scriptNormalEnd.getNormalEndId(), scriptNormalEnd.getScriptId());
                if (ifScriptNormalEndExist.equals(0)) {
//        如果不存在普通结局 则新增
                    scriptDAO.insertScriptNormalEnd(scriptNormalEnd);
                } else {
//            如果存在 则更新
                    scriptDAO.updateScriptNormalEnd(scriptNormalEnd);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return scriptNormalEnd.getNormalEndId();
    }

    @Override
    public boolean checkScriptStatus(String token, Integer scriptId) {
        try {
            int producerId = decodeToId(token);
            Integer status = scriptDAO.selectIfScriptExist(producerId, scriptId);
            if (!Objects.isNull(status) && status.equals(100)) {
//                TODO 存在当前剧本时（更新已上架剧本的操作的默认逻辑）TBD
            } else if (!Objects.isNull(status) && status.equals(120)) {
//                TODO 存在当前剧本时（更新草稿箱剧本的操作的默认逻辑）TBD
            } else {
//                进入else语句则表示没有当前的剧本的状态
                ScriptStatus defaultScriptStatus = new ScriptStatus(scriptId, producerId);
                scriptDAO.insertScriptStatus(defaultScriptStatus);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String getScriptProducer(Integer scriptId) {
        Integer producerId = scriptDAO.getProducerId(scriptId);
        if (producerId.equals(0))
            return "官方制作";
        String username = userDAO.getUsername(producerId);
        return username;
    }

    @Override
    public List<ScriptMsg> getOfficialScript() {
        List<ScriptMsg> scriptMsg;
        scriptMsg = redisCache.getCacheList("officialScripts");
        if (scriptMsg.isEmpty()) {
            scriptMsg = new ArrayList<>();
//            初始化集合怕他报错
            try {
                List<Integer> officialScriptId = scriptDAO.getOfficialScriptId();
                for (Integer eachOfficialScript : officialScriptId) {
                    ScriptMsg officialScript = scriptDAO.getScriptById(eachOfficialScript);
                    scriptMsg.add(officialScript);
                }
                if (scriptMsg.isEmpty()) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        redisCache.setCacheList("officialScripts", scriptMsg, 5, TimeUnit.MINUTES);
        return scriptMsg;
    }

    @Override
    public List<ScriptWithScore> getAllScriptOnline() {
        List<Integer> onlineScriptId = scriptDAO.selectScriptOnline();
        HashMap<Integer, Integer> eachOnLineScriptScore = refreshScore.getScore();
        ArrayList<ScriptWithScore> scriptWithScores = new ArrayList<>();
        for (Integer id : onlineScriptId) {
            ScriptMsg scriptById = scriptDAO.getScriptById(id);
            Integer score = eachOnLineScriptScore.get(id);
            ScriptWithScore scriptWithScore = new ScriptWithScore(scriptById, score);
            scriptWithScores.add(scriptWithScore);
        }
        return scriptWithScores;
    }

    @Override
    public List<ScriptWithScore> getScriptByClassification(String classification) {
        boolean isClassification = checkClassificationLegality(classification);
        ArrayList<ScriptWithScore> scriptWithScores = new ArrayList<>();
        if (!isClassification)
            return null;
        List<ScriptMsg> scriptMsgsInClassification = scriptDAO.getScriptMsgByClassification(classification);
        HashMap<Integer, Integer> eachOnLineScriptScore = refreshScore.getScore();
        for (ScriptMsg scriptMsg : scriptMsgsInClassification) {
            Integer scriptStatus = scriptDAO.getScriptStatus(scriptMsg.getScriptId());
            if (scriptStatus.equals(120))
                continue;
            Integer score = eachOnLineScriptScore.get(scriptMsg.getScriptId());
            ScriptWithScore scriptWithScore = new ScriptWithScore(scriptMsg, score);
            scriptWithScores.add(scriptWithScore);
        }
        return scriptWithScores;
    }

    //    验证是不是真正的 剧本类型
    private boolean checkClassificationLegality(String classification) {
        return classification.equals("financialFraud") || classification.equals("telFraud")
                || classification.equals("overseaFraud") || classification.equals("cult")
                || classification.equals("pyramidSale") || classification.equals("wireFraud");
    }

    @Override
    public ScriptMsg getScriptMsg(Integer scriptId, Boolean isOnline) {
        ScriptMsg scriptMsg = null;
        if (isOnline)
            scriptMsg = redisCache.getCacheObject("scriptMsgIn" + scriptId);
        if (!isOnline || Objects.isNull(scriptMsg)) {
            try {
                scriptMsg = scriptDAO.getScriptById(scriptId);
                Integer scriptStatus = scriptDAO.getScriptStatus(scriptMsg.getScriptId());
                scriptMsg.setScriptStatus(scriptStatus);
            } catch (Exception e) {
                return null;
            }
        }
        if (isOnline)
            redisCache.setCacheObject("scriptMsgIn" + scriptId, scriptMsg, 5, TimeUnit.MINUTES);
        return scriptMsg;
    }

    //    弃用获取节点方法
    @Override
    public List<ScriptNode> getScriptNode(Integer scriptId) {
        List<ScriptNode> scriptNode;
        scriptNode = redisCache.getCacheList("scriptNodesIn" + scriptId);
        if (scriptNode == null) {
            try {
                scriptNode = scriptDAO.getScriptNode(scriptId);
            } catch (Exception e) {
                return null;
            }
        }
        redisCache.setCacheList("scriptNodesIn" + scriptId, scriptNode, 3, TimeUnit.MINUTES);
        return scriptNode;
    }

    @Override
    public List<ScriptNode> getScriptDetail(Integer scriptId, Boolean isOnline) {
        List<ScriptNode> list = new LinkedList<>();
        List<ScriptNodeMsg> scriptNodeMsg = scriptDAO.getScriptNodeMsg(scriptId);
//        如果空的 没有节点也直接返回 （草稿箱中的剧本就会出现这种情况）
        if (scriptNodeMsg.size() == 0)
            return null;
        if (isOnline)
            list = redisCache.getCacheList("scriptDetailIn" + scriptId);
//        进了下面的这个 isEmpty语句 表示从redis中拿不到
//        或者是未上线（草稿箱中的剧本）
        if (list.isEmpty())

//            这里其实有一个bug
//            scriptDAO.getScriptNodeChoice() 这个语句没有做非空判断
//            当遍历到表示结局的节点的时候 节点对象ScriptNodeMsg中 表示左右节点id的字段(leftChoiceId,rightChoiceId)其实是-1
//            换句话来说是get不到这个节点的 所以在这个时候
//            leftChoice 和 rightChoice 其实是null
//            进入到下方的映射类构造方法时 也是以null传进去的
//            传过去的时候 同样是null
//            但莫名其妙就跑起来了笑死我了

//        对每一个选择 获取左右所产生的结果 （去到 tb_script_node_choice 中查数据）
            for (ScriptNodeMsg nodeMsg : scriptNodeMsg) {

//            获取左节点的详细信息
                int leftChoiceId = nodeMsg.getLeftChoiceId();
                ScriptChoice leftChoice = scriptDAO.getScriptNodeChoice(scriptId, leftChoiceId);

//            获取右节点的详细信息
                int rightChoiceId = nodeMsg.getRightChoiceId();
                ScriptChoice rightChoice = scriptDAO.getScriptNodeChoice(scriptId, rightChoiceId);

//                获取完成 封装到包装类中
                ScriptNode scriptNode = new ScriptNode(nodeMsg.getNodeId(), nodeMsg.getWord(), scriptId, leftChoice, rightChoice);

                list.add(scriptNode);
            }
//        如果是上架的剧本 就将他的数据存到redis中
        if (isOnline)
            redisCache.setCacheList("scriptDetailIn" + scriptId, list, 5, TimeUnit.MINUTES);
        return list;
//        至此为止 这个链表中包含了所有节点及其相关的信息
    }

    @Override
    public String getScriptEnd(Integer scriptId, ScriptInfluenceChange scriptInfluenceChange) {
        String specialEnd;
        String normalEnd;
        try {
            specialEnd = scriptDAO.getScriptSpecialEnd(scriptId, scriptInfluenceChange.getInfluence1(), scriptInfluenceChange.getInfluence2(), scriptInfluenceChange.getInfluence3(), scriptInfluenceChange.getInfluence4());
            if (Objects.isNull(specialEnd)) {
                normalEnd = scriptDAO.getScriptNormalEnd(scriptInfluenceChange.getInfluence1(), scriptInfluenceChange.getInfluence2(), scriptInfluenceChange.getInfluence3(), scriptInfluenceChange.getInfluence4(), scriptId);
                return normalEnd;
            }
            return specialEnd;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    //    获取指标的名字
    public ScriptInfluenceName getScriptInfluenceName(Integer scriptId, Boolean isOnline) {
        ScriptInfluenceName scriptInfluenceName = null;
//        如果游玩的是 已经上线的剧本 则从redis里面查是否有 对应的信息
//        如果没有再查表
        if (isOnline)
            scriptInfluenceName = redisCache.getCacheObject("scriptInfluenceName" + scriptId);
//        不然会报错 values must not be null
        //        补充一个非空判断 如果不是空的才存进去
        if (!Objects.isNull(scriptInfluenceName))
            redisCache.setCacheObject("scriptInfluenceName" + scriptId, scriptInfluenceName, 5, TimeUnit.MINUTES);
        if (!isOnline || Objects.isNull(scriptInfluenceName)) {
            try {
                scriptInfluenceName = scriptDAO.getInfluenceName(scriptId);
            } catch (Exception e) {
                return null;
            }
        }
        return scriptInfluenceName;
    }

    //    记住曾经玩过的剧本的ID
    @Override
    public boolean rememberScript(String token, Integer scriptId) {
        int userId;
        try {
            userId = decodeToId(token);
//            如果调用此方法的是管理员用户 则代表是在审核中 不需要记录游玩操作
            if (userId == 0)
                return true;
            Integer ifPlayed = scriptDAO.ifPlayedScript(scriptId, userId);
//            如果不是空的（如果在数据库有查到游玩记录）
//            直接返回
            if (!Objects.isNull(ifPlayed)) {
                return true;
            } else
//                如果在数据库没查到游玩记录
//                记录游玩操作 并且返回
                scriptDAO.rememberPlayed(userId, scriptId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Integer scoreScript(String token, Integer score, Integer scriptId) {
        int userId;
        try {
            userId = decodeToId(token);
            Integer ifScored = scriptDAO.checkIfScored(userId, scriptId);
            if (ifScored.equals(1))
                return 0;
//            如果有查找到投票记录则说明已经投过票了
            scriptDAO.insertScriptScore(userId, scriptId, score);
        } catch (Exception e) {
            return -1;
        }
        return 1;
    }

    @Override
    public List<ScriptMsg> showMyDesign(String token) {
        ArrayList<ScriptMsg> scriptMsgs;
        try {
            int producerId = decodeToId(token);
//            注意状态100 为上架的剧本 120为草稿箱状态 110为下架状态
            scriptMsgs = new ArrayList<>();
            List<Integer> scriptRepository = scriptDAO.getScriptByProducerInRepository(producerId);
            for (Integer eachScript : scriptRepository) {
//                看看这个剧本是否被删除 如果删了就不渲染了
                Integer ifDel = scriptDAO.checkIfDel(eachScript);
                if (ifDel.equals(1))
                    continue;
                ScriptMsg script = scriptDAO.getScriptById(eachScript);
                script.setScriptStatus(scriptDAO.getScriptStatus(eachScript));
                scriptMsgs.add(script);
            }
        } catch (Exception e) {
            return null;
        }
        return scriptMsgs;
    }

    @Override
    public boolean insertScriptPost(String token, Integer scriptId) {
        try {
            int userId = decodeToId(token);
            ScriptMsg scriptMsg = scriptDAO.getScriptById(scriptId);
            ScriptInfluenceName influenceName = scriptDAO.getInfluenceName(scriptMsg.getScriptId());
            String main = scriptMsgToPost(scriptMsg, influenceName);

            Post post = new Post(scriptMsg.getScriptName(), main, userId);
            communityDAO.insertPost(post);
            Integer postId = post.getPostId();

            PostTheme postTheme = new PostTheme(1);
            postTheme.setPostId(postId);
            communityDAO.insertPostTheme(postTheme);

            PostTopic postTopic = new PostTopic();
            postTopic.setPostId(postId);
            communityDAO.insertPostTopic(postTopic);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean insertScriptFollower(Integer scriptId) {
        ScriptWithEnd script = getScript(scriptId);
        //            这里的script是 复制之前的 原来的剧本的标识 利用它找到整个对象
        Integer formalProducerId = scriptDAO.getProducerId(scriptId);
        String username = userDAO.getUsername(formalProducerId);


        redisCache.setCacheObject("script" + scriptId, new ScriptWithProducer(script, username));
        return true;
    }

    //    此方法包装剧本信息 成为帖子格式
    private String scriptMsgToPost(ScriptMsg scriptMsg, ScriptInfluenceName influenceName) throws IllegalAccessException {
        String main = "";
        main += "背景设定：" + scriptMsg.getScriptBackground() + '\n';
        main += "指标设定：" + '\n';
        main = insertInfluenceName(influenceName, main);
        return main;
    }

    //    此方法利用反射根据指标数量动态渲染
    private String insertInfluenceName(ScriptInfluenceName scriptInfluenceName, String main) throws IllegalAccessException {
        Class<? extends ScriptInfluenceName> clazz = scriptInfluenceName.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        int i = 1;
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
//            如果是字符串类型的 代表已经获取到了指标的名字 （对应的映射类中只有指标名字的数据类型为字符串）
            if (type.equals(String.class) && declaredField.get(scriptInfluenceName) != null) {
//                如果获取到的值 不是空的话 加入字符串当中
                System.out.println("指标" + i + "：" + declaredField.get(scriptInfluenceName));
                main += "指标" + (i++) + ":" + declaredField.get(scriptInfluenceName) + '\n';
            }
        }
        return main;
    }

    @Override
    public boolean forkToRepository(String token, Integer scriptId) {
        try {
            int forkUserId = decodeToId(token);
            ScriptWithProducer scriptWithProducer = new ScriptWithProducer();
            scriptWithProducer = redisCache.getCacheObject("script" + scriptId);
            ScriptWithEnd scriptWithEnd = scriptWithProducer.getScriptWithEnd();

//            TODO 这个地方可以返回共创人
            //    这个方法主要是清空得到的对象中残留的scriptId 其实可以直接使用反射来做。。但是不太好
            ScriptWithEnd scriptClear = setScriptId(scriptWithEnd, null);
            //        剧本总体信息
            ScriptMsg scriptMsg = scriptClear.getScript().getScriptMsg();
//            此时经过复制操作之后的剧本 表内没有记录 记作新的一个剧本 相当于复制操作
            scriptDAO.insertScript(scriptMsg);
//            加进去之后 由于在DAO层语句中 设置了回显主键 此时的scriptMsg是有自己独立的scriptId的
            forkRepository(scriptMsg.getScriptId(), forkUserId, setScriptId(scriptWithEnd, scriptMsg.getScriptId()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private ScriptWithEnd getScript(Integer scriptId) {
        //        获取剧本总体信息
        ScriptMsg scriptFork = scriptDAO.getScriptById(scriptId);
//        获取指标信息
        ScriptInfluenceName influenceName = scriptDAO.getInfluenceName(scriptId);
//        获取节点 及 选项信息
        List<ScriptNodeMsg> scriptNodeMsg = scriptDAO.getScriptNodeMsg(scriptId);
//        ArrayList<ScriptChoice> scriptChoices = new ArrayList<>();
        ArrayList<ScriptNode> scriptNodes = new ArrayList<>();
        for (ScriptNodeMsg nodeMsg : scriptNodeMsg) {

//            TODO 屎山待异常捕获空指针优化
//            如果nodeMsg（节点信息中）获取到左节点或者是右节点的id是-1的话 就表示来到了结尾
            if (nodeMsg.getLeftChoiceId().equals(-1)) {
                if (nodeMsg.getRightChoiceId().equals(-1)) {
//                    此时 这个节点两边都是空 表示这个节点两边的选项都是通往结局
                    scriptNodes.add(new ScriptNode(nodeMsg, null, null));
//                    哥们直接传空冲锋
                    continue;
//                    这里不continue的话 就会导致两个节点是空的会复制多一次
                }
//                左空右不空
                ScriptChoice rightChoice = scriptDAO.getScriptNodeChoice(scriptId, nodeMsg.getRightChoiceId());
                scriptNodes.add(new ScriptNode(nodeMsg, null, rightChoice));
                continue;
            } else if (!nodeMsg.getLeftChoiceId().equals(-1) && nodeMsg.getRightChoiceId().equals(-1)) {
//                右空左不空
                ScriptChoice leftChoice = scriptDAO.getScriptNodeChoice(scriptId, nodeMsg.getLeftChoiceId());
                scriptNodes.add(new ScriptNode(nodeMsg, leftChoice, null));
                continue;
            }
//            上面总体上是 分为结局和非结局做了区分 很屎山 可以利用异常处理优化 TBD

            ScriptChoice leftChoice = scriptDAO.getScriptNodeChoice(scriptId, nodeMsg.getLeftChoiceId());
            ScriptChoice rightChoice = scriptDAO.getScriptNodeChoice(scriptId, nodeMsg.getRightChoiceId());
//            scriptChoices.add(leftChoice);
//            scriptChoices.add(rightChoice);
            ScriptNode scriptNode = new ScriptNode(nodeMsg, leftChoice, rightChoice);
            scriptNodes.add(scriptNode);
        }
//        组装成剧本内容的封装对象
        Script script = new Script(scriptFork, scriptNodes, influenceName);

//        获取结局信息 组装结局对象
        ScriptNormalEnd scriptNormalEnds = scriptDAO.getScriptNormalEndById(scriptId);
        List<ScriptEnd> scriptSpecialEnds = scriptDAO.getScriptEndById(scriptId);
        ScriptEnds scriptEnds = new ScriptEnds(scriptId, scriptSpecialEnds, scriptNormalEnds);

//        组装剧本对象
        ScriptWithEnd scriptWithEnd = new ScriptWithEnd(script, scriptEnds);
        return scriptWithEnd;
    }

//    这个方法是用来设置剧本的id 使用时机为
//    在复制之前 消除原有的标识符 scriptId
//    复制之后 写入新的版本的时候 赋好新的scriptId 防止报错

    private ScriptWithEnd setScriptId(ScriptWithEnd scriptWithEnd, Integer value) {
        List<ScriptNode> scriptNodes = scriptWithEnd.getScript().getScriptNodes();
        for (ScriptNode scriptNode : scriptNodes) {
            scriptNode.setScriptId(value);
//            这个地方同getScript理 当遍历到涉及结局的节点的时候 rightChoice和leftChoice都是空的
//            此时getRightChoice出来的结果是null set不了

            try {
                scriptNode.getRightChoice().setScriptId(value);
            } catch (NullPointerException e) {
//                如果此时报空指针 就证明此节点的这个选项通往结局 不需要处理
            }
            try {
                scriptNode.getLeftChoice().setScriptId(value);
            } catch (NullPointerException e) {
//             两个语句要分开写 否则当上面的右选项通往结局
//             下面这个语句的左选项不通往结局的时候 就会遗漏信息
            }
        }
        scriptWithEnd.getScript().setScriptNodes(scriptNodes);

        scriptWithEnd.getScript().getScriptInfluenceName().setScriptId(value);
        scriptWithEnd.getScript().getScriptMsg().setScriptId(value);
        List<ScriptEnd> scriptEnd = scriptWithEnd.getScriptEnds().getScriptEnd();
        for (ScriptEnd specialEnd : scriptEnd) {
            specialEnd.setScriptId(value);
        }
        scriptWithEnd.getScriptEnds().setScriptEnd(scriptEnd);

        scriptWithEnd.getScriptEnds().getScriptNormalEnd().setScriptId(value);
        scriptWithEnd.getScriptEnds().setScriptId(value);

        return scriptWithEnd;
    }

    private void forkRepository(Integer scriptId, Integer forkUserId, ScriptWithEnd scriptWithEnd) {
//        剧本节点 选项信息
        for (ScriptNode scriptNode : scriptWithEnd.getScript().getScriptNodes()) {
//            当遍历到结局节点的时候 get到的Choice是null的
//            目的 将其choiceId设置为-1
//            我将犹如蟒蛇缠绕般使用异常捕获捕获空指针设置id
            Integer leftChoiceId;
            Integer rightChoiceId;
            try {
                leftChoiceId = scriptNode.getLeftChoice().getChoiceId();
            } catch (NullPointerException e) {
                leftChoiceId = -1;
            }
            try {
                rightChoiceId = scriptNode.getRightChoice().getChoiceId();
            } catch (NullPointerException e) {
                rightChoiceId = -1;
            }
            scriptDAO.insertScriptNodeMsg(new ScriptNodeMsg(scriptNode, leftChoiceId, rightChoiceId));
//            当id不为-1的时候 正常加入节点
//            为-1的时候则跑路
            if (!(leftChoiceId == -1))
                scriptDAO.insertScriptChoice(scriptNode.getLeftChoice());
            if (!(rightChoiceId == -1))
                scriptDAO.insertScriptChoice(scriptNode.getRightChoice());
        }

//        剧本指标信息
        scriptDAO.insertScriptInfluenceName(scriptWithEnd.getScript().getScriptInfluenceName());

//        剧本结局信息
        scriptDAO.insertScriptNormalEnd(scriptWithEnd.getScriptEnds().getScriptNormalEnd());
        for (ScriptEnd scriptEnd : scriptWithEnd.getScriptEnds().getScriptEnd()) {
            scriptDAO.insertScriptEnd(scriptEnd);
        }

//        以默认状态加入用户自己的草稿箱当中
        scriptDAO.insertScriptStatus(new ScriptStatus(scriptId, forkUserId));
    }

    @Override
    public boolean commit(String token, Integer scriptId) {
        int userId = 0;
        try {
            userId = decodeToId(token);
            scriptDAO.insertCommit(scriptId, userId, new Date());
            scriptDAO.updateCommitStatus(scriptId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean insertOrUpdateNodePosition(ScriptNodePositionList scriptNodePositionList) {
        Integer scriptId = scriptNodePositionList.getScriptId();
        try {
            List<ScriptNodePosition> scriptNodePositions = scriptNodePositionList.getScriptNodePositions();
            for (ScriptNodePosition scriptNodePosition : scriptNodePositions) {
                Integer nodeId = scriptNodePosition.getNodeId();
                scriptNodePosition.setScriptId(scriptId);
                Integer isExist = scriptDAO.checkIfNodePositionExist(scriptId, nodeId);
//        如果等于0的话 则表明之前没有存过此节点的位置
//            执行新增操作
                if (isExist.equals(0))
                    scriptDAO.insertNodePosition(scriptNodePosition);
//            如果找到的话 则说明曾经存储过
//            执行更新操作
                else if (isExist.equals(1))
                    scriptDAO.updateNodePosition(scriptNodePosition);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public ScriptNodePositionList scriptNodePositionList(Integer scriptId) {
        List<ScriptNodePosition> scriptNodePositions = scriptDAO.showNodePosition(scriptId);
        return new ScriptNodePositionList(scriptId, scriptNodePositions);
    }

    @Override
    public boolean delRepository(Integer scriptId) {
        try {
//            玩家在草稿箱删除自己的剧本的时候 默认是逻辑删除 不会真正删除节点 省事
            scriptDAO.logicalDelScript(scriptId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public ScriptEnds showRepositoryEnds(Integer scriptId) {
//        确认是否草稿箱中的剧本
        Integer scriptStatus = scriptDAO.getScriptStatus(scriptId);
        if (!scriptStatus.equals(SCRIPT_STATUS_REPOSITORY))
            return null;
        ScriptNormalEnd scriptNormalEnd;
        List<ScriptEnd> scriptEndList;
        try {
            scriptEndList = scriptDAO.getScriptEndById(scriptId);
            scriptNormalEnd = scriptDAO.getScriptNormalEndById(scriptId);
        } catch (Exception e) {
            return null;
        }
        ScriptEnds scriptEnds = new ScriptEnds(scriptId, scriptEndList, scriptNormalEnd);
        return scriptEnds;
    }

    @Override
    public boolean delRepositorySpecialEnd(Integer endId) {
        Integer scriptId = scriptDAO.getScriptIdByEndId(endId);
        Integer scriptStatus = scriptDAO.getScriptStatus(scriptId);
        if (!scriptStatus.equals(SCRIPT_STATUS_REPOSITORY))
            return false;
        try {
            scriptDAO.delSpecialEnd(endId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<ScriptCommit> getUserCommitRecord(String token) {
        List<ScriptCommit> userCommit;
        try {
            int userId = decodeToId(token);
            userCommit = scriptDAO.getUserCommit(userId);
            if (Objects.isNull(userCommit))
                return new ArrayList<>();
//            将审核记录异常状态和空的状态区分开来
//            异常 返回null
//            空则 返回分配空间后的列表
        } catch (Exception e) {
            return null;
        }
        return userCommit;
    }

    @Override
    public boolean delExtraNode(Integer scriptId, Integer nodeId) {
        try {
            scriptDAO.delNode(scriptId, nodeId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean modifyScriptClassification(Integer scriptId,String classification) {
        try {
            scriptDAO.updateScriptClassification(scriptId,classification);
        }catch (Exception e){
            log.info("修改剧本类型错误");
            return false;
        }
        return true;
    }

    private int decodeToId(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
        return Integer.parseInt(userId);
    }

}
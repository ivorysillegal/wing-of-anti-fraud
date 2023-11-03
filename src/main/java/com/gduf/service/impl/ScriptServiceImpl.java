package com.gduf.service.impl;

import com.gduf.dao.ScriptDAO;
import com.gduf.pojo.script.ScriptChoice;
import com.gduf.pojo.script.mapper.*;
import com.gduf.pojo.script.*;
import com.gduf.pojo.script.ScriptStatus;
import com.gduf.service.ScriptService;
import com.gduf.util.JwtUtil;
import com.gduf.util.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ScriptServiceImpl implements ScriptService {

    @Autowired
    private ScriptDAO scriptDAO;

    @Autowired
    private RedisCache redisCache;

    //    增加剧本 （名称及id及乱七八糟）
    @Override
    public ScriptMsg insertOrUpdateScript(ScriptMsg scriptMsg, ScriptInfluenceName scriptInfluenceName) {
        ScriptMsg script = scriptDAO.getScriptById(scriptMsg.getScriptId());

        try {
            if (Objects.isNull(script)) {
//                如果没有目标剧本 则返回null 则添加新草稿
                scriptMsg.setScriptStatus(true);
//        弃用状态指标
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
//            定义 选项（choice）中的 对应的剧本id
            Integer scriptId = scriptNode.getScriptId();
            scriptNode.getRightChoice().setScriptId(scriptId);
            scriptNode.getLeftChoice().setScriptId(scriptId);
//            构造映射对象
            Integer leftChoiceId = scriptNode.getLeftChoice().getChoiceId();
            Integer rightChoiceId = scriptNode.getRightChoice().getChoiceId();
            ScriptNodeMsg nodeMsg = new ScriptNodeMsg(scriptNode, leftChoiceId, rightChoiceId);
            Integer nodeExist = scriptDAO.isNodeExist(scriptId);
//            当等于0的时候 表示不存在这个剧本 此时需要新增
            if (nodeExist == 0) {
                //            加入节点及选项信息
                scriptDAO.insertScriptNodeMsg(nodeMsg);
                scriptDAO.insertScriptChoice(scriptNode.getLeftChoice());
                scriptDAO.insertScriptChoice(scriptNode.getRightChoice());
            } else {
//                进入else条件的时候 表示已经存在了这个剧本 此时需要更新剧本的信息
                scriptDAO.updateScriptNodeMsg(nodeMsg);
                scriptDAO.updateScriptChoice(scriptNode.getLeftChoice());
                scriptDAO.updateScriptChoice(scriptNode.getRightChoice());
            }
        }
        return true;
    }

    @Override
    public boolean insertOrUpdateScriptEnds(ScriptEnds scriptEnds) {
        try {
            Integer scriptId = scriptEnds.getScriptId();
            List<ScriptEnd> scriptEndsList = scriptEnds.getScriptEnd();
//        先对特殊结局进行处理
            for (ScriptEnd scriptEnd : scriptEndsList) {
                Integer endId = scriptEnd.getEndId();
//            如果之前没有记录过这个结局的话 传过来的endId是空的
                if (endId == null) {
                    scriptEnd.setScriptId(scriptId);
//                    手动设置该结局 所属的剧本id
                    scriptDAO.insertScriptEnd(scriptEnd);
                } else {
//            如果传过来的endId不为空 需要做一下判断 看一下是否存在 并且是否属于对应的剧本
                    if (scriptDAO.selectIfEndExist(endId, scriptId) == 0)
                        return false;
//                如果没问题的话 对结局执行更新
                    scriptDAO.updateScriptEnd(scriptEnd);
                }
            }
//        至此 for循环结束 特殊结局上传完毕
            ScriptNormalEnd scriptNormalEnd = scriptEnds.getScriptNormalEnd();
//                同上 手动设置该结局所属的剧本id
            scriptNormalEnd.setScriptId(scriptId);
            Integer ifScriptSpecialEndExist = scriptDAO.selectIfScriptNormalEndExist(scriptNormalEnd.getNormalEndId(), scriptNormalEnd.getScriptId());
//        如果不存在特殊结局 则新增
            if (ifScriptSpecialEndExist == 0) {
                scriptDAO.insertScriptNormalEnd(scriptNormalEnd);
            } else {
//            如果存在 则更新
                scriptDAO.updateScriptNormalEnd(scriptNormalEnd);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
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
    public List<ScriptMsg> getScript() {
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
    public List<ScriptMsg> getScriptByClassification(String classification) {
        boolean isClassification = checkClassificationLegality(classification);
        if (isClassification){
            List<ScriptMsg> scriptMsgsInClassification = scriptDAO.getScriptMsgByClassification(classification);
            return scriptMsgsInClassification;
        }
        return null;
    }

//    验证是不是真正的 剧本类型
    private boolean checkClassificationLegality(String classification){
        return classification.equals("financialFraud") || classification.equals("telFraud")
                || classification.equals("overseaFraud") || classification.equals("cult")
                || classification.equals("pyramidSale") || classification.equals("wireFraud");
    }

    @Override
    public ScriptMsg getScriptMsg(Integer scriptId) {
        ScriptMsg scriptMsg;
        scriptMsg = redisCache.getCacheObject("scriptMsgIn" + scriptId);
        if (scriptMsg == null) {
            try {
                scriptMsg = scriptDAO.getScriptById(scriptId);
            } catch (Exception e) {
                return null;
            }
        }
        redisCache.setCacheObject("scriptMsgIn" + scriptId, scriptMsg, 5, TimeUnit.MINUTES);
        return scriptMsg;
    }

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
    public List<ScriptNode> getScriptDetail(Integer scriptId) {
        List<ScriptNode> list = new LinkedList<>();
        List<ScriptNodeMsg> scriptNodeMsg = scriptDAO.getScriptNodeMsg(scriptId);
//        对每一个选择 获取左右所产生的结果 （去到 tb_script_node_choice 中查数据）

        list = redisCache.getCacheList("scriptDetailIn" + scriptId);
        if (list.isEmpty()) {

//            这里其实有一个bug
//            scriptDAO.getScriptNodeChoice() 这个语句没有做非空判断
//            当遍历到表示结局的节点的时候 节点对象ScriptNodeMsg中 表示左右节点id的字段(leftChoiceId,rightChoiceId)其实是-1
//            换句话来说是get不到这个节点的 所以在这个时候
//            leftChoice 和 rightChoice 其实是null
//            进入到下方的映射类构造方法时 也是以null传进去的
//            传过去的时候 同样是null

//            但莫名其妙就跑起来了笑死我了

            for (ScriptNodeMsg nodeMsg : scriptNodeMsg) {

//            获取左节点的详细信息
                int leftChoiceId = nodeMsg.getLeftChoiceId();
                ScriptChoice leftChoice = scriptDAO.getScriptNodeChoice(scriptId, leftChoiceId);

//            获取右节点的详细信息
                int rightChoiceId = nodeMsg.getRightChoiceId();
                ScriptChoice rightChoice = scriptDAO.getScriptNodeChoice(scriptId, rightChoiceId);

//                获取完成 封装到包装类中
                ScriptNode scriptNode = new ScriptNode(scriptId, nodeMsg.getWord(), nodeMsg.getNodeId(), leftChoice, rightChoice);

                list.add(scriptNode);
            }
        }
        redisCache.setCacheList("scriptDetailIn" + scriptId, list, 5, TimeUnit.MINUTES);
        return list;
//        至此为止 这个链表中包含了所有节点及其相关的信息
    }

    @Override
    public ScriptEndSent getScriptEnd(Integer scriptId, ScriptInfluenceChange scriptInfluenceChange) {
        String specialEnd;
        String normalEnd;
        try {
//            scriptSpecialEnd = scriptDAO.getScriptSpecialEnd(scriptId,scriptInfluence);
            specialEnd = scriptDAO.getScriptSpecialEnd(scriptId, scriptInfluenceChange.getInfluence1(), scriptInfluenceChange.getInfluence2(), scriptInfluenceChange.getInfluence3(), scriptInfluenceChange.getInfluence4());
//            scriptStoryEnd = scriptDAO.getScriptStoryEnd(scriptInfluence, scriptId);
            normalEnd = scriptDAO.getScriptNormalEnd(scriptInfluenceChange.getInfluence1(), scriptInfluenceChange.getInfluence2(), scriptInfluenceChange.getInfluence3(), scriptInfluenceChange.getInfluence4(), scriptId);
        } catch (Exception e) {
            return null;
        }
        ScriptEndSent scriptEndSent = new ScriptEndSent(specialEnd, normalEnd);
        return scriptEndSent;
    }

    //    获取指标的名字
    @Override
    public ScriptInfluenceName getScriptInfluenceName(Integer scriptId) {
        ScriptInfluenceName scriptInfluenceName;
        scriptInfluenceName = redisCache.getCacheObject("scriptInfluenceName" + scriptId);
        if (Objects.isNull(scriptInfluenceName)) {
            try {
                scriptInfluenceName = scriptDAO.getInfluenceName(scriptId);
            } catch (Exception e) {
                return null;
            }
        }
        redisCache.setCacheObject("scriptInfluenceName" + scriptId, scriptInfluenceName, 5, TimeUnit.MINUTES);
        return scriptInfluenceName;
    }

    //    记住曾经玩过的剧本的ID
    @Override
    public boolean rememberScript(String token, Integer scriptId) {
        int userId;
        try {
            userId = decodeToId(token);
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
    public List<ScriptMsg> showMyDesign(String token) {
        ArrayList<ScriptMsg> scriptMsgs;
        try {
            int producerId = decodeToId(token);
//            注意状态100 为上架的剧本 120为草稿箱状态 110为下架状态
            scriptMsgs = new ArrayList<>();
            List<Integer> scriptRepository = scriptDAO.getScriptByProducerInRepository(producerId);
            for (Integer eachScript : scriptRepository) {
                ScriptMsg script = scriptDAO.getScriptById(eachScript);
                scriptMsgs.add(script);
            }
        } catch (Exception e) {
            return null;
        }
        return scriptMsgs;
    }

    @Override
    public boolean forkToRepository(String token, Integer scriptId) {
        try {
            int forkUserId = decodeToId(token);
            ScriptWithEnd scriptWithEnd = getScript(scriptId);
//            这里的script是 复制之前的 原来的剧本的标识 利用它找到整个对象
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

//            如果nodeMsg（节点信息中）获取到左节点或者是右节点的id是-1的话 就表示来到了结尾
            if (nodeMsg.getLeftChoiceId().equals(-1)){
                if (nodeMsg.getRightChoiceId().equals(-1)){
//                    此时 这个节点两边都是空 表示这个节点两边的选项都是通往结局
                    scriptNodes.add(new ScriptNode(nodeMsg,null,null));
//                    哥们直接传空冲锋
                }
//                左空右不空
                ScriptChoice rightChoice = scriptDAO.getScriptNodeChoice(scriptId, nodeMsg.getRightChoiceId());
                scriptNodes.add(new ScriptNode(nodeMsg,null,rightChoice));
                continue;
            }else if (!nodeMsg.getLeftChoiceId().equals(-1) && nodeMsg.getRightChoiceId().equals(-1)){
//                右空左不空
                ScriptChoice leftChoice = scriptDAO.getScriptNodeChoice(scriptId, nodeMsg.getLeftChoiceId());
                scriptNodes.add(new ScriptNode(nodeMsg,leftChoice,null));
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
            scriptNode.getLeftChoice().setScriptId(value);
            scriptNode.getRightChoice().setScriptId(value);
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
            scriptDAO.insertScriptNodeMsg(new ScriptNodeMsg(scriptNode, scriptNode.getLeftChoice().getChoiceId(), scriptNode.getRightChoice().getChoiceId()));
            scriptDAO.insertScriptChoice(scriptNode.getLeftChoice());
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
            scriptDAO.insertCommit(scriptId, userId);
        } catch (Exception e) {
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
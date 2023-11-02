package com.gduf.service.impl;

import com.gduf.dao.ScriptDAO;
import com.gduf.pojo.script.ScriptChoice;
import com.gduf.pojo.script.mapper.ScriptEnd;
import com.gduf.pojo.script.mapper.ScriptEnds;
import com.gduf.pojo.script.mapper.ScriptNode;
import com.gduf.pojo.script.*;
import com.gduf.pojo.script.ScriptStatus;
import com.gduf.pojo.script.mapper.ScriptNormalEnd;
import com.gduf.service.ScriptService;
import com.gduf.util.JwtUtil;
import com.gduf.util.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        ScriptMsg script = scriptDAO.getScriptBypId(scriptMsg.getScriptId());

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
            System.out.println(e.getCause());
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
            List<ScriptEnd> scriptEndsList = scriptEnds.getScriptEnds();
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
        scriptMsg = redisCache.getCacheList("scripts");
        if (scriptMsg == null) {
            try {
                scriptMsg = scriptDAO.getAllScript();
            } catch (Exception e) {
                return null;
            }
        }
        redisCache.setCacheList("scripts", scriptMsg);
        return scriptMsg;
    }

    @Override
    public ScriptMsg getScriptMsg(Integer scriptId) {
        ScriptMsg scriptMsg;
        scriptMsg = redisCache.getCacheObject("scriptMsgIn" + scriptId);
        if (scriptMsg == null) {
            try {
                scriptMsg = scriptDAO.getScriptMsg(scriptId);
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

    private int decodeToId(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
        return Integer.parseInt(userId);
    }
}
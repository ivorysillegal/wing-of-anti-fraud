package com.gduf.service.impl;

import com.gduf.dao.ScriptDAO;
import com.gduf.pojo.script.*;
import com.gduf.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ScriptServiceImpl implements ScriptService {

    @Autowired
    private ScriptDAO scriptDAO;

    //    增加剧本 （名称及id及乱七八糟）
    @Override
    public boolean insertScript(ScriptMsg scriptMsg, ScriptInfluenceName scriptInfluenceName) {
        try {
            scriptDAO.insertScript(scriptMsg);
            scriptDAO.insertScriptInfluenceName(scriptInfluenceName);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //    增加剧本节点（每个节点的信息）
    @Override
    public boolean insertScriptNodes(List<ScriptNode> scriptNodes) {
        try {
            for (ScriptNode scriptNode : scriptNodes) {
//                scriptDAO.insertScriptNode(scriptNode);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<ScriptMsg> getScript() {
        List<ScriptMsg> scriptMsg;
        try {
            scriptMsg = scriptDAO.getAllScript();
        } catch (Exception e) {
            return null;
        }
        return scriptMsg;
    }

    @Override
    public ScriptMsg getScriptMsg(Integer scriptId) {
        ScriptMsg scriptMsg;
        try {
            scriptMsg = scriptDAO.getScriptMsg(scriptId);
        } catch (Exception e) {
            return null;
        }
        return scriptMsg;
    }

    @Override
    public List<ScriptNode> getScriptNode(Integer ScriptId) {
        List<ScriptNode> scriptNode;
        try {
            scriptNode = scriptDAO.getScriptNode(ScriptId);
        } catch (Exception e) {
            return null;
        }
        return scriptNode;
    }

    @Override
    public List<ScriptNode> getScriptDetail(Integer scriptId) {
        List<ScriptNode> list = new LinkedList<>();
        List<ScriptNodeMsg> scriptNodeMsg = scriptDAO.getScriptNodeMsg(scriptId);
//        对每一个选择 获取左右所产生的结果 （去到 tb_script_node_choice 中查数据）

        for (ScriptNodeMsg nodeMsg : scriptNodeMsg) {

//            获取左节点的详细信息
            int leftChoiceId = nodeMsg.getLeftChoiceId();
            ScriptChoice leftChoice = scriptDAO.getScriptNodeChoice(scriptId, leftChoiceId);

//            获取右节点的详细信息
            int rightChoiceId = nodeMsg.getRightChoiceId();
            ScriptChoice rightChoice = scriptDAO.getScriptNodeChoice(scriptId, rightChoiceId);
            ScriptNode scriptNode = new ScriptNode(scriptId, nodeMsg.getWord(), nodeMsg.getNodeId(), leftChoice, rightChoice);
            list.add(scriptNode);
        }
        return list;
//        至此为止 这个链表中包含了所有节点及其相关的信息
    }

    @Override
    public ScriptEnd getScriptEnd(Integer scriptId, ScriptInfluenceChange scriptInfluenceChange) {
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
        ScriptEnd scriptEnd = new ScriptEnd(specialEnd, normalEnd);
        return scriptEnd;
    }

    @Override
    public ScriptInfluenceName getScriptInfluenceName(Integer scriptId) {
        ScriptInfluenceName scriptInfluenceName;
        try {
            scriptInfluenceName = scriptDAO.getInfluenceName(scriptId);
        } catch (Exception e) {
            return null;
        }
        return scriptInfluenceName;
    }
}
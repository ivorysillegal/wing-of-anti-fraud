package com.itheima.service.impl;

import com.itheima.dao.ScriptDAO;
import com.itheima.pojo.Script;
import com.itheima.pojo.ScriptNode;
import com.itheima.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScriptServiceImpl implements ScriptService {

    @Autowired
    private ScriptDAO scriptDAO;

//    增加剧本 （名称及id及乱七八糟）
    @Override
    public boolean insertScript(Script script) {
        try {
            scriptDAO.insertScript(script);
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
                scriptDAO.insertScriptNode(scriptNode);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<Script> getScript() {
        List<Script> script;
        try {
            script = scriptDAO.getAllScript();
        } catch (Exception e) {
            return null;
        }
        return script;
    }

    @Override
    public Script getScript(Integer scriptId) {
        Script script;
        try {
            script = scriptDAO.getScript(scriptId);
        } catch (Exception e) {
            return null;
        }
        return script;
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
}

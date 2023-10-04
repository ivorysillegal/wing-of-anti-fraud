package com.itheima.service;

import com.itheima.pojo.Script;
import com.itheima.pojo.ScriptNode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ScriptService {
    public boolean insertScript(Script script, List<ScriptNode> scriptNodes);

    public List<Script> getScript();

    public Script getScript(Integer scriptId);

    public List<ScriptNode> getScriptNode(Integer scriptId);
}

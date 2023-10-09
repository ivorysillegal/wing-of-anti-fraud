package com.gduf.service;

import com.gduf.pojo.Script;
import com.gduf.pojo.ScriptNode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ScriptService {
    public boolean insertScript(Script script);

    public boolean insertScriptNodes(List<ScriptNode> scriptNodes);

    public List<Script> getScript();

    public Script getScript(Integer scriptId);

    public List<ScriptNode> getScriptNode(Integer scriptId);
}

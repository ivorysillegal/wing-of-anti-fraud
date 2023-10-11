package com.gduf.service;

import com.gduf.pojo.script.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ScriptService {
    public boolean insertScript(ScriptMsg scriptMsg);

    public boolean insertScriptNodes(List<ScriptNode> scriptNodes);

    public List<ScriptMsg> getScript();

    public ScriptMsg getScriptMsg(Integer scriptId);

    public List<ScriptNode> getScriptNode(Integer scriptId);

    public List<ScriptNode> getScriptDetail(Integer scriptId);

    public ScriptEnd getScriptEnd(Integer scriptId, ScriptInfluence scriptInfluence);
}

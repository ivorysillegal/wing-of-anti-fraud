package com.gduf.service;

import com.gduf.pojo.script.mapper.ScriptEnds;
import com.gduf.pojo.script.mapper.ScriptNode;
import com.gduf.pojo.script.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ScriptService {
    public ScriptMsg insertOrUpdateScript(ScriptMsg scriptMsg,ScriptInfluenceName scriptInfluenceName);

    public boolean insertOrUpdateScriptNodes(List<ScriptNode> scriptNodes);

    public boolean insertOrUpdateScriptEnds(ScriptEnds scriptEnds);

    public boolean checkScriptStatus(String token,Integer scriptId);

    public List<ScriptMsg> getScript();

    public ScriptMsg getScriptMsg(Integer scriptId);

    public List<ScriptNode> getScriptNode(Integer scriptId);

    public List<ScriptNode> getScriptDetail(Integer scriptId);

    public ScriptEndSent getScriptEnd(Integer scriptId, ScriptInfluenceChange scriptInfluenceChange);

    public ScriptInfluenceName getScriptInfluenceName(Integer scriptId);

//    记住用户此次玩过的剧本
    public boolean rememberScript(String token,Integer scriptId);
}

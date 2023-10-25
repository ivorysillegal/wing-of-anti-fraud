package com.gduf.service;

import com.gduf.pojo.script.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ScriptService {
    public boolean insertScript(ScriptMsg scriptMsg,ScriptInfluenceName scriptInfluenceName);

    public boolean insertScriptNodes(List<ScriptNode> scriptNodes);

    public List<ScriptMsg> getScript();

    public ScriptMsg getScriptMsg(Integer scriptId);

    public List<ScriptNode> getScriptNode(Integer scriptId);

    public List<ScriptNode> getScriptDetail(Integer scriptId);

    public ScriptEnd getScriptEnd(Integer scriptId, ScriptInfluenceChange scriptInfluenceChange);

    public ScriptInfluenceName getScriptInfluenceName(Integer scriptId);

//    记住用户此次玩过的剧本
    public boolean rememberScript(String token,Integer scriptId);
}

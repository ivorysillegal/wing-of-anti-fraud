package com.gduf.service;

import com.gduf.pojo.script.mapper.ScriptEnds;
import com.gduf.pojo.script.mapper.ScriptNode;
import com.gduf.pojo.script.*;
import com.gduf.pojo.script.mapper.ScriptWithScore;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ScriptService {
    public ScriptMsg insertOrUpdateScript(ScriptMsg scriptMsg, ScriptInfluenceName scriptInfluenceName);

    public boolean insertOrUpdateScriptNodes(List<ScriptNode> scriptNodes);

    public boolean insertOrUpdateScriptEnds(ScriptEnds scriptEnds);

    public boolean checkScriptStatus(String token, Integer scriptId);

    public List<ScriptMsg> getScript();

    public List<ScriptWithScore> getScriptByClassification(String classification);

    public ScriptMsg getScriptMsg(Integer scriptId);

    public List<ScriptNode> getScriptNode(Integer scriptId);

    public List<ScriptNode> getScriptDetail(Integer scriptId);

    public ScriptEndSent getScriptEnd(Integer scriptId, ScriptInfluenceChange scriptInfluenceChange);

    public ScriptInfluenceName getScriptInfluenceName(Integer scriptId);

    //    记住用户此次玩过的剧本
    public boolean rememberScript(String token, Integer scriptId);

//    用户体验完剧本之后 给剧本打分
    public Integer scoreScript(String token,Integer score,Integer scriptId);

    //    展示用户 草稿箱中的剧本的总体信息
    public List<ScriptMsg> showMyDesign(String token);

    //    将别人的半成品搞过来自己进行加工
    public boolean forkToRepository(String token, Integer scriptId);

    //    申请者发过来 申请审核
    public boolean commit(String token, Integer scriptId);
}

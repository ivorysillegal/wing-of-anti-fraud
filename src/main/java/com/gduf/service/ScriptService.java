package com.gduf.service;

import com.gduf.pojo.script.commit.ScriptCommit;
import com.gduf.pojo.script.mapper.*;
import com.gduf.pojo.script.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ScriptService {
    public ScriptMsg insertOrUpdateScript(ScriptMsg scriptMsg, ScriptInfluenceName scriptInfluenceName);

    public boolean insertOrUpdateScriptNodes(List<ScriptNode> scriptNodes);

    public Integer insertOrUpdateScriptEnds(ScriptSpecialEnd scriptSpecialEnd);

    public Integer insertOrUpdateScriptNormalEnds(ScriptEnds scriptEnds);

    public boolean checkScriptStatus(String token, Integer scriptId);

    //    获取剧本的制作人
    public String getScriptProducer(Integer scriptId);

    //    获取官方制作的6个剧本
    public List<ScriptMsg> getOfficialScript();

    //    获取所有上线了的剧本的id
    public List<ScriptWithScore> getAllScriptOnline();

    public List<ScriptWithScore> getScriptByClassification(String classification);

    public ScriptMsg getScriptMsg(Integer scriptId, Boolean isOnline);

    public List<ScriptNode> getScriptNode(Integer scriptId);

    public List<ScriptNode> getScriptDetail(Integer scriptId, Boolean isOnline);

    public String getScriptEnd(Integer scriptId, ScriptInfluenceChange scriptInfluenceChange);

    public ScriptInfluenceName getScriptInfluenceName(Integer scriptId, Boolean isOnline);

    //    记住用户此次玩过的剧本
    public boolean rememberScript(String token, Integer scriptId);

    //    用户体验完剧本之后 给剧本打分
    public Integer scoreScript(String token, Integer score, Integer scriptId);

    //    展示用户 草稿箱中的剧本的总体信息
    public List<ScriptMsg> showMyDesign(String token);

    //    将自己的半成品剧本分享到社区(作为帖子)
    public boolean insertScriptPost(String token, Integer scriptId);

    //    根据剧本id获取到对应的剧本后 将剧本存入mongodb中 之后fork就直接从redis中拿
    public boolean insertScriptFollower(Integer scriptId);

    //    将别人的半成品搞过来自己进行加工
    public boolean forkToRepository(String token, Integer scriptId);

    //    申请者发过来 申请审核
    public boolean commit(String token, Integer scriptId);

    //    增加 或者 更新 节点在前端页面中的位置
    public boolean insertOrUpdateNodePosition(ScriptNodePositionList scriptNodePositionList);

    //    前端获取 发送节点在前端的位置
    public ScriptNodePositionList scriptNodePositionList(Integer scriptId);

    //    删除对应草稿箱的剧本（逻辑删除 不渲染）
    public boolean delRepository(Integer scriptId);

    //    展示出草稿箱中的所有结局
    public ScriptEnds showRepositoryEnds(Integer scriptId);

//    删除剧本的特殊结局
    public boolean delRepositorySpecialEnd(Integer endId);

//    获取用户的审核记录
    public List<ScriptCommit> getUserCommitRecord(String token);

//    删除特定的剧本中特定的节点
    public boolean delExtraNode(Integer scriptId,Integer nodeId);

//    通过剧本id 改变剧本类别
    public boolean modifyScriptClassification(Integer scriptId,String classification);
}

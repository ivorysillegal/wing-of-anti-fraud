package com.gduf.service;

import com.gduf.pojo.community.*;
import com.gduf.util.TreeNode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//投票板块的接口
@Transactional
public interface VoteService {

//    渲染投票
    public VoteWithChoice showVote(Integer id);

//    执行投票
    public boolean voteAction(Integer voteId,Boolean opinion);

    public List<VoteWithChoice> showNewestVote();

//    获取所有评论 包括一级 二级
    public List<VoteComment> showVoteComment(Integer voteId);

//    获取所有一级评论
    public List<VoteFirstComment> showFirstVoteComment(Integer voteId);

//    获取特定帖子的二级评论
    public TreeNode<VoteSecondComment> showExtraSecondVoteComment(Integer firstVoteCommentId);
}

package com.gduf.service;

import com.gduf.pojo.community.*;
import com.gduf.util.TreeNode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//投票板块的接口
@Transactional
public interface VoteService {

//    渲染投票
    public VoteWithChoice showVoteByTerm(Integer term);

//    执行投票
    public boolean voteAction(Integer voteId,Boolean opinion,String token);

    public List<VoteWithChoice> showNewestVote();

//    获取所有评论 包括一级 二级
    public List<VoteComment> showVoteComment(Integer voteId);

//    获取所有一级评论
    public List<VoteFirstComment> showFirstVoteComment(Integer voteId);

//    获取特定帖子的二级评论
//    public TreeNode<VoteSecondComment> showExtraSecondVoteComment(Integer firstVoteCommentId);

//    获取特定帖子的二级评论
    public List<TreeNode<VoteSecondComment>> showExtraSecondVoteComment(Integer firstVoteCommentId);

//    添加一级评论 （输入评论）
    public boolean insertFirstComment(VoteFirstComment voteFirstComment);

//    添加二级评论（即 回复一级评论）
    public boolean insertSecondComment(VoteSecondComment voteSecondComment);
}

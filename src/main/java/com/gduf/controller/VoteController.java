package com.gduf.controller;

import com.gduf.pojo.community.*;
import com.gduf.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gduf.controller.Code.*;

@RestController
@RequestMapping("/community/vote")
public class VoteController {

    private static Integer LATEST_TERM = 1;

    @Autowired
    private VoteService voteService;

    @GetMapping
    public Result showNewestVote() {
        VoteWithChoice voteWithChoice = voteService.showVoteByTerm(LATEST_TERM);
        if (Objects.isNull(voteWithChoice) || Objects.isNull(voteWithChoice.getVote())) {
            return new Result("最新一期投票读取失败", SHOW_VOTE_ERR, null);
        } else return new Result("最新一期投票读取成功", SHOW_VOTE_OK, voteWithChoice);
    }

    @GetMapping("/before")
    public Result showVoteByTime() {
        List<VoteAndChoice> votes = voteService.showLastVote();
        if (Objects.isNull(votes) || votes.isEmpty()) {
            return new Result("往期投票读取失败", SHOW_VOTE_ERR, null);
        }
        return new Result("往期投票读取成功", SHOW_VOTE_OK, votes);
    }

    @GetMapping("/voted")
    public Result showMyVoted(@RequestHeader String token) {
        List<VoteUser> voteUsers = voteService.showMyVoted(token);
        if (Objects.isNull(voteUsers))
            return new Result("读取用户投票记录失败", SHOW_MY_VOTE_ERR, null);
        return new Result("读取用户投票记录成功", SHOW_MY_VOTE_OK, voteUsers);
    }

    @PostMapping
    public Result voteAction(@RequestBody Map map, @RequestHeader String token) {
        Integer voteId = (Integer) map.get("voteId");
        Boolean opinion = (Boolean) map.get("opinion");
        Integer isVote = voteService.voteAction(voteId, opinion, token);
        if (isVote.equals(1))
            return new Result("投票成功", VOTE_OK, null);
        else if (isVote.equals(0))
            return new Result("已投票过 投票失败", VOTE_ERR, null);
        return new Result("投票失败", VOTE_ERR, null);
    }

    //    显示特定帖子的二级评论
    @PostMapping("/comment/second")
    public Result showSecondVoteComments(@RequestBody Map map) {
        Integer firstVoteCommentId = (Integer) map.get("firstVoteCommentId");
//        List<TreeNode<VoteSecondComment>> treeNodes;
        List<VoteSecondComment> voteSecondComments;
        try {
//            treeNodes = voteService.showExtraSecondVoteComment(firstVoteCommentId);
            voteSecondComments = voteService.showExtraSecondVoteComment(firstVoteCommentId);
        } catch (Exception e) {
            return new Result("显示二级评论失败", SHOW_VOTE_COMMENT_ERR, null);
        }
        return new Result("显示二级评论成功 或没有二级评论", SHOW_VOTE_COMMENT_OK, voteSecondComments);
    }

    //    显示一级评论
    @PostMapping("/comment/first")
    public Result showFirstVoteComments(@RequestBody Map map) {
        Integer voteId = (Integer) map.get("voteId");
        List<VoteFirstComment> voteFirstComments = voteService.showFirstVoteComment(voteId);
        if (voteFirstComments == null)
            return new Result("显示一级评论失败", SHOW_VOTE_COMMENT_ERR, null);
        return new Result("显示一级评论成功", SHOW_VOTE_COMMENT_OK, voteFirstComments);
    }

    @PostMapping("/comment/write")
    public Result insertFirstComment(@RequestHeader String token, @RequestBody VoteFirstComment voteFirstComment) {
        Integer isInsert = voteService.insertFirstComment(token, voteFirstComment);
        if (isInsert.equals(1))
            return new Result("添加评论成功", INSERT_VOTE_FIRST_COMMENT_OK, null);
        else if (isInsert.equals(0))
            return new Result("未投过票不能评论", INSERT_VOTE_FIRST_COMMENT_ERR, null);
        return new Result("添加评论失败", INSERT_VOTE_FIRST_COMMENT_ERR, null);
    }

    @PostMapping("/comment/reply")
    public Result insertSecondComment(@RequestHeader String token, @RequestBody VoteSecondComment voteSecondComment) {
        Integer isInsert = voteService.insertSecondComment(token, voteSecondComment);
        if (isInsert.equals(1)) {
            return new Result("添加评论成功", INSERT_VOTE_FIRST_COMMENT_OK, null);
        } else if (isInsert.equals(0))
            return new Result("未投票无法评论", INSERT_VOTE_FIRST_COMMENT_ERR, null);
        return new Result("添加评论失败", INSERT_VOTE_FIRST_COMMENT_ERR, null);
    }
}
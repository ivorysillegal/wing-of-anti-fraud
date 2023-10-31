package com.gduf.controller;

import com.gduf.pojo.community.VoteFirstComment;
import com.gduf.pojo.community.VoteSecondComment;
import com.gduf.pojo.community.VoteWithChoice;
import com.gduf.service.VoteService;
import com.gduf.util.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gduf.controller.Code.*;

@RestController
@RequestMapping("/community/vote")
public class VoteController {

    private static Integer LATEST_TERM = 0;

    @Autowired
    private VoteService voteService;

    @GetMapping
    public Result showVote() {
        VoteWithChoice voteWithChoice = voteService.showVoteByTerm(LATEST_TERM);
        if (Objects.isNull(voteWithChoice) || Objects.isNull(voteWithChoice.getVote())) {
            return new Result("最新一期投票读取失败", SHOW_VOTE_ERR, null);
        } else return new Result("最新一期投票读取成功", SHOW_VOTE_OK, voteWithChoice);
    }

    @PostMapping
    public Result voteAction(@RequestBody Map map,@RequestHeader String token) {
        Integer voteId = (Integer) map.get("voteId");
        Boolean opinion = (Boolean) map.get("opinion");
        boolean isVote = voteService.voteAction(voteId, opinion,token);
        if (isVote)
            return new Result("投票成功", VOTE_OK, null);
        return new Result("投票失败", VOTE_ERR, null);
    }

    @PostMapping("/comment")
    public Result showSecondVoteComments(@RequestBody Map map) {
        Integer firstVoteCommentId = (Integer) map.get("firstVoteCommentId");
        List<TreeNode<VoteSecondComment>> treeNodes;
        try {
            treeNodes = voteService.showExtraSecondVoteComment(firstVoteCommentId);
        } catch (Exception e) {
            return new Result("显示二级评论失败", SHOW_VOTE_COMMENT_ERR, null);
        }
        return new Result("显示二级评论成功 或没有二级评论", SHOW_VOTE_COMMENT_OK, treeNodes);
    }

    @PostMapping("/comment/write")
    public Result insertFirstComment(@RequestBody VoteFirstComment voteFirstComment) {
        boolean isInsert = voteService.insertFirstComment(voteFirstComment);
        if (!isInsert)
            return new Result("添加评论失败", INSERT_VOTE_FIRST_COMMENT_ERR, null);
        return new Result("添加评论成功", INSERT_VOTE_FIRST_COMMENT_OK, null);
    }

    @PostMapping("/comment/reply")
    public Result insertSecondComment(@RequestBody VoteSecondComment voteSecondComment) {
        boolean isInsert = voteService.insertSecondComment(voteSecondComment);
        if (!isInsert)
            return new Result("添加评论失败", INSERT_VOTE_FIRST_COMMENT_ERR, null);
        return new Result("添加评论成功", INSERT_VOTE_FIRST_COMMENT_OK, null);
    }
}
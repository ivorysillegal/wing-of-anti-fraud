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
        VoteWithChoice voteWithChoice = voteService.showVote(LATEST_TERM);
        if (Objects.isNull(voteWithChoice) || Objects.isNull(voteWithChoice.getVote())) {
            return new Result("最新一期投票读取成功", SHOW_VOTE_OK, voteWithChoice);
        } else return new Result("最新一期投票读取成功", SHOW_VOTE_ERR, null);
    }

    @PostMapping
    public Result voteAction(@RequestBody Map map) {
        Integer voteId = (Integer) map.get("voteId");
        Boolean opinion = (Boolean) map.get("opinion");
        boolean isVote = voteService.voteAction(voteId, opinion);
        if (isVote)
            return new Result("投票成功", VOTE_OK, null);
        return new Result("投票失败", VOTE_ERR, null);
    }

    @PostMapping
    public Result showSecondVoteComments(@RequestBody Map map) {
        Integer parentCommentId = (Integer) map.get("parentCommentId");
        List<TreeNode<VoteSecondComment>> treeNodes;
        try {
            treeNodes = voteService.showExtraSecondVoteComment(parentCommentId);
        } catch (Exception e) {
            return new Result("显示二级评论失败", SHOW_VOTE_COMMENT_ERR, null);
        }
        return new Result("显示二级评论成功 或没有二级评论", SHOW_VOTE_COMMENT_OK, treeNodes);
    }

    @PostMapping("/comment")
    public Result insertFirstComment(@RequestBody VoteFirstComment voteFirstComment) {
        boolean isInsert = voteService.insertFirstComment(voteFirstComment);
        if (!isInsert)
            return new Result("添加评论失败", INSERT_VOTE_FIRST_COMMENT_ERR, null);
        return new Result("添加评论成功", INSERT_VOTE_FIRST_COMMENT_OK, null);
    }

    @PostMapping("/reply")
    public Result insertSecondComment(@RequestBody VoteSecondComment voteSecondComment) {
        boolean isInsert = voteService.insertSecondComment(voteSecondComment);
        if (!isInsert)
            return new Result("添加评论失败", INSERT_VOTE_FIRST_COMMENT_ERR, null);
        return new Result("添加评论成功", INSERT_VOTE_FIRST_COMMENT_OK, null);
    }
}
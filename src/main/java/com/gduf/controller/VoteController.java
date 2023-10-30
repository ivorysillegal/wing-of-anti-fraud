package com.gduf.controller;

import com.gduf.pojo.community.VoteWithChoice;
import com.gduf.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
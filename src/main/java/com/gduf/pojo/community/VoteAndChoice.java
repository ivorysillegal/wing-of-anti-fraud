package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteAndChoice {
    private Integer voteId;
    private String topic;
    private String main;
    private Integer term;
    private Date createTime;
    private Integer comments;
    private VoteChoice voteChoice1;
    private VoteChoice voteChoice2;


    public VoteAndChoice(Vote vote, VoteChoice voteChoice1, VoteChoice voteChoice2) {
        this.voteId = vote.getVoteId();
        this.topic = vote.getTopic();
        this.main = vote.getMain();
        this.term = vote.getTerm();
        this.createTime = vote.getCreateTime();
        this.voteChoice1 = voteChoice1;
        this.voteChoice2 = voteChoice2;
    }
}

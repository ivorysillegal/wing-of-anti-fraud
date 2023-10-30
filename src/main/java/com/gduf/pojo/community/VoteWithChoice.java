package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteWithChoice {
    private Vote vote;
    private VoteChoice voteChoice1;
    private VoteChoice voteChoice2;
}



package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteChoice {
    private Integer voteChoiceId;
    private Integer voteId;
    private String msg;
    private Integer approve;
    private Boolean opinion;
}

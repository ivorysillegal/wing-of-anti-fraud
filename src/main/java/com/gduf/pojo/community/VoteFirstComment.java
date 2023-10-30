package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteFirstComment {
    private Integer voteCommentId;
    private String msg;
    private Integer voteId;
    private Boolean opinion;
    private Integer writerId;
}

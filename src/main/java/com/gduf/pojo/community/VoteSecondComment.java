package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteSecondComment {
    private Integer secondVoteCommentId;
    private String msg;
    private Integer parentCommentId;
    private Boolean opinion;
    private Integer writerId;
    private Integer firstVoteCommentId;
}

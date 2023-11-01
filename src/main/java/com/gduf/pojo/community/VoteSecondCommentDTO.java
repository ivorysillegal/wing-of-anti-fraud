package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteSecondCommentDTO {
    private VoteSecondComment comment;
//    private List<VoteSecondCommentDTO> voteSecondComments;
    private List<VoteSecondCommentDTO> children;
}

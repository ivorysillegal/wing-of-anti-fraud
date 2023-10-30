package com.gduf.pojo.community;

import com.gduf.util.TreeNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteComment {
    private VoteFirstComment voteFirstComment;
//    private List<VoteSecondComment> voteSecondComments;
    private TreeNode<VoteSecondComment> voteSecondComments;
}

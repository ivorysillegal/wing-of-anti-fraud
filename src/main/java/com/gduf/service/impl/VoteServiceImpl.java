package com.gduf.service.impl;

import com.gduf.dao.VoteDAO;
import com.gduf.pojo.community.*;
import com.gduf.service.VoteService;
import com.gduf.util.HashTree;
import com.gduf.util.Tree;
import com.gduf.util.TreeNode;
import com.gduf.util.TreeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteDAO voteDAO;

    @Override
    public VoteWithChoice showVote(Integer id) {
        Vote vote = voteDAO.showVoteById(id);
        VoteChoice voteChoice1 = voteDAO.showVoteChoiceById(vote.getVoteChoice1());
        VoteChoice voteChoice2 = voteDAO.showVoteChoiceById(vote.getVoteChoice2());
        VoteWithChoice voteWithChoice = new VoteWithChoice(vote, voteChoice1, voteChoice2);
        return voteWithChoice;
    }

    @Override
    public boolean voteAction(Integer voteId, Boolean opinion) {
        try {
            voteDAO.voteAction(voteId, opinion);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<VoteWithChoice> showNewestVote() {
        List<Vote> votes = voteDAO.showNewestVote();
        List<VoteWithChoice> voteWithChoices = new ArrayList<>();
        for (Vote vote : votes) {
            VoteChoice voteChoice1 = voteDAO.showVoteChoiceById(vote.getVoteChoice1());
            VoteChoice voteChoice2 = voteDAO.showVoteChoiceById(vote.getVoteChoice2());
            VoteWithChoice voteWithChoice = new VoteWithChoice(vote, voteChoice1, voteChoice2);
            voteWithChoices.add(voteWithChoice);
        }
        return voteWithChoices;
    }

    @Override
    public List<VoteComment> showVoteComment(Integer voteId) {
//        List<VoteFirstComment> voteFirstComments = voteDAO.showFirstVoteCommentById(voteId);
//        ArrayList<VoteComment> voteComments = new ArrayList<>();
//        for (VoteFirstComment voteFirstComment : voteFirstComments) {
//            List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(voteFirstComment.getVoteCommentId());
//            VoteComment voteComment = new VoteComment(voteFirstComment, voteSecondComments);
//            voteComments.add(voteComment);
//        }
//        return voteComments;


//        不知道能不能跑的
        List<VoteFirstComment> voteFirstComments = voteDAO.showFirstVoteCommentById(voteId);
        List<VoteComment> voteComments = new ArrayList<>();
        for (VoteFirstComment voteFirstComment : voteFirstComments) {
            List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(voteFirstComment.getVoteCommentId());
            Function<VoteSecondComment, Integer> idGetter = VoteSecondComment::getParentCommentId;
            TreeNode<VoteSecondComment> secondCommentTreeNode = TreeUtils.createTree(voteSecondComments, idGetter);
            voteComments.add(new VoteComment(voteFirstComment, secondCommentTreeNode));
        }

//        HashTree<VoteSecondComment> hashTree = new HashTree<>();
//        for (VoteFirstComment voteFirstComment : voteFirstComments) {
//            List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(voteFirstComment.getVoteCommentId());
//            for (VoteSecondComment voteSecondComment : voteSecondComments) {
//                if (voteSecondComment.getParentCommentId() == 0) {
//                    hashTree.add(voteSecondComment);
//                }
//            }
//        }


        return voteComments;
    }

    @Override
    public List<VoteFirstComment> showFirstVoteComment(Integer voteId) {
        List<VoteFirstComment> voteFirstComments = voteDAO.showFirstVoteCommentById(voteId);
        return voteFirstComments;
    }

//    @Override
//    public TreeNode<VoteSecondComment> showExtraSecondVoteComment(Integer firstVoteCommentId) {
//        List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(firstVoteCommentId);
//        Function<VoteSecondComment, Integer> idGetter = voteSecondComment -> firstVoteCommentId;
//        TreeNode<VoteSecondComment> secondCommentTreeNode = TreeUtils.createTree(voteSecondComments, idGetter);
//        return secondCommentTreeNode;
//    }

    @Override
    public List<TreeNode<VoteSecondComment>> showExtraSecondVoteComment(Integer parentId) {
        //        获取特定一级评论的所有二级评论 集成集合
        List<TreeNode<VoteSecondComment>> voteSecondCommentsNodes = new ArrayList<>();
        List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(parentId);
//        将每一个二级评论全部组织 初始化成一个节点
        for (VoteSecondComment voteSecondComment : voteSecondComments) {
            TreeNode<VoteSecondComment> voteSecondCommentsNode = new TreeNode<>(voteSecondComment);
            voteSecondCommentsNodes.add(voteSecondCommentsNode);
        }
        return TreeUtils.findParent(voteSecondCommentsNodes);
    }

    @Override
    public boolean insertFirstComment(VoteFirstComment voteFirstComment) {
        try {
            voteDAO.insertFirstComment(voteFirstComment);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean insertSecondComment(VoteSecondComment voteSecondComment) {
        try {
            voteDAO.insertSecondComment(voteSecondComment);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}



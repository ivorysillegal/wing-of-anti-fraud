package com.gduf.service.impl;

import com.gduf.dao.VoteDAO;
import com.gduf.pojo.community.*;
import com.gduf.pojo.user.User;
import com.gduf.service.VoteService;
import com.gduf.util.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteDAO voteDAO;
    @Autowired
    private RedisCache redisCache;

    @Override
    public VoteWithChoice showVoteByTerm(Integer term) {
        Vote vote = voteDAO.showVoteByTerm(term);
        VoteChoice voteChoice1;
        VoteChoice voteChoice2;
        try {
            voteChoice1 = voteDAO.showVoteChoiceById(vote.getVoteChoice1());
            voteChoice2 = voteDAO.showVoteChoiceById(vote.getVoteChoice2());
        } catch (NullPointerException e) {
            return null;
        }
        VoteWithChoice voteWithChoice = new VoteWithChoice(vote, voteChoice1, voteChoice2);
        return voteWithChoice;
    }

    @Override
    public Integer voteAction(Integer voteId, Boolean opinion, String token) {
        try {
            int userId = decodeToId(token);
            Integer isVote = voteDAO.checkIfVote(voteId, userId);
//            如果不是空的 则证明已经投过票了
//            此次投票操作失败
            if (isVote.equals(1)) return 0;
            voteDAO.voteAction(voteId, opinion);
            voteDAO.insertVote(voteId, userId, opinion);
        } catch (Exception e) {
            return -1;
        }
        return 1;
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
    public List<VoteAndChoice> showLastVote() {
        List<Vote> votes = voteDAO.showAllLastVote();
        ArrayList<VoteAndChoice> voteAndChoices = new ArrayList<>();
        for (Vote vote : votes) {
            VoteChoice voteChoice1 = voteDAO.showVoteChoiceById(vote.getVoteChoice1());
            VoteChoice voteChoice2 = voteDAO.showVoteChoiceById(vote.getVoteChoice2());
            voteAndChoices.add(new VoteAndChoice(vote, voteChoice1, voteChoice2));
        }
        return voteAndChoices;
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
//        List<VoteFirstComment> voteFirstComments = voteDAO.showFirstVoteCommentById(voteId);
//        List<VoteComment> voteComments = new ArrayList<>();
//        for (VoteFirstComment voteFirstComment : voteFirstComments) {
////            List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(voteFirstComment.getVoteCommentId());
//            Function<VoteSecondComment, Integer> idGetter = VoteSecondComment::getParentCommentId;
//            TreeNode<VoteSecondComment> secondCommentTreeNode = TreeUtils.createTree(voteSecondComments, idGetter);
//            voteComments.add(new VoteComment(voteFirstComment, secondCommentTreeNode));
//        }

//        HashTree<VoteSecondComment> hashTree = new HashTree<>();
//        for (VoteFirstComment voteFirstComment : voteFirstComments) {
//            List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(voteFirstComment.getVoteCommentId());
//            for (VoteSecondComment voteSecondComment : voteSecondComments) {
//                if (voteSecondComment.getParentCommentId() == 0) {
//                    hashTree.add(voteSecondComment);
//                }
//            }
//        }


//        return voteComments;
        return null;
    }

    @Override
    public List<VoteFirstComment> showFirstVoteComment(Integer voteId) {
        List<VoteFirstComment> voteFirstComments;
        try {
            voteFirstComments = voteDAO.showFirstVoteCommentById(voteId);
        } catch (Exception e) {
            return null;
        }
        return voteFirstComments;
    }

    @Override
    public List<VoteUser> showMyVoted(String token) {
        List<VoteUser> voteUsers;
        try {
            int userId = decodeToId(token);
            voteUsers = voteDAO.showVoted(userId);
        } catch (Exception e) {
            return null;
        }
        return voteUsers;
    }

//    @Override
//    public TreeNode<VoteSecondComment> showExtraSecondVoteComment(Integer firstVoteCommentId) {
//        List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(firstVoteCommentId);
//        Function<VoteSecondComment, Integer> idGetter = voteSecondComment -> firstVoteCommentId;
//        TreeNode<VoteSecondComment> secondCommentTreeNode = TreeUtils.createTree(voteSecondComments, idGetter);
//        return secondCommentTreeNode;
//    }

    @Override
    public List<VoteSecondComment> showExtraSecondVoteComment(Integer firstVoteCommentId) {
//    public List<TreeNode<VoteSecondComment>> showExtraSecondVoteComment(Integer firstVoteCommentId) {
        //        获取特定一级评论的所有二级评论 集成集合
//        List<TreeNode<VoteSecondComment>> voteSecondCommentsNodes = new ArrayList<>();
        List<VoteSecondComment> voteSecondComments = voteDAO.showSecondCommentByFirstId(firstVoteCommentId);
//        将每一个二级评论全部组织 初始化成一个节点
//        for (VoteSecondComment voteSecondComment : voteSecondComments) {
//            TreeNode<VoteSecondComment> voteSecondCommentsNode = new TreeNode<>(voteSecondComment);
//            voteSecondCommentsNodes.add(voteSecondCommentsNode);
//        }
        return voteSecondComments;
//        return TreeUtils.findParent(voteSecondCommentsNodes);
    }

    @Override
    public Integer insertFirstComment(String token, VoteFirstComment voteFirstComment) {
        try {
            int writerId = decodeToId(token);
            Integer isVote = voteDAO.checkIfVote(voteFirstComment.getVoteId(), writerId);
            if (isVote.equals(0))
                return 0;
            voteFirstComment.setWriterId(writerId);
            voteDAO.insertFirstComment(voteFirstComment);
            voteDAO.updateVoteComments(voteFirstComment.getVoteId());
        } catch (Exception e) {
            return -1;
        }
        return 1;
    }

    @Override
    public Integer insertSecondComment(String token, VoteSecondComment voteSecondComment) {
        try {
            int writerId = decodeToId(token);
            Integer voteId = voteDAO.showVoteIdByCommentId(voteSecondComment.getFirstVoteCommentId());
            Integer isVote = voteDAO.checkIfVote(voteId, writerId);
            voteDAO.updateCommentReply(voteSecondComment.getFirstVoteCommentId());
            if (isVote.equals(0))
                //                如果是空的 则说明没有投过票
//                没有投过票不得评论
                return 0;
            voteSecondComment.setWriterId(writerId);
            voteDAO.insertSecondComment(voteSecondComment);
        } catch (Exception e) {
            return -1;
        }
        return 1;
    }

    private User decode(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
//            getSubject获取的是未加密之前的原始值
        User user = redisCache.getCacheObject("login" + userId);
        return user;
    }

    private int decodeToId(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
        return Integer.parseInt(userId);
    }

}



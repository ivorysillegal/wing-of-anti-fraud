package com.gduf.service.impl;

import com.gduf.dao.CommunityDAO;
import com.gduf.pojo.Post;
import com.gduf.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityDAO communityDAO;

    @Override
    public List<Post> showAllPost() {
        List<Post> posts;
        try {
            posts = communityDAO.showAllPost();
        } catch (Exception e) {
            return null;
        }
        return posts;
    }

    @Override
    public List<Post> showPostByWriter(Integer writerId) {
        List<Post> posts;
        try {
            posts = communityDAO.showPostByWriter(writerId);
        } catch (Exception e) {
            return null;
        }
        return posts;
    }

    @Override
    public boolean insertPost(Post post) {
        try {
            communityDAO.insertPost(post);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void insertLike(Integer userId, Integer postId) {
        communityDAO.insetLike(postId, userId);
        communityDAO.updateLikesInCommunity(postId);
    }

    @Override
    public void insertStar(Integer userId, Integer postId) {
        communityDAO.insertStar(postId, userId);
        communityDAO.updateStarsInCommunity(postId);
    }

    @Override
    public void insertComment(String commentMsg, Integer postId,Integer userId) {
        communityDAO.insertComment(commentMsg, postId,userId);
        communityDAO.updateCommentsInCommunity(postId);
    }

    @Override
    public void insertLikesForComment(Integer userId, Integer commentId) {
        communityDAO.insertLikesForComment(userId, commentId);
        communityDAO.updateLikesForCommentsInCommunity(commentId);
    }


}

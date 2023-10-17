package com.gduf.service;

import com.gduf.pojo.community.Post;
import com.gduf.pojo.community.PostWithComments;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CommunityService {
    public List<Post> showAllPost();

    public PostWithComments showPostById(Integer postId);

    public List<Post> showPostByWriter(Integer writerId);

    public boolean insertPost(Post post);

    public void insertLike(Integer userId,Integer postId);

    public void insertStar(Integer userId,Integer postId);

    public void insertComment(String commentMsg,Integer postId,Integer userId);

    public void insertLikesForComment(Integer userId,Integer postId);
}

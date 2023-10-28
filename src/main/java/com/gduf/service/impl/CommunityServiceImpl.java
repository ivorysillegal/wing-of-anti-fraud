package com.gduf.service.impl;

import com.gduf.dao.CommunityDAO;
import com.gduf.pojo.community.*;
import com.gduf.pojo.user.User;
import com.gduf.service.CommunityService;
import com.gduf.util.JwtUtil;
import com.gduf.util.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityDAO communityDAO;

    @Autowired
    private RedisCache redisCache;

    @Override
    public List<Post> showAllPost() {
        List<Post> posts;
        posts = communityDAO.showAllPost();
        return posts;
    }

    @Override
    public PostWithComments showPostById(Integer postId) {
        Post post = communityDAO.showPostById(postId);
        List<Comment> comments = communityDAO.showEachPostComment(postId);
        return new PostWithComments(post, comments);
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

    //    根据标签查找帖子
    @Override
    public List<Post> showPostByTag(PostAbout postAbout) {
        PostTheme postTheme = postAbout.getPostTheme();
        PostTopic postTopic = postAbout.getPostTopic();
        List<Integer> postIdByTopic = communityDAO.showPostIdByTopic(postTopic);
        List<Integer> postIdByTheme = communityDAO.showPostIdByTheme(postTheme);
        boolean hasPost = postIdByTheme.retainAll(postIdByTopic);
//        retainAll方法 保留两个集合中的交集 返回值为是否含有交集
//        if (!hasPost)
//            return null;
        Iterator<Integer> postIterator = postIdByTheme.iterator();
        List<Post> posts = new ArrayList<>();
        while (postIterator.hasNext()) {
            Post post = communityDAO.showPostById(postIterator.next());
            posts.add(post);
        }
        return posts;
    }

    @Override
    public boolean insertPost(Post post, String token) {
        try {
//            User user = JwtUtil.decode(token);
            User user = decode(token);
            post.setWriterId(user.getUserId());
            communityDAO.insertPost(post);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean insertPostAbout(PostAbout postAbout) {
        try {
            Integer postId = postAbout.getPostId();
            PostTheme postTheme = postAbout.getPostTheme();
            postTheme.setPostId(postId);
            PostTopic postTopic = postAbout.getPostTopic();
            postTopic.setPostId(postId);
            boolean isUpdate = updateValue(postTopic, postTheme);
            if (!isUpdate)
                return false;
            communityDAO.insertPostTheme(postTheme);
            communityDAO.insertPostTopic(postTopic);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean updateValue(Object o1, Object o2) {
        return updateValue(o1) &&
                updateValue(o2);
    }

    private static boolean updateValue(Object o) {
        Class<?> clazz = o.getClass();
        Field[] themeFields = clazz.getDeclaredFields();
        for (Field field : themeFields) {
            field.setAccessible(true);
            try {
                if ((Integer) field.get(o) == 0) {
                    field.set(o, -1);
                }
            } catch (IllegalAccessException e) {
                return false;
            }
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
    public void insertComment(String commentMsg, Integer postId, Integer userId) {
        communityDAO.insertComment(commentMsg, postId, userId);
        communityDAO.updateCommentsInCommunity(postId);
    }

    @Override
    public void insertLikesForComment(Integer userId, Integer commentId) {
        communityDAO.insertLikesForComment(userId, commentId);
        communityDAO.updateLikesForCommentsInCommunity(commentId);
    }

    private User decode(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
//            getSubject获取的是未加密之前的原始值
        User user = redisCache.getCacheObject("login" + userId);
        return user;
    }

}

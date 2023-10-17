package com.gduf.controller;

import com.gduf.pojo.community.Post;
import com.gduf.pojo.community.PostWithComments;
import com.gduf.pojo.user.User;
import com.gduf.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.gduf.controller.Code.*;

@RestController
@RequestMapping("/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;


    private User user = new User("1", "1");

    //    社区首页渲染帖子
    @GetMapping
    public Result indexPost() {
        List<Post> posts;
        try {
            posts = communityService.showAllPost();
        } catch (Exception e) {
            return new Result("获取帖子失败", SHOW_POST_ERR, null);
        }
        return new Result("获取帖子成功", SHOW_POST_OK, posts);
    }

    //    根据作者找帖子
    @GetMapping("/search")
    public Result searchPostByWriter(@RequestBody Integer writerId) {
        List<Post> posts;
        try {
            posts = communityService.showPostByWriter(writerId);
        } catch (Exception e) {
            return new Result("获取帖子失败", SHOW_POST_ERR, null);
        }
        if (posts == null || posts.isEmpty()) {
            return new Result("此作者没有发布过帖子", SHOW_POST_NULL, null);
        }
        return new Result("获取帖子成功", SHOW_POST_OK, posts);
    }


    //    作者上传自己的帖子
    @PostMapping("/write")
    public Result writePost(@RequestBody Post post) {
        post.setWriterId(user.getUserId());
        if (communityService.insertPost(post)) {
            return new Result("上传帖子成功", INSERT_POST_OK, null);
        } else return new Result("上传帖子失败", INSERT_POST_ERR, null);
    }

    //    点赞功能
    @PostMapping("/like")
    public Result like(@RequestBody Map<String, Integer> likes) {
//    Map的键和值可以是postId和userId
        try {
            Integer postId = likes.get("postId");
//            Integer userId = likes.get("userId");
            user.setUserId(38);
            Integer userId = user.getUserId();
            communityService.insertLike(userId, postId);
        } catch (Exception e) {
            return new Result("点赞失败", COMMUNITY_LIKE_ERR, null);
        }
        return new Result("点赞成功", COMMUNITY_LIKE_OK, null);
    }

    //    收藏功能
    @PostMapping("/star")
    public Result star(@RequestBody Map<String, Integer> stars) {
//    Map的键和值可以是postId和userId
        try {
            Integer postId = stars.get("postId");
            user.setUserId(38);
            Integer userId = user.getUserId();
//            Integer userId = stars.get("userId");
            communityService.insertStar(userId, postId);
        } catch (Exception e) {
            return new Result("收藏失败", COMMUNITY_STAR_ERR, null);
        }
        return new Result("收藏成功", COMMUNITY_STAR_OK, null);
    }

    //    评论功能
    @PostMapping("/comment")
    public Result comment(@RequestBody LinkedHashMap comments) {
//    Map的键和值可以是commentMsg和userId和postId
        try {
            String commentMsg = (String) comments.get("commentMsg");
            Integer userId = user.getUserId();
//            Integer userId = (Integer) comments.get("userId");
            user.setUserId(38);
            Integer postId = (Integer) comments.get("postId");
            communityService.insertComment(commentMsg, postId, userId);
        } catch (Exception e) {
            return new Result("评论失败", COMMUNITY_COMMENT_ERR, null);
        }
        return new Result("评论成功", COMMUNITY_COMMENT_OK, null);
    }

    //    点赞评论
    @PostMapping("/comment/like")
    public Result likesForComment(@RequestBody Map<String, Integer> likesComment) {
        try {
//            Integer userId = likesComment.get("userId");
            user.setUserId(38);
            Integer userId = user.getUserId();
            Integer postId = likesComment.get("postId");
            communityService.insertLikesForComment(userId, postId);
        } catch (Exception e) {
            return new Result("点赞评论失败", COMMUNITY_COMMENT_LIKE_ERR, null);
        }
        return new Result("点赞评论成功", COMMUNITY_COMMENT_LIKE_OK, null);
    }

    @PostMapping("/main")
    public Result showPost(@RequestBody Map<String,Integer> map) {
        Integer postId = map.get("postId");
        PostWithComments postWithComments;
        try {
            postWithComments = communityService.showPostById(postId);
        } catch (Exception e) {
            return new Result("读取帖子主要内容失败", SHOW_POST_MAIN_ERR, null);
        } return new Result("查看帖子主要内容成功", SHOW_POST_MAIN_OK, postWithComments);
    }
}
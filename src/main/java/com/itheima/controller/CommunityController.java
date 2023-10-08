package com.itheima.controller;

import com.itheima.pojo.Post;
import com.itheima.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.itheima.controller.Code.*;

@RestController
@RequestMapping("/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;

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
        if (communityService.insertPost(post)) {
            return new Result("上传帖子成功", INSERT_POST_OK, null);
        } else return new Result("上传帖子失败", INSERT_POST_ERR, null);
    }

//    点赞功能
   @PostMapping("/likes")
   public Result like(@RequestBody Map<Integer,Integer> likes){
//    Map的键和值可以是postId和userId


   }
}
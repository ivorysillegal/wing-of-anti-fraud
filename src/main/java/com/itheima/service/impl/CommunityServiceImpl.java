package com.itheima.service.impl;

import com.itheima.dao.CommunityDAO;
import com.itheima.pojo.Post;
import com.itheima.service.CommunityService;
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
}

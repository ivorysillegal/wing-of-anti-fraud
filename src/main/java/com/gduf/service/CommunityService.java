package com.gduf.service;

import com.gduf.pojo.Post;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CommunityService {
    public List<Post> showAllPost();

    public List<Post> showPostByWriter(Integer writerId);

    public boolean insertPost(Post post);
}

package com.gduf.service;

import com.gduf.pojo.community.Comment;
import com.gduf.pojo.community.Post;
import com.gduf.pojo.user.UserValue;
import com.gduf.pojo.user.UserWithValue;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService {
    String login(String username, String password);

    int register(String username, String password, String email);

    boolean picUpload(String base64ImageData, String token);

    UserWithValue showUser(String token);

    boolean updateUser(UserValue userValue, String token);

    void sendMsg(String to, String subject, String context, String code);

    boolean verifyAccount(String username, String beforePassword, String afterPassword);

    UserWithValue showUser(Integer userId);

    List<Post> showLikePost(String token);

    List<Comment> showMyComment(String token);

    List<Post> showMyPost(String token);
}

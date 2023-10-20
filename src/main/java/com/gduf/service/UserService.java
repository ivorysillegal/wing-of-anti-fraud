package com.gduf.service;

import com.gduf.pojo.user.UserValue;
import com.gduf.pojo.user.UserWithValue;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
    String login(String username, String password);

    int register(String username, String password, String email);

    boolean picUpload(String base64ImageData, String token);

    UserWithValue showUser(String token);

    boolean updateUser(UserValue userValue, String token);

    void sendMsg(String to, String subject, String context, String code);

    boolean verifyAccount(String username, String beforePassword, String afterPassword);
}

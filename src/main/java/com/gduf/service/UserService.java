package com.gduf.service;

import com.gduf.pojo.user.ImageUploadRequest;
import com.gduf.pojo.user.UserValue;
import com.gduf.pojo.user.UserWithValue;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
    String login(String username, String password);

    int register(String username, String password);

    void picUpload(ImageUploadRequest imageUploadRequest);

    UserWithValue showUser(String token);

    boolean updateUser(UserValue userValue,String token);
}

package com.gduf.service;

import com.gduf.pojo.user.ImageUploadRequest;
import com.gduf.pojo.user.UserValue;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
    String login(String username, String password);

    int register(String username, String password);

    boolean picUpload(ImageUploadRequest imageUploadRequest);

    UserValue showUser();

    boolean updateUser(UserValue userValue);
}

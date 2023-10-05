package com.itheima.service;

import com.itheima.pojo.user.ImageUploadRequest;
import com.itheima.pojo.user.User;
import com.itheima.pojo.user.UserValue;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
    boolean login(String username, String password);

    int register(String username, String password);

    boolean picUpload(ImageUploadRequest imageUploadRequest);

    UserValue showUser();

    boolean updateUser(UserValue userValue);
}

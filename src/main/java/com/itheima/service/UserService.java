package com.itheima.service;

import com.itheima.pojo.user.ImageUploadRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
    boolean login(String username,String password);

    int register(String username,String password);

    boolean picUpload(ImageUploadRequest imageUploadRequest);
}

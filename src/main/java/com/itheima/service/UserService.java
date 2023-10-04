package com.itheima.service;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
    boolean login(String username,String password);

    boolean register(String username,String password);


}

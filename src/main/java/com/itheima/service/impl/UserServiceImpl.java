package com.itheima.service.impl;

import com.itheima.dao.UserDAO;
import com.itheima.pojo.user.User;
import com.itheima.pojo.user.UserValue;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    private User user;
//    public static User user;
//    仅为测试所用

    //    登录
    @Override
    public boolean login(String username, String password) {
//        先判断此用户名的用户是否存在
        this.user = userDAO.getByUsername(username);
        if (user != null)
            return user.getPassword().equals(password);
//        确认用户存在后 确认密码是否正确
        return false;
    }

    //    注册
    public boolean register(String username, String password) {
        User user = userDAO.getByUsername(username);
        if (user != null)
            return false;
        try {
            userDAO.insertBasic(new User(username, password));
            userDAO.insertValue(new UserValue());
//            创建用户个人信息 补充默认信息
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}




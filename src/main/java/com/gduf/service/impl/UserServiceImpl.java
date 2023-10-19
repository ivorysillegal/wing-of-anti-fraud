package com.gduf.service.impl;

import com.gduf.dao.UserDAO;
import com.gduf.pojo.user.User;
import com.gduf.pojo.user.UserValue;
import com.gduf.pojo.user.UserWithValue;
import com.gduf.service.UserService;
import com.gduf.util.JwtUtil;
import com.gduf.util.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    //    @Autowired
    private final RedisCache redisCache;

    @Autowired
    public UserServiceImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    //    登录
    @Override
    public String login(String username, String password) {
        User user = userDAO.getByUsername(username);
        if (user != null) {
            if (!user.getPassword().equals(password)) {
                return null;
            }
            Integer userId = user.getUserId();
//        生成token
            String jwt = JwtUtil.createJWT(String.valueOf(userId));
//        存入redis
            redisCache.setCacheObject("login" + userId, user);
            return jwt;
        } else return null;
    }

    //    注册
    public int register(String username, String password) {
        User user = userDAO.getByUsername(username);
        if (user != null)
            return -1;
        try {
            userDAO.insertBasic(new User(username, password));
//            userDAO.insertValue(new UserValue());
//            上面这一行 暂且没用
//            创建用户个人信息 补充默认信息
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean picUpload(String base64ImageData, String token) {
        User user;
        try {
            user = decode(token);
            userDAO.updatePic(user.getUserId(), base64ImageData);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public UserWithValue showUser(String token) {
        UserValue userValue = null;
        User user;
        try {
            user = decode(token);
        } catch (Exception e) {
            return null;
        }
        userValue = userDAO.getValueById(user.getUserId());
        return new UserWithValue(user, userValue);
    }

    @Override
    public boolean updateUser(UserValue userValue, String token) {
        try {
            User user = decode(token);
            userDAO.UpdateUserValue(userValue, user.getUserId());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private User decode(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
//            getSubject获取的是未加密之前的原始值
        User user = redisCache.getCacheObject("login" + userId);
        return user;
    }
}




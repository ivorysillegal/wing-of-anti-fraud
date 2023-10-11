package com.gduf.service.impl;

import com.gduf.dao.UserDAO;
import com.gduf.pojo.user.ImageUploadRequest;
import com.gduf.pojo.user.User;
import com.gduf.pojo.user.UserValue;
import com.gduf.service.UserService;
import com.gduf.util.TokenBasedAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    protected static User user;
//    public static User user;
//    仅为测试所用

    //    登录
    @Override
    public String login(String username, String password) {
//        先判断此用户名的用户是否存在
        user = userDAO.getByUsername(username);
        if (user != null) {
            boolean equals = user.getPassword().equals(password);
            if (!equals)
                return null;
            //        确认用户存在后 确认密码是否正确
        }
        String token = TokenBasedAuthentication.generateToken();
        return token;
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
    public boolean picUpload(ImageUploadRequest request) {
        try {
            String fileName = request.getFileName();
            String base64ImageData = request.getFile();
            byte[] imageData = Base64Utils.decodeFromString(base64ImageData);

//        // 获取绝对路径
            String parent = new File("static/images").getAbsolutePath();
            File dir = new File(parent);
            if (!dir.exists()) {
                dir.mkdirs(); // 创建当前的目录
            }

            String[] split = fileName.split("\\.");
            String fileExtension = split[1];
            String newFileName = UUID.randomUUID().toString().toUpperCase() + "." + fileExtension;
            File dest = new File(dir, newFileName);

            try (OutputStream os = new FileOutputStream(dest)) {
                os.write(imageData);
            } catch (IOException e) {
                return false;
            }
            String avatar = "../images/wms/" + newFileName;
            Integer userId = user.getUserId();
//        UserValue userValue = userDAO.getValueById(userId);
            userDAO.updatePic(userId, avatar);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public UserValue showUser() {
        UserValue userValue;
        try {
            userValue = userDAO.getValueById(user.getUserId());
        } catch (Exception e) {
            return null;
        }
        return userValue;
    }

    @Override
    public boolean updateUser(UserValue userValue) {
        try {
            userDAO.UpdateUserValue(userValue);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}




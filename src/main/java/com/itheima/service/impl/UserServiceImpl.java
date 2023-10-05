package com.itheima.service.impl;

import com.itheima.dao.UserDAO;
import com.itheima.pojo.user.ImageUploadRequest;
import com.itheima.pojo.user.User;
import com.itheima.pojo.user.UserValue;
import com.itheima.service.UserService;
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
    public boolean login(String username, String password) {
//        先判断此用户名的用户是否存在
        this.user = userDAO.getByUsername(username);
        if (user != null)
            return user.getPassword().equals(password);
//        确认用户存在后 确认密码是否正确
        return false;
    }

    //    注册
    public int register(String username, String password) {
        User user = userDAO.getByUsername(username);
        if (user != null)
            return -1;
        try {
            userDAO.insertBasic(new User(username, password));
            userDAO.insertValue(new UserValue());
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




package com.gduf.service.impl;

import com.gduf.dao.CommunityDAO;
import com.gduf.dao.ScriptDAO;
import com.gduf.dao.UserDAO;
import com.gduf.pojo.community.Comment;
import com.gduf.pojo.community.Post;
import com.gduf.pojo.script.ScriptMsg;
import com.gduf.pojo.user.User;
import com.gduf.pojo.user.UserValue;
import com.gduf.pojo.user.UserWithValue;
import com.gduf.service.UserService;
import com.gduf.util.JwtUtil;
import com.gduf.util.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CommunityDAO communityDAO;
    @Autowired
    private ScriptDAO scriptDAO;

    //        @Autowired
    private final RedisCache redisCache;

    @Value("${spring.mail.username}")
    private String from;   // 邮件发送人

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public UserServiceImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    //    登录
    @Override
    public String login(String username, String password) {
        User user = userDAO.getByUsername(username);
        if (user != null) {
//            if (!user.getPassword().equals(JwtUtil.createJWT(password))) {
            if (!user.getPassword().equals(password))
                return null;

            Integer userId = user.getUserId();
//        生成token
            String jwt = JwtUtil.createJWT(String.valueOf(userId));
//        存入redis
            redisCache.setCacheObject("login" + userId, user);
            return jwt;
        } else return null;
    }

    //    注册
    public int register(String username, String password, String email) {
        User user = userDAO.getByUsername(username);
//        password = JwtUtil.createJWT(password);
        if (user != null)
            return -1;
        try {
            User newUser = new User(username, password);
            userDAO.insertBasic(newUser);
            userDAO.initializationUserValue(newUser.getUserId(), email);
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
        int userId;
        try {
            user = decode(token);
            if (user != null) {
                userId = user.getUserId();
            } else {
                userId = decodeToId(token);
            }
//            if (userDAO.getValueById(userId) == null) {
//                userDAO.initializationUserValue(userId);
//            }
            userDAO.updatePic(userId, base64ImageData);
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

    //    更新个人信息
    @Override
    public boolean updateUser(UserValue userValue, String token) {
        try {
            User user = decode(token);
//            if (Objects.isNull(userDAO.getValueById(user.getUserId())))
//                userDAO.initializationUserValue(user.getUserId());
            userDAO.UpdateUserValue(userValue.getSign(), userValue.getAge(), userValue.getGender(), user.getUserId());
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

    private int decodeToId(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
        return Integer.parseInt(userId);
    }

    //    发送邮件
//    @Override
//    public void sendMsg(String to, String subject, String context, String code) {
    private void sendMsg(String to, String subject, String context, String code) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(context);
        // 真正的发送邮件操作，从 from到 to
        mailSender.send(mailMessage);
        redisCache.setCacheObject(to, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public boolean userInitialization(String email, String code) {
        try {
            String subject = "反诈通注册验证码";
            String context = "欢迎使用反诈通，登录验证码为: " + code + ",五分钟内有效，请妥善保管!";
            sendMsg(email, subject, context, code);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean resetPasswordCode(String email, String code) {
        try {
            String subject = "反诈通重置密码 注册码";
            String context = "欢迎使用反诈通，重置密码验证码为: " + code + ",五分钟内有效，请妥善保管!";
            sendMsg(email, subject, context, code);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean resetPassword(String token, String passwordReset) {
        int userId;
        try {
            userId = decodeToId(token);
            userDAO.resetPassword(userId, passwordReset);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //    重置密码
    @Override
    public boolean verifyAccount(String token, String beforePassword, String afterPassword) {
        try {
            User user = decode(token);
            if (!Objects.isNull(user))
                if (user.getPassword().equals(beforePassword))
                    userDAO.updatePassword(afterPassword, user.getUserId());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //    展示个人详细信息
    @Override
    public UserWithValue showUser(Integer userId) {
        UserWithValue userWithValue = redisCache.getCacheObject(userId + "userValue");
        if (userWithValue != null) {
            return userWithValue;
        } else {
            UserValue userValue = userDAO.getValueById(userId);
            String username = userDAO.getUsername(userId);
            userWithValue = new UserWithValue(new User(username, userId), userValue);
        }
        redisCache.setCacheObject(userId + "userValue", userWithValue, 10, TimeUnit.MINUTES);
        return userWithValue;
    }

    //    展示我喜欢的帖子
    @Override
    public List<Post> showLikePost(String token) {
        int userId = 0;
        try {
            userId = decodeToId(token);
        } catch (Exception e) {
            return null;
        }
        List<Integer> likePostId = communityDAO.showLikePostId(userId);
        List<Post> posts = new ArrayList<>();
        for (Integer postId : likePostId) {
            Post post = communityDAO.showPostById(postId);
            posts.add(post);
        }
        return posts;
    }

    //    展示我写的评论
    @Override
    public List<Comment> showMyComment(String token) {
        int userId = 0;
        try {
            userId = decodeToId(token);
        } catch (Exception e) {
            return null;
        }
        List<Comment> comments = redisCache.getCacheList("userComments" + userId);
        if (comments.isEmpty()) {
            comments = communityDAO.showCommentById(userId);
        }
        redisCache.setCacheList("userComments" + userId, comments, 10, TimeUnit.MINUTES);
        return comments;
    }

    //    展示我写的帖子
    @Override
    public List<Post> showMyPost(String token) {
        int userId;
        List<Post> posts;
        try {
            userId = decodeToId(token);
        } catch (Exception e) {
            return null;
        }
        posts = redisCache.getCacheList("userPosts" + userId);
        if (posts.isEmpty())
            posts = communityDAO.showPostByWriter(userId);
        redisCache.setCacheList("userPosts" + userId, posts, 10, TimeUnit.MINUTES);
        return posts;
    }

    //    展示我玩过的剧本
    @Override
    public List<ScriptMsg> showMyPlayedScript(String token) {
        int userId;
        List<Integer> playedScriptId;
        List<ScriptMsg> scripts;
        try {
            userId = decodeToId(token);
        } catch (Exception e) {
            return null;
        }
        scripts = redisCache.getCacheList("userPlayedScripts" + userId);
        if (scripts.isEmpty()) {
            playedScriptId = scriptDAO.getPlayedScriptId(userId);
            for (Integer eachScriptId : playedScriptId) {
                ScriptMsg script = scriptDAO.getScriptById(eachScriptId);
                scripts.add(script);
            }
        }
        redisCache.setCacheList("userPlayedScripts" + userId, scripts);
        return scripts;
    }

}




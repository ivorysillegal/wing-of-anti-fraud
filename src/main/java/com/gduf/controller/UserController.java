package com.gduf.controller;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.core.collection.ListUtil;
import com.gduf.pojo.CaptchaCode;
import com.gduf.pojo.community.Comment;
import com.gduf.pojo.community.Post;
import com.gduf.pojo.script.ScriptMsg;
import com.gduf.pojo.user.UserValue;
import com.gduf.pojo.user.UserWithValue;
import com.gduf.service.UserService;
import com.gduf.util.Captcha;
import com.gduf.util.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import static com.gduf.controller.Code.*;

@RestController
//@CrossOrigin
@RequestMapping("/users")
public class UserController {

    private AbstractCaptcha captcha;

    @Autowired
    private UserService userService;

    //    发送验证码
    @PostMapping("/sendMsg")
    public Result sendMsg(@RequestBody Map emailCode) {
        String email = (String) emailCode.get("email");
        String usage = (String) emailCode.get("usage");
        String subject;
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        String context;
        // 获取邮箱账号
        try {
            if (usage.equals("passwordForgotten")) {
                subject = "反诈通注册验证码";
                context = "欢迎使用反诈通，登录验证码为: " + code + ",五分钟内有效，请妥善保管!";
                userService.sendMsg(email, subject, context, code);
            } else if (usage.equals("register")) {
                subject = "反诈通重置密码 注册码";
                context = "欢迎使用反诈通，重置密码验证码为: " + code + ",五分钟内有效，请妥善保管!";
                userService.sendMsg(email, subject, context, code);
            }
        } catch (Exception e) {
            return new Result("验证码发送失败", 201, null);
        }
        return new Result("验证码发送成功", 200, code);
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String token = userService.login(username, password);
        if (token != null)
            return new Result("登录成功", LOGIN_OK, token);
        else
            return new Result("登录失败", LOGIN_ERR, null);
    }

    //    注册账号
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String email = requestBody.get("email");
        if (password == null || username == null) {
            return new Result("账号或密码为空", REGISTER_ERR, null);
        }
        int register = userService.register(username, password, email);
        if (register == 1) {
            return new Result("注册成功", REGISTER_OK, null);
        } else if (register == 0)
            return new Result("注册失败 系统繁忙", REGISTER_ERR, null);
        else
            return new Result("用户名重复 请重试！", REGISTER_REPEAT_NAME, null);
    }

    //    重置密码
    @PostMapping("update_password")
    public Result passwordForgotten(@RequestBody Map map) {
        String username = (String) map.get("username");
        String beforePassword = (String) map.get("beforePassword");
        String afterPassword = (String) map.get("afterPassword");
        if (!userService.verifyAccount(username, beforePassword, afterPassword)) {
            return new Result("密码修改成功", UPDATE_PASSWORD_OK, null);
        }
        return new Result("密码修改失败", UPDATE_PASSWORD_ERR, null);
    }

    @PostMapping("/send_captcha")
    public Result sendCaptcha() {
        CaptchaCode captchaCode;
        byte[] imageBytes;
        try {
            captcha = Captcha.createCaptcha();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            captcha.write(outputStream);
            String code = captcha.getCode();
            imageBytes = outputStream.toByteArray();
            captchaCode = new CaptchaCode(imageBytes, code);
        } catch (Exception e) {
            return new Result("验证码发送失败", SEND_CAPTCHA_ERR, null);
        }
        return new Result("验证码发送成功", SEND_CAPTCHA_OK, captchaCode);
    }


    //    头像上传
    @PostMapping("/picUpload")
    public Result picUpload(@RequestHeader String token, @RequestBody Map map) {
        String base64ImageData = (String) map.get("file");
        if (base64ImageData == null || base64ImageData.isEmpty()) {
            return new Result("文件为空", PIC_UPLOAD_ERR, null);
        }
        try {
            boolean b = userService.picUpload(base64ImageData, token);
            if (!b)
                return new Result("头像上传失败", PIC_UPLOAD_ERR, null);
        } catch (Exception e) {
            return new Result("头像上传失败", PIC_UPLOAD_ERR, null);
        }
        return new Result("头像上传成功", PIC_UPLOAD_OK, null);
    }


    //    更新个人信息
    @PostMapping
    public Result updateMsg(@RequestHeader String token, @RequestBody UserValue userValue) {
        if (!userService.updateUser(userValue, token))
            return new Result("个人信息更新失败", UPDATE_MSG_ERR, null);
        return new Result("个人信息更新成功", UPDATE_MSG_OK, null);
    }


    //    个人信息展示（主页）
    @GetMapping
    public Result showMsg(@RequestHeader String token) {
        return getUser(null, token);
    }

    //    查找对应用户的信息(用于社区查找用户等乱七八糟)
    @PostMapping("/detail")
    public Result getUser(@RequestBody Map<String, Integer> map) {
        Integer userId = map.get("userId");
        return getUser(userId, null);
    }

    private Result getUser(Integer userId, String token) {
        UserWithValue user = null;
        try {
            if (token == null)
                user = userService.showUser(userId);
            else if (userId == null) {
                user = userService.showUser(token);
            }
            if (user == null || user.getUserValue() == null || user.getUser() == null) {
                return new Result("个人信息展示失败", SHOW_MSG_ERR, null);
            }
            if (user.getUserValue().getPic() == null)
                user.getUserValue().setPic(DEFAULT_PIC);
        } catch (Exception e) {
            return new Result("个人信息展示失败", SHOW_MSG_ERR, null);
        }
        return new Result("个人信息展示成功", SHOW_MSG_OK, user);
    }

    //    查看个人点赞
    @GetMapping("/like")
    public Result getMyLike(@RequestHeader String token) {
        List<Post> posts;
        try {
            posts = userService.showLikePost(token);
        } catch (Exception e) {
            return new Result("展示个人喜欢帖子失败", SHOW_LIKE_POST_ERR, null);
        }
        if (posts == null) {
            return new Result("此用户没有喜欢的帖子", SHOW_LIKE_POST_NULL, null);
        }
        return new Result("展示个人喜欢的帖子成功 或此用户没有喜欢的帖子", SHOW_LIKE_POST_OK, posts);
    }

    @GetMapping("/comment")
    public Result getMyComment(@RequestHeader String token) {
        List<Comment> comments;
        try {
            comments = userService.showMyComment(token);
        } catch (Exception e) {
            return new Result("展示我的评论失败", SHOW_MY_COMMENT_ERR, null);
        }
        if (comments == null) {
            return new Result("展示我的评论失败", SHOW_MY_COMMENT_ERR, null);
        }
        return new Result("展示用户评论成功 或评论为空", SHOW_MY_COMMENT_OK, comments);
    }

    @GetMapping("/post")
    public Result getMyPost(@RequestHeader String token) {
        List<Post> posts;
        try {
            posts = userService.showMyPost(token);
        } catch (Exception e) {
            return new Result("展示我写的帖子失败", SHOW_MY_POST_ERR, null);
        }
        if (posts == null) {
            return new Result("展示我写的帖子失败", SHOW_MY_POST_ERR, null);
        }
        return new Result("展示我写的帖子成功", SHOW_MY_POST_OK, posts);
    }

    @GetMapping("/playedScript")
    public Result getPlayedScript(@RequestHeader String token) {
        List<ScriptMsg> scripts;
        try {
            scripts = userService.showMyPlayedScript(token);
        } catch (Exception e) {
            return new Result("展示我玩过的剧本失败", SHOW_PLAYED_SCRIPT_ERR, null);
        }
        if (scripts == null) {
            return new Result("展示我玩过的剧本失败 或者压根没玩过", SHOW_PLAYED_SCRIPT_ERR, null);
        }
        return new Result("展示我玩过的剧本成功", SHOW_PLAYED_SCRIPT_OK, scripts);
    }
}
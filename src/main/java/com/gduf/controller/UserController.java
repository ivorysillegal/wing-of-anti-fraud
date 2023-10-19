package com.gduf.controller;

import cn.hutool.captcha.AbstractCaptcha;
import com.gduf.pojo.CaptchaCode;
import com.gduf.pojo.user.UserValue;
import com.gduf.pojo.user.UserWithValue;
import com.gduf.service.UserService;
import com.gduf.util.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static com.gduf.controller.Code.*;

@RestController
//@CrossOrigin
@RequestMapping("/users")
public class UserController {

    private AbstractCaptcha captcha;

    @Autowired
    private UserService userService;

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
        if (password == null || username == null) {
            return new Result("账号或密码为空", REGISTER_ERR, null);
        }
        int register = userService.register(username, password);
        if (register == 1) {
            return new Result("注册成功", REGISTER_OK, null);
        } else if (register == 0)
            return new Result("注册失败 系统繁忙", REGISTER_ERR, null);
        else
            return new Result("用户名重复 请重试！", REGISTER_REPEAT_NAME, null);
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
        UserWithValue userWithValue;
        try {
            userWithValue = userService.showUser(token);
            if (userWithValue == null) {
                return new Result("个人信息展示失败", SHOW_MSG_ERR, null);
            }
        } catch (Exception e) {
            return new Result("个人信息展示失败", SHOW_MSG_ERR, null);
        }
        return new Result("个人信息展示成功", SHOW_MSG_OK, userWithValue);
    }
}

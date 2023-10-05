package com.itheima.controller;

import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.itheima.pojo.StrongPasswordAnswer;
import com.itheima.pojo.StrongPasswordQuestion;
import com.itheima.pojo.user.ImageUploadRequest;
import com.itheima.service.StrongPasswordService;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.itheima.controller.Code.*;

@RestController
//@CrossOrigin
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StrongPasswordService strongPasswordService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        boolean loginOrNot = userService.login(username, password);
        if (loginOrNot)
            return new Result("登录成功", LOGIN_OK, null);
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


    @PostMapping("/picUpload")
    public Result picUpload(@RequestBody ImageUploadRequest request) {
        String base64ImageData = request.getFile();
        if (base64ImageData == null || base64ImageData.isEmpty()) {
            return new Result("文件为空", PIC_UPLOAD_ERR, null);
        }
        byte[] imageData = Base64Utils.decodeFromString(base64ImageData);
        if (imageData.length > 512 * 512) {
            return new Result("照片大小超出限制", PIC_UPLOAD_ERR, null);
        }
        boolean b = userService.picUpload(request);
        if (!b)
            return new Result("头像上传失败", PIC_UPLOAD_ERR, null);
        return new Result("头像上传成功", PIC_UPLOAD_OK, null);
    }


    @GetMapping("/password")
    public Result passwordMaker() {
        try {
            List<StrongPasswordQuestion> strongPasswordQuestions = strongPasswordService.showQuestion();
            return new Result("获取问题成功", SHOW_PASSWORD_QUESTION_OK, strongPasswordQuestions);
        } catch (Exception e) {
            return new Result("获取问题失败", SHOW_PASSWORD_QUESTION_ERR, strongPasswordService);
        }
    }


    @PostMapping("/password")
//    public Result passwordMaker(@RequestBody StrongPasswordAnswer strongPasswordAnswer) {
    public Result passwordMaker(@RequestBody Object strongPasswordAnswer) {

        LinkedHashMap linkedHashMap = (LinkedHashMap) strongPasswordAnswer;
        System.out.println(linkedHashMap);
        ArrayList<String> answer = (ArrayList<String>) linkedHashMap.get("answers");
//        String[] answer = (String[])strongPasswordAnswer;
//        String[] answer = new String[8];

        String password;
        try {
//            String[] answer = strongPasswordAnswer.getAnswer();
            String originalText = "";
            for (String s : answer) {
                originalText += s;
                originalText += ' ';
            }
            password = strongPasswordService.translateText(originalText, "zh", "en");

        } catch (Exception e) {
            return new Result("密码生成失败", PASSWORD_SEND_ERR, null);
        }
        return new Result("密码生成完毕", PASSWORD_SEND_OK, password);
    }
}

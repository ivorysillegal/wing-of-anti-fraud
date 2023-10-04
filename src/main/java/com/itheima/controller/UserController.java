package com.itheima.controller;

import com.itheima.pojo.StrongPasswordQuestion;
import com.itheima.service.StrongPasswordService;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.itheima.controller.Code.*;

@RestController
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
        boolean register = userService.register(username, password);
        if (register) {
            return new Result("注册成功", REGISTER_OK, null);
        } else
            return new Result("注册失败", REGISTER_ERR, null);
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




}

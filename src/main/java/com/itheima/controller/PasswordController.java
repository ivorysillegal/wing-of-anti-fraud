package com.itheima.controller;

import com.itheima.pojo.StrongPasswordQuestion;
import com.itheima.service.StrongPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.itheima.controller.Code.*;
import static com.itheima.controller.Code.PASSWORD_SEND_OK;

@RestController
//@CrossOrigin
@RequestMapping("/users")
public class PasswordController {

    @Autowired
    private StrongPasswordService strongPasswordService;

    //    获取问题
    @GetMapping("/password")
    public Result passwordMaker() {
        try {
            List<StrongPasswordQuestion> strongPasswordQuestions = strongPasswordService.showQuestion();
            return new Result("获取问题成功", SHOW_PASSWORD_QUESTION_OK, strongPasswordQuestions);
        } catch (Exception e) {
            return new Result("获取问题失败", SHOW_PASSWORD_QUESTION_ERR, null);
        }
    }

    //    翻译问题
    @PostMapping("/password")
//    public Result passwordMaker(@RequestBody StrongPasswordAnswer strongPasswordAnswer) {
    public Result passwordMaker(@RequestBody Object strongPasswordAnswer) {

        LinkedHashMap linkedHashMap = (LinkedHashMap) strongPasswordAnswer;
        System.out.println(linkedHashMap);
        ArrayList<String> answer = (ArrayList<String>) linkedHashMap.get("answers");
        String password;
        try {
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

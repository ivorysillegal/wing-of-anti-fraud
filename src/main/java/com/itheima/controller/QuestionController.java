package com.itheima.controller;

import com.itheima.pojo.Question;
import com.itheima.pojo.user.QuestionRelate;
import com.itheima.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.itheima.controller.Code.*;

@RestController
//@CrossOrigin
@RequestMapping("/study")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    //    随机抽取问题
    @GetMapping
    public Result showQuestion() {
        List<Question> question = null;
        try {
            question = questionService.getQuestion();
            if (question == null) {
                return new Result("读取问题失败", SHOW_QUESTION_ERR, null);
            }
        } catch (Exception e) {
            return new Result("读取问题失败", SHOW_QUESTION_ERR, null);
        }
        return new Result("读取问题成功", SHOW_QUESTION_OK, question);
    }

    //    插入问题 变成错题集
    @PostMapping
    public Result getCondition(@RequestBody ArrayList<QuestionRelate> questionRelate) {
        boolean b = questionService.saveWrongAnswerQuestion(questionRelate);
        if (!b)
            return new Result("获取回答情况失败", GET_QUESTION_CONDITION_ERR, null);
        return new Result("获取回答情况成功", GET_QUESTION_CONDITION_OK, null);
    }

    @GetMapping("/wrong")
    public Result showWrongQuestion() {
        List<Question> questions = questionService.showWrongQuestion();
        if (questions == null || questions.isEmpty())
            return new Result("获取错题失败", GET_QUESTION_CONDITION_ERR, null);
        return new Result("获取错题成功", GET_QUESTION_CONDITION_OK, questions);
    }
}

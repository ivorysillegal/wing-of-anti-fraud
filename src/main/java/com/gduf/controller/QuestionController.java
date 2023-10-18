package com.gduf.controller;

import com.gduf.pojo.Question;
import com.gduf.pojo.user.QuestionRelate;
import com.gduf.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.gduf.controller.Code.*;

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
//    不知道为什么接收的时候会直接接收成LinkedListHashMap
//    于是直接通过一堆获取 遍历操作将其搞定
    @PostMapping
    public Result getCondition(@RequestBody LinkedHashMap questionRelate) {
        ArrayList<LinkedHashMap<String, Integer>> data = (ArrayList<LinkedHashMap<String, Integer>>) questionRelate.get("data");
        Iterator<LinkedHashMap<String, Integer>> iterator = data.iterator();
        ArrayList<QuestionRelate> value = new ArrayList<>();
        while (iterator.hasNext()) {
            LinkedHashMap<String, Integer> linkedHashMap = iterator.next();
            QuestionRelate qr = new QuestionRelate(linkedHashMap.get("userId"), linkedHashMap.get("questionId"));
            value.add(qr);
        }
        boolean success = questionService.saveWrongAnswerQuestion(value);
        if (!success)
            return new Result("发送回答情况失败", GET_QUESTION_CONDITION_ERR, null);
        return new Result("发送回答情况成功", GET_QUESTION_CONDITION_OK, null);
    }
}

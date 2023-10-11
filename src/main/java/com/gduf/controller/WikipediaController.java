package com.gduf.controller;

import com.gduf.pojo.News;
import com.gduf.pojo.Question;
import com.gduf.pojo.Wikipedia;
import com.gduf.service.NewsService;
import com.gduf.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

import static com.gduf.controller.Code.*;

@RestController
@RequestMapping("/wikipedia")
public class WikipediaController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public Result showQuestionInDetail(@RequestBody LinkedHashMap classifications) {
        String classification = (String) classifications.get("classification");
        List<Question> questions;
        List<News> news;
        Wikipedia wikipedia = null;
//        这里因为题目的数量和新闻的数量不是相等的 也没有明确的对应关系
//        所以暂且设定两个List的映射类Wikipedia 直接传给前端
        try {
            news = newsService.showNewsByClassification(classification);
            questions = questionService.showQuestionInClassification(classification);
            wikipedia = new Wikipedia(questions, news);
        } catch (Exception e) {
            return new Result("读取信息失败", SHOW_WIKIPEDIA_ERR, null);
        }
        return new Result("读取信息成功", SHOW_WIKIPEDIA_OK, wikipedia);
    }

}

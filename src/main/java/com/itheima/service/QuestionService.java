package com.itheima.service;

import com.itheima.pojo.Question;
import com.itheima.pojo.user.QuestionRelate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface QuestionService {
    public List<Question> getQuestion();

    public boolean saveWrongAnswerQuestion(List<QuestionRelate> questionRelate);

    public List<Question> showWrongQuestion();
}

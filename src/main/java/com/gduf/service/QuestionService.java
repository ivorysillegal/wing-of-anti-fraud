package com.gduf.service;

import com.gduf.pojo.Question;
import com.gduf.pojo.user.QuestionRelate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface QuestionService {
    public List<Question> getQuestion();

    public boolean saveWrongAnswerQuestion(List<QuestionRelate> questionRelate);

    public List<Question> showQuestionInClassification(String classification);
}

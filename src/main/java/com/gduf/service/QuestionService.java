package com.gduf.service;

import com.gduf.pojo.wikipedia.Question;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface QuestionService {
    public List<Question> getQuestion();

    public List<Question> showQuestionInClassification(String classification);

    public List<Question> getCompetitionQuestionsByDan(String dan);
}

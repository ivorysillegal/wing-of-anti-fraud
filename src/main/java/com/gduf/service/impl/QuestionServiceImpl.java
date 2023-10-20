package com.gduf.service.impl;

import com.gduf.dao.QuestionDAO;
import com.gduf.pojo.wikipedia.Question;
import com.gduf.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionDAO questionDAO;

    @Override
    public List<Question> getQuestion() {
        List<Question> questions;
        try {
            questions = questionDAO.randomGetQuestions();
        } catch (Exception e) {
            return null;
        }
        return questions;
    }

    @Override
    public List<Question> showQuestionInClassification(String classification) {
        List<Question> questions;
        try {
            questions = questionDAO.showQuestionByClassification(classification);
        } catch (Exception e) {
            return null;
        }
        return questions;
    }

}

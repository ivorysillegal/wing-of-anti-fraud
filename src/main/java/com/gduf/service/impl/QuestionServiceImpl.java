package com.gduf.service.impl;

import com.gduf.dao.QuestionDAO;
import com.gduf.pojo.Question;
import com.gduf.pojo.user.QuestionRelate;
import com.gduf.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public boolean saveWrongAnswerQuestion(List<QuestionRelate> questionRelate) {
        try {
            for (QuestionRelate relate : questionRelate) {
                questionDAO.insertWrongAnswer(relate);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<Question> showWrongQuestion() {
        List<Question> wrongQuestions = new ArrayList<>();
        try {
            List<QuestionRelate> questionRelates = questionDAO.selectWrongQuestion(UserServiceImpl.user.getUserId());
//            供测试所用
//                        List<QuestionRelate> questionRelates = questionDAO.selectWrongQuestion(1);
            for (QuestionRelate questionRelate : questionRelates) {
                wrongQuestions.add(questionDAO.getQuestionById(questionRelate.getQuestionId()));
            }
        } catch (Exception e) {
            return null;
        }
        return wrongQuestions;
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

package com.itheima.service.impl;

import com.itheima.dao.QuestionDAO;
import com.itheima.pojo.Question;
import com.itheima.pojo.user.QuestionRelate;
import com.itheima.service.QuestionService;
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
            for (QuestionRelate questionRelate : questionRelates) {
                wrongQuestions.add(questionDAO.getQuestionById(questionRelate.getQuestionId()));
            }
        } catch (Exception e) {
            return null;
        }
        return wrongQuestions;
    }
}

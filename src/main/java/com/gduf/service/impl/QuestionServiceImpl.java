package com.gduf.service.impl;

import com.gduf.dao.CompetitionDAO;
import com.gduf.dao.QuestionDAO;
import com.gduf.pojo.wikipedia.Question;
import com.gduf.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.gduf.controller.Code.*;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private CompetitionDAO competitionDAO;

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

    @Override
    public List<Question> getCompetitionQuestionsByDan(String dan) {
        List<Question> questions = new ArrayList<>();
        if (dan.startsWith("青铜")) {
            questions = questionDAO.randomGetQuestionByDan(BRONZE_QUESTION_NUM);
        } else if (dan.startsWith("白银"))
            questions = questionDAO.randomGetQuestionByDan(SILVER_QUESTION_NUM);
        else if (dan.startsWith("黄金"))
            questions = questionDAO.randomGetQuestionByDan(GOLD_QUESTION_NUM);
        else if (dan.startsWith("铂金"))
            questions = questionDAO.randomGetQuestionByDan(PLATINUM_QUESTION_NUM);
        else if (dan.startsWith("王者"))
            questions = questionDAO.randomGetQuestionByDan(KING_QUESTION_NUM);
        else return null;
        return questions;
    }
}

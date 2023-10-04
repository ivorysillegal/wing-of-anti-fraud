package com.itheima.service.impl;

import com.itheima.dao.StrongPasswordDAO;
import com.itheima.pojo.StrongPasswordQuestion;
import com.itheima.service.StrongPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrongPasswordServiceImpl implements StrongPasswordService {

    @Autowired
    private StrongPasswordDAO strongPasswordDAO;

    @Override
    public List<StrongPasswordQuestion> showQuestion() {
        List<StrongPasswordQuestion> questionForPassword = strongPasswordDAO.getQuestionForPassword();
        return questionForPassword;
    }
}

package com.gduf.service;

import com.gduf.pojo.StrongPasswordQuestion;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface StrongPasswordService {
    public List<StrongPasswordQuestion> showQuestion();

    public String translateText(String textToTranslate, String fromLanguage, String toLanguage);
}

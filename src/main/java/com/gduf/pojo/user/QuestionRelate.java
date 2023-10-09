package com.gduf.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionRelate {
    private Integer userId;
    private Integer questionId;
    private Boolean answerCondition;

    public QuestionRelate(Integer userId, Integer questionId) {
        this.userId = userId;
        this.questionId = questionId;
        this.answerCondition = false;
    }
}

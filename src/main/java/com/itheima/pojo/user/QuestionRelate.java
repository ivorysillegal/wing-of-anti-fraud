package com.itheima.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionRelate {
    private Integer userId;
    private Integer questionId;
    private Boolean answerCondition;
}

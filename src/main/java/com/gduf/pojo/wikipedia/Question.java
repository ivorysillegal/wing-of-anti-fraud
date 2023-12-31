package com.gduf.pojo.wikipedia;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Question {
    private Integer questionId;
    private String questionMsg;
    private String rightAnswer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;
    private String classification;
}

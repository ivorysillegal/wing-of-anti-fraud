package com.gduf.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StrongPasswordQuestion {
    private Integer questionId;
    private String passwordQuestion;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;

}

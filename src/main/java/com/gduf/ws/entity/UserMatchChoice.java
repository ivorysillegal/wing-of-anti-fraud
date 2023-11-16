package com.gduf.ws.entity;

import lombok.Data;

@Data
public class UserMatchChoice {
//    玩家的分数
    private Integer userScore;
//    玩家每一题提交的选项
    private String userSelectedAnswer;
}

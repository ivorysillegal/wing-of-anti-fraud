package com.gduf.ws.entity;

import lombok.Data;

@Data
public class UserMatchChoice {
    private String userId;
//    玩家每一题提交的选项的索引
    private Integer choice;
}

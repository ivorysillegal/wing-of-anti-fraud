package com.itheima.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserValue {
    private String sign;
    private int age;
    private boolean gender;
    private String pic;
    private String lastGame;

    public UserValue() {
        this.sign = null;
        this.age = 0;
        this.gender = false;
        this.pic = null;
        this.lastGame = null;
    }
}
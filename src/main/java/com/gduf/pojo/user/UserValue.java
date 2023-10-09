package com.gduf.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserValue {
    private Integer userId;
    private String sign;
    private int age;
    private boolean gender;
    private String picAvatar;
    private String lastGame;

    public UserValue() {
        this.sign = null;
        this.age = 0;
        this.gender = false;
        this.picAvatar = null;
        this.lastGame = null;
    }
}
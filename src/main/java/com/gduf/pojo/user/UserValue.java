package com.gduf.pojo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserValue {
    private Integer userId;
    private boolean gender;
    private int age;
    private String sign;
    private String picAvatar;

    public UserValue() {
        this.sign = null;
        this.age = 0;
        this.gender = false;
        this.picAvatar = null;
    }
}
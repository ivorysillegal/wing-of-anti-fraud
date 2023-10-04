package com.itheima.pojo.user;

import lombok.Data;
import lombok.Setter;

@Data
public class User {
    private Integer userId;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

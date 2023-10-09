package com.gduf.pojo.user;

import lombok.Data;

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

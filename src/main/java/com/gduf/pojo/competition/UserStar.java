package com.gduf.pojo.competition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.websocket.Session;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStar {
    private Integer userId;
    private String username;
    private Integer stars;
    private String dan;
    private Session session;

    public UserStar(Integer userId, String username, Integer stars) {
        this.userId = userId;
        this.username = username;
        this.stars = stars;
    }
}

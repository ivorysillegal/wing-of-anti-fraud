package com.gduf.service.impl;

import com.gduf.dao.CompetitionDAO;
import com.gduf.dao.UserDAO;
import com.gduf.pojo.competition.UserStar;
import com.gduf.service.CompetitionService;
import com.gduf.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    private CompetitionDAO competitionDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public String showPlayersDan(String token) {
        int userId = 0;
        try {
            userId = decodeToId(token);
        } catch (Exception e) {
            return null;
        }
        return showPlayersDan(userId);
    }

    @Override
    public String showPlayersDan(Integer userId) {
        Integer userStars;
        try {
            userStars = competitionDAO.showUserStar(userId);
        } catch (Exception e) {
            return null;
        }
        String userDan = null;
        if (!Objects.isNull(userStars))
            userDan = calculate(userStars);
        else
            return "青铜 0 星";
        return userDan;
    }

    @Override
    public UserStar showStars(String token) {
        try {
            int userId = decodeToId(token);
            Integer stars = competitionDAO.showUserStar(userId);
            String username = userDAO.getUsername(userId);
            return new UserStar(userId, username, stars);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean updateUserStar(Integer userId) {
        try {
            competitionDAO.updateUserStar(userId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String calculate(Integer score) {
        if (between(0, 1, score))
            return "青铜 " + (score - 0) + " 星";
        else if (between(2, 5, score))
            return "白银 " + (score - 2) + " 星";
        else if (between(5, 10, score))
            return "黄金 " + (score - 5) + " 星";
        else if (between(10, 17, score))
            return "铂金 " + (score - 7) + " 星";
        else if (score >= 18)
            return "王者" + (score - 17) + "星";
        else
            return null;
    }

    private boolean between(Integer a, Integer b, Integer value) {
        return a <= value && b >= value;
    }

    private int decodeToId(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
        return Integer.parseInt(userId);
    }
}

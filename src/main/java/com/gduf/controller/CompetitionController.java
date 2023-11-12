package com.gduf.controller;

import com.gduf.pojo.competition.UserStar;
import com.gduf.service.CompetitionService;
import com.gduf.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.gduf.controller.Code.*;

@RestController
@RequestMapping("/wikipedia/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

//    获取进入pk界面的用户信息
//    @GetMapping
//    public Result getUserSession(@RequestBody User user, HttpSession session){
//        Integer userId = user.getUserId();
//        try {
//            session.setAttribute("userId",userId);
//        }catch (Exception e){
//            return new Result("获取用户缓存失败",LOAD_USER_ERR,null);
//        }
//        return new Result("获取用户缓存成功",LOAD_USER_OK,null);
//    }

    //    开始匹配
    @PostMapping
    public Result matching(@RequestHeader String token, HttpSession session) {
        try {
            UserStar userWithStar = competitionService.showStars(token);
            session.setAttribute("userWithStar", userWithStar);
        } catch (Exception e) {
            return new Result("获取用户缓存失败", LOAD_USER_ERR, null);
        }
        return new Result("获取用户缓存成功", LOAD_USER_OK, null);
    }

    //    获取用户段位
    @GetMapping
    public Result getMyDan(@RequestHeader String token) {
        String userDan;
        try {
            userDan = competitionService.showPlayersDan(token);
        } catch (Exception e) {
            return new Result("加载用户段位失败", LOAD_USER_DAN_ERR, null);
        }
        return new Result("加载用户段位成功", LOAD_USER_DAN_OK, userDan);
    }

    private int decodeToId(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
        return Integer.parseInt(userId);
    }
}

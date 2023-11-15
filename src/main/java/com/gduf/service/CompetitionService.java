package com.gduf.service;

import com.gduf.pojo.competition.UserStar;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CompetitionService {

//    获取用户段位
    public String showPlayersDan(String token);

    public String showPlayersDan(Integer userId);

    public String showPlayersExtraDan(Integer userId);

    //    获取用户的星数
    public UserStar showStars(String token);

//    pk结束之后 更新赢了的用户的星星
    public boolean updateUserStar(Integer userId);
}

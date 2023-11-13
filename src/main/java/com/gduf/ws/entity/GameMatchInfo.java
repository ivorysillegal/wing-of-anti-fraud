package com.gduf.ws.entity;

import com.gduf.pojo.wikipedia.Question;
import lombok.Data;

import java.util.List;

/**
 * @author yeeq
 * @date 2021/4/4
 */
@Data
public class GameMatchInfo {
    // TODO UserMatchInfo 后期按需扩充dan等属性
    private UserMatchInfo selfInfo;
    private UserMatchInfo opponentInfo;
    private List<Question> questions;
//    用户名
    private String selfUsername;
    private String opponentUsername;
//    头像
    private String selfPicAvatar;
    private String opponentPicAvatar;
}

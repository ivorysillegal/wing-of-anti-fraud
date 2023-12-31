package com.gduf.ws.utils;

/**
 * 消息类型
 *
 * @author yeeq
 */
public enum MessageTypeEnum {

    /**
     * 用户加入
     */
    ADD_USER,
    /**
     * 匹配对手
     */
    MATCH_USER,
    /**
     * 取消匹配
     */
    CANCEL_MATCH,
    /**
     * 游戏开始
     */
    PLAY_GAME,

    /**
    *  对方已提交答案
    **/
    OPPONENT_COMMIT_ANSWER,

    /**
     * 游戏结束
     */
    GAME_OVER,
}
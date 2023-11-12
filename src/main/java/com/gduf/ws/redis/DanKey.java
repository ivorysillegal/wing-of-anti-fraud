package com.gduf.ws.redis;

//这个枚举类表示用户的段位
public enum DanKey {

    BRONZE,
//    青铜

    SILVER,
//    白银

    GOLD,
//    黄金

    PLATINUM,
//    铂金

    KING;
//    黄金

    public String getKey() {
        return this.name();
    }

}

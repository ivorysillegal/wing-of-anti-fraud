package com.gduf.pojo.script.mapper;

import com.gduf.pojo.script.ScriptEndSent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptEnd {
//    这个类是特殊结局的映射类
//    游玩时传结局到前端的时候不会使用到 因为当时已经在DAO层判断的结局 直接返回对应的字符串
//    使用时机为 上传结局时 使用此类作为映射类
    private Integer endId;
    private Integer scriptId;
    private String endMsg;
    private Integer influence1;
    private Integer influence2;
    private Integer influence3;
    private Integer influence4;
}

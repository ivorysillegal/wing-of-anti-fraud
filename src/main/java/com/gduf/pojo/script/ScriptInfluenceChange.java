package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptInfluenceChange {
//    本类描述游戏过程中指标的变化
    private Integer influence1;
    private Integer influence2;
    private Integer influence3;
    private Integer influence4;
}

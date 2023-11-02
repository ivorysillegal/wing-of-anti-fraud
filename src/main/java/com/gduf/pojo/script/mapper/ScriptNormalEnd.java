package com.gduf.pojo.script.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptNormalEnd {
    private Integer normalEndId;
    private Integer scriptId;
    private String normalEnd1;
    private String normalEnd2;
    private Integer startValue1;
    private Integer endValue1;
    private Integer startValue2;
    private Integer endValue2;
    private Integer startValue3;
    private Integer endValue3;
    private Integer startValue4;
    private Integer endValue4;
}
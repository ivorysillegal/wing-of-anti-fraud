package com.gduf.pojo.script.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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


    public boolean isEmpty() {
        return Objects.isNull(normalEndId) && Objects.isNull(scriptId) && Objects.isNull(normalEnd1) && Objects.isNull(normalEnd2) && Objects.isNull(startValue1) && Objects.isNull(endValue1)
                && Objects.isNull(startValue2) && Objects.isNull(endValue2) && Objects.isNull(startValue3) && Objects.isNull(endValue3) && Objects.isNull(startValue4) && Objects.isNull(endValue4);
    }
}

package com.gduf.pojo.script.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//mongodb映射类
public class WholeScript {
    private ScriptWithEnd scriptWithEnd;
    private Integer scriptId;
}

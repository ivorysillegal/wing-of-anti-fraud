package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptMsg {
    private Integer scriptId;
    private String scriptName;
    private Boolean scriptStatus;
    private String scriptBackground;
    private String classification;
}

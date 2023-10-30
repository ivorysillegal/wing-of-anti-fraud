package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptMsg {
    private Integer scriptId;
    private String scriptName;
    private Boolean scriptStatus;
    private String scriptBackground;
    private String classification;
}

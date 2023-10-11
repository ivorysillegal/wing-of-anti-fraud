package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptChoice {
    private Integer choiceId;
    private String choiceMsg;
    private Integer influence1;
    private Integer influence2;
    private Integer influence3;
    private Integer influence4;
    private Integer jump;
    private Integer scriptId;

}

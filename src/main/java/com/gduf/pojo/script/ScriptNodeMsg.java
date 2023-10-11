package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptNodeMsg {
    private Integer nodeId;
    private String word;
    private Integer scriptId;
    private Integer leftChoiceId;
    private Integer rightChoiceId;
}
package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptNodeMsg {
    private Integer nodeId;
    private String word;
    private Integer scriptId;
    private Integer leftChoiceId;
    private Integer rightChoiceId;
}
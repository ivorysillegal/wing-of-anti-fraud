package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptSpecialEnd {
    private Integer endId;
    private Integer script_id;
    private String end_msg;
    private Integer influence1;
    private Integer influence2;
    private Integer influence3;
    private Integer influence4;
}

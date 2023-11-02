package com.gduf.pojo.script.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptEnds {
    private Integer scriptId;
    private List<ScriptEnd> scriptEnds;
    private ScriptNormalEnd scriptNormalEnd;
}

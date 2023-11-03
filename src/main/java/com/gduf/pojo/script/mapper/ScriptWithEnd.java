package com.gduf.pojo.script.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptWithEnd {
    private Script script;
    private ScriptEnds scriptEnds;
}

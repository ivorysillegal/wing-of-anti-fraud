package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Script {
    private ScriptMsg scriptMsg;
    private List<ScriptNode> scriptNodes;
}

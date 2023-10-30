package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Script {
    private ScriptMsg scriptMsg;
    private List<ScriptNode> scriptNodes;
    private ScriptInfluenceName scriptInfluenceName;
}

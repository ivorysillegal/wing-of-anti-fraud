package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptMsgWithInfluenceName {
    private ScriptInfluenceName scriptInfluenceName;
    private ScriptMsg scriptMsg;
}

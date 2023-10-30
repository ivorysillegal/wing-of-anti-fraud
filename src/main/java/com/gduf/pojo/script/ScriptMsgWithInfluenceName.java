package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptMsgWithInfluenceName {
    private ScriptInfluenceName scriptInfluenceName;
    private ScriptMsg scriptMsg;
}

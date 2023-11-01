package com.gduf.pojo.script.mapper;

import com.gduf.pojo.script.ScriptInfluenceName;
import com.gduf.pojo.script.ScriptMsg;
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

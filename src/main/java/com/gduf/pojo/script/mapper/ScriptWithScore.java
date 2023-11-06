package com.gduf.pojo.script.mapper;

import com.gduf.pojo.script.ScriptMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptWithScore {
    private ScriptMsg scriptMsg;
    private Integer score;
}

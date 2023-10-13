package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptInfluenceName {
//    本类描述各剧本特有的指标的名字
    private Integer influenceNameId;
    private String influence1Name;
    private String influence2Name;
    private String influence3Name;
    private String influence4Name;
    private Integer scriptId;
//    对应剧本的id
}

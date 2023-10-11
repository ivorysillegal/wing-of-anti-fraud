package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptNode {
    private Integer nodeId;
    //    节点的id
    private String word;
    private Integer scriptId;
    //    属于哪一个剧本?
    private ScriptChoice leftChoice;
    private ScriptChoice rightChoice;
}

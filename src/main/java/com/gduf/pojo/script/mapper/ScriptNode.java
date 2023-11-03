package com.gduf.pojo.script.mapper;

import com.gduf.pojo.script.ScriptChoice;
import com.gduf.pojo.script.ScriptNodeMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptNode {
    private Integer nodeId;
    //    节点的id
    private String word;
    private Integer scriptId;
    //    属于哪一个剧本?
    private ScriptChoice leftChoice;
    private ScriptChoice rightChoice;

    public ScriptNode(ScriptNodeMsg scriptNodeMsg, ScriptChoice leftChoice, ScriptChoice rightChoice) {
        this.scriptId = scriptNodeMsg.getScriptId();
        this.nodeId = scriptNodeMsg.getNodeId();
        this.word = scriptNodeMsg.getWord();
        this.leftChoice = leftChoice;
        this.rightChoice = rightChoice;
    }
}

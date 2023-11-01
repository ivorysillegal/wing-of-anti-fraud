package com.gduf.pojo.script;

import com.gduf.pojo.script.mapper.ScriptNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptNodeMsg {
    private Integer nodeId;
    private String word;
    private Integer scriptId;
    private Integer leftChoiceId;
    private Integer rightChoiceId;

    public ScriptNodeMsg(ScriptNode scriptNode, Integer leftChoiceId, Integer rightChoiceId) {
        this.nodeId = scriptNode.getNodeId();
        this.scriptId = scriptNode.getScriptId();
        this.word = scriptNode.getWord();
        this.leftChoiceId = leftChoiceId;
        this.rightChoiceId = rightChoiceId;
    }
}
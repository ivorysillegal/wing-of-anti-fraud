package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//此类用于保存 节点在最后一次完成编辑之后 所处的位置
public class ScriptNodePosition {
    private Integer scriptId;
    private Integer nodeId;
    private Integer x;
    private Integer y;
}

package com.gduf.pojo.script.mapper;

import com.gduf.pojo.script.ScriptNodePosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//此类是一个剧本所有节点位置的映射类 用于保存和获取一个剧本中所有节点的位置
public class ScriptNodePositionList {
    private Integer scriptId;
    private List<ScriptNodePosition> scriptNodePositions;
}


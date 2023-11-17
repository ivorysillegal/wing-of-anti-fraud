package com.gduf.pojo.script.mapper;

import com.gduf.pojo.script.ScriptNodePosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptWithPosition {

    private List<ScriptNodePosition> scriptNodePositions;
    private Integer scriptId;
}

package com.gduf.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Script {
    private Integer scriptId;
    private Integer scriptName;
    private Boolean scriptStatus;
}

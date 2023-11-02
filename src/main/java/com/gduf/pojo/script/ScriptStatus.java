package com.gduf.pojo.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.gduf.controller.Code.SCRIPT_STATUS_CHECKING;

@Data
@AllArgsConstructor
@NoArgsConstructor
//自制剧本的时候 自动添加入 (草稿箱状态)
public class ScriptStatus {
    private Integer scriptId;
    private Integer status;
    private Integer producerId;
    private Boolean isOfficial;
    private Boolean isDel;

    public ScriptStatus(Integer scriptId, Integer producerId) {
        this.scriptId = scriptId;
        this.producerId = producerId;
        this.isOfficial = false;
        this.isDel = false;
        this.status = SCRIPT_STATUS_CHECKING;
    }
}

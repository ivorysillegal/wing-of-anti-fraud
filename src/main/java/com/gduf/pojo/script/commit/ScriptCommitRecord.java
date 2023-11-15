package com.gduf.pojo.script.commit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptCommitRecord {
    private Integer commitId;
    private Integer scriptId;
    private String commitMsg;

}

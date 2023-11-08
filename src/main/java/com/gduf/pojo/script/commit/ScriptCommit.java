package com.gduf.pojo.script.commit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptCommit {
    private Integer commitId;
    private Integer scriptId;
    private Integer userId;
    private Integer commitStatus;
    private String commitMsg;
    private Date commitDate;
}

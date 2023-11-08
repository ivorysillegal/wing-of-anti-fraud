package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostTheme {
    private Integer postId;
    private Integer isExperience;
    private Integer isAsk;
    private Integer isScript;

    public PostTheme(Integer isScript) {
        this.isExperience = -1;
        this.isAsk = -1;
        this.isScript = isScript;
    }
}

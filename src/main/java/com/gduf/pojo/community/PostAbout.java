package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAbout {
    private Integer postId;
    private PostTheme postTheme;
    private PostTopic postTopic;
}

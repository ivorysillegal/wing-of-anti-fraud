package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//包装帖子的主要内容用于返回
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostWithComments {
    private Post post;
    private List<Comment> comments;
}

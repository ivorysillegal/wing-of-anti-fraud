package com.gduf.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Post {
    private Integer postId;
    private String title;
    private String article;
    private Date createTime;
    private Integer writerId;
    private Integer likes;
    private Integer stars;
    private Integer comments;
}

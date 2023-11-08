package com.gduf.pojo.community;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Integer postId;
    private String title;
    private String article;
    private Date createTime;
    private Integer writerId;
    private Integer likes;
    private Integer stars;
    private Integer comments;


//    此构造函数用于上传帖子时作有参映射
    public Post(String title, String article) {
        this.title = title;
        this.article = article;
        this.createTime = new Date();
    }


    public Post(String title, String article,Integer writerId) {
        this.title = title;
        this.article = article;
        this.writerId = writerId;
        this.createTime = new Date();
    }
}

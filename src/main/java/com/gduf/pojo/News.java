package com.gduf.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class News {
    private Integer newsId;
    private String pic;
    private String title;
    private String main;
    private String classification;
}
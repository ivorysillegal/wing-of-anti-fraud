package com.gduf.pojo.wikipedia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {
    private Integer newsId;
    private String pic;
    private String title;
    private String main;
    private String classification;
}
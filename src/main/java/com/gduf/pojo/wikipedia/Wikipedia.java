package com.gduf.pojo.wikipedia;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Wikipedia {
    private List<Question> question;
    private List<News> news;
}

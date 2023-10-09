package com.gduf.service;

import com.gduf.pojo.News;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface NewsService {
    public List<News> showNewsByClassification(String classification);
}

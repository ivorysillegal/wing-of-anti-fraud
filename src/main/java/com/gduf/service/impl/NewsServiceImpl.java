package com.gduf.service.impl;

import com.gduf.dao.NewsDAO;
import com.gduf.pojo.wikipedia.News;
import com.gduf.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsDAO newsDAO;

    @Override
    public List<News> showNewsByClassification(String classification) {
        List<News> news;
        try {
            news = newsDAO.showNewsByClassification(classification);
        } catch (Exception e) {
            return null;
        }
        return news;
    }
}

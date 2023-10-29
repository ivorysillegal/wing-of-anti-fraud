package com.gduf.service.impl;

import com.gduf.dao.NewsDAO;
import com.gduf.pojo.wikipedia.News;
import com.gduf.service.NewsService;
import com.gduf.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsDAO newsDAO;

    @Autowired
    private RedisCache redisCache;

    @Override
    public List<News> showNewsByClassification(String classification) {
        List<News> news;
        news = redisCache.getCacheList("newsIn" + classification);
        if (news.isEmpty()) {
            try {
                news = newsDAO.showNewsByClassification(classification);
            } catch (Exception e) {
                return null;
            }
        }
        redisCache.setCacheList("newsIn" + classification, news,10, TimeUnit.MINUTES);
        return news;
    }
}

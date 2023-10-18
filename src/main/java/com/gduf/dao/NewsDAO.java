package com.gduf.dao;

import com.gduf.pojo.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NewsDAO {
    @Select("select * from tb_news where classification = #{classification} order by rand() limit 6")
    public List<News> showNewsByClassification(String classification);
}

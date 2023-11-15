package com.gduf.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CompetitionDAO {

    @Select("select star from user_dan where user_id = #{userId}")
    public Integer showUserStar(Integer userId);

    @Update("update user_dan set star = (star + 1) where user_id = #{userId}")
    public void updateUserStar(Integer userId);

    @Insert("INSERT INTO user_dan (user_id) VALUES (#{userId})")
    public void initializationUserStar(Integer userId);

}

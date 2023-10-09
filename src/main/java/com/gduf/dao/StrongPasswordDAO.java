package com.gduf.dao;

import com.gduf.pojo.StrongPasswordQuestion;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StrongPasswordDAO {
    @Select("select * from tb_password_maker_question order by rand() limit 5")
    public List<StrongPasswordQuestion> getQuestionForPassword();

    @Insert("insert into tb_password_maker_data (user_id,password,password_place) values(#{userId},#{password},#{passwordPlace})")
    public int insertPassword(StrongPasswordQuestion strongPasswordQuestion);
}
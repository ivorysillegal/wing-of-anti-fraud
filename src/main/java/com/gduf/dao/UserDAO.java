package com.gduf.dao;

import com.gduf.pojo.user.User;
import com.gduf.pojo.user.UserValue;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {

    @Insert("insert into tb_user_basic (username, password) values (#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
//    这里使用了 回显主键的方法 但是一定要记住 keyProperty
    public int insertBasic(User user);

    @Insert("insert into user_value (sign,age,gender,pic) values (#{sign},#{age},#{gender},#{pic})")
    public int insertValue(UserValue userValue);

    @Insert("insert into user_value (user_id) values (#{userId})")
    public int initializationUserValue(Integer userId);

    @Select("select * from tb_user_basic where username = #{username}")
    public User getByUsername(String username);

    @Update("update user_value set pic = #{pic} where user_id = #{userId}")
    public int updatePic(Integer userId, String pic);

    @Select("select * from user_value where user_id = #{id}")
    public UserValue getValueById(Integer id);

    @Update("update user_value set sign = #{sign},age = #{age},gender = #{gender} where user_id = #{beforeUserId}")
    public int UpdateUserValue(String sign, Integer age, Boolean gender, Integer beforeUserId);

    @Update("update tb_user_basic set password = #{password} where username = #{username}")
    public int updatePassword(String password,String username);
}

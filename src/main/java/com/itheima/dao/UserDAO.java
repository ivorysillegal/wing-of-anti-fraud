package com.itheima.dao;

import com.itheima.domain.Book;
import com.itheima.pojo.user.User;
import com.itheima.pojo.user.UserValue;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {

    @Insert("insert into tb_user_basic (username, password) values (#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
//    这里使用了 回显主键的方法 但是一定要记住 keyProperty指的是类名
    public int insertBasic(User user);

    @Insert("insert into user_value (sign,age,gender,pic,last_game) values (#{sign},#{age},#{gender},#{pic},#{lastGame})")
    public int insertValue(UserValue userValue);

//    @Insert("insert into user_game (coins,last_played_game) values(#{coins},#{lastPlayedGame})")
//    public int insertGame();

//    @Delete("delete from tb_user_basic where id = #{id}")
//    public int selectOne(Integer id);
//
//    @Select("select * from tbl_book where id = #{id}")
//    public Book getById(Integer id);

    @Select("select * from tb_user_basic where username = #{username}")
    public User getByUsername(String username);

    @Update("update user_value set pic_avatar = #{picAvatar} where user_id = #{userId}")
    public int updatePic(Integer userId,String picAvatar);

    @Select("select * from user_value where user_id = #{id}")
    public UserValue  getValueById(Integer id);
}

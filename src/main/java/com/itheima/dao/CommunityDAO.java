package com.itheima.dao;

import com.itheima.domain.Book;
import com.itheima.pojo.Post;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommunityDAO {

    @Insert("insert into tb_community (title,article,create_time) values (#{title},#{article},#{createTime})")
    public int insertPost(Post post);

    @Select("select * from tb_community")
    public List<Post> showAllPost();

    @Select("select * from tb_community where writer_id = #{writerId}")
    public List<Post> showPostByWriter(Integer writerId);

    @Update("update tbl_book set type = #{type}, name = #{name}, description = #{description} where id = #{id}")
    public int update(Book book);

    @Update("update tb_community set likes = (likes + 1)")
    public void insetLikes();
}

package com.gduf.dao;

import com.gduf.pojo.community.Comment;
import com.gduf.pojo.community.Post;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommunityDAO {

    @Insert("insert into tb_post (title,article,create_time,writer_id) values (#{title},#{article},#{createTime},#{writerId})")
    public int insertPost(Post post);

    @Select("select * from tb_post")
    public List<Post> showAllPost();

    @Select("select * from tb_post where writer_id = #{writerId}")
    public List<Post> showPostByWriter(Integer writerId);

    @Select("select * from tb_post where post_id = #{postId}")
    public Post showPostById(Integer postId);

    @Select("select * from tb_comment where post_id = #{postId}")
    public List<Comment> showEachPostComment(Integer postId);

    //    当点赞的时候 更新社区表中点赞数量
    @Update("update tb_post set likes = (likes + 1) where post_id = #{postId}")
    public void updateLikesInCommunity(Integer postId);

    //    更新收藏
    @Update("update tb_post set stars = (stars + 1) where post_id = #{postId}")
    public void updateStarsInCommunity(Integer postId);

    //    更新评论
    @Update("update tb_post set comments = (comments + 1) where post_id = #{postId}")
    public void updateCommentsInCommunity(Integer postId);

    @Update("update tb_comment set likes = (likes + 1) where comment_id = #{commentId} ")
    public void updateLikesForCommentsInCommunity(Integer commentId);

    //    关系表增加
    @Insert("insert into user_like_posts (user_id,post_id) values (#{userId},#{postId}) ")
    public void insetLike(Integer postId, Integer userId);

    @Insert("insert into user_star_posts (user_id,post_id) values (#{userId},#{postId})")
    public void insertStar(Integer postId, Integer userId);

    @Insert("insert into tb_comment (comment_msg,post_id,user_id) values (#{commentMsg},#{postId},#{userId})")
    public void insertComment(String commentMsg, Integer postId, Integer userId);

    @Insert("insert into user_like_comments (user_id,comment_id) values (#{userId},#{commentId})")
    public void insertLikesForComment(Integer userId, Integer commentId);

    @Select("select post_id from user_like_posts where user_id = #{userId}")
    public List<Integer> showLikePostId(Integer userId);

    @Select("select * from tb_comment where user_id = #{userId}")
    public List<Comment> showCommentById(Integer userId);
}

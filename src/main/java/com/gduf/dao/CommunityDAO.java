package com.gduf.dao;

import com.gduf.pojo.community.Comment;
import com.gduf.pojo.community.Post;
import com.gduf.pojo.community.PostTheme;
import com.gduf.pojo.community.PostTopic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommunityDAO {

    @Insert("insert into tb_post (title,article,create_time,writer_id) values (#{title},#{article},#{createTime},#{writerId})")
    @Options(useGeneratedKeys = true, keyProperty = "postId")
    public void insertPost(Post post);

    @Insert("insert into post_theme (post_id,is_experience,is_ask,is_script) values (#{postId},#{isExperience},#{isAsk},#{isScript})")
    public void insertPostTheme(PostTheme postTheme);

    @Insert("insert into post_topic (post_id,is_tel_fraud,is_cult,is_wire_fraud,is_financial_fraud,is_oversea_fraud,is_pyramid_sale) values (#{postId},#{isTelFraud},#{isCult},#{isWireFraud},#{isFinancialFraud},#{isOverseaFraud},#{isPyramidSale})")
    public void insertPostTopic(PostTopic postTopic);

    @Select("select * from tb_post")
    public List<Post> showAllPost();

//    @Select("select count (*) from post_theme where is_script = #{isScript}")
    @Select("SELECT post_id FROM post_theme WHERE is_script <> 1")
    public List<Integer> showCommonPost();

    @Select("select * from tb_post where writer_id = #{writerId}")
    public List<Post> showPostByWriter(Integer writerId);

    @Select("select post_id from post_theme where is_script = 1")
    public List<Integer> showScriptPostId();

    @Select("select post_id from post_theme where is_experience = #{isExperience} OR is_ask = #{isAsk}")
    public List<Integer> showPostIdByTheme(PostTheme postTheme);

//    如果是经验贴 1
//    不是 0
//    存表的时候 存入-1
    @Select("select post_id from post_topic where is_tel_fraud = #{isTelFraud} OR is_cult = #{isCult} OR is_wire_fraud = #{isWireFraud} OR is_financial_fraud = #{isFinancialFraud} OR is_oversea_fraud = #{isOverseaFraud} OR is_pyramid_sale = #{isPyramidSale}")
    public List<Integer> showPostIdByTopic(PostTopic postTopic);

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

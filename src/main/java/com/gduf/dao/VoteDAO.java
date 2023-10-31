package com.gduf.dao;

import com.gduf.pojo.community.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VoteDAO {
    @Select("select * from tb_vote where term = #{term}")
    public Vote showVoteByTerm(Integer term);

    @Select("select * from vote_choice where vote_choice_id = #{voteChoiceId}")
    public VoteChoice showVoteChoiceById(Integer voteChoiceId);

    @Update("update vote_choice set approve = (approve + 1) where vote_id = #{voteId} AND opinion = #{opinion}")
    public void voteAction(Integer voteId, Boolean opinion);

    //   手动寻找最新一期的投票
    @Select("select * from tb_vote where term = (select max(term) from tb_vote)")
    public List<Vote> showNewestVote();

    //    寻找一级评论
    @Select("select * from vote_comment where vote_id = #{voteId}")
    public List<VoteFirstComment> showFirstVoteCommentById(Integer voteId);

    //    根据一级评论的id找二级评论
    @Select("select * from vote_second_comment where first_vote_comment_id = #{firstVoteCommentId}")
//    public List<VoteSecondComment> showSecondCommentByFirstId(Integer firstVoteCommentId);
//    public List<VoteSecondCommentDTO> showSecondCommentByFirstId(Integer firstVoteCommentId);
    public List<VoteSecondCommentDTO> showSecondCommentByFirstId(@Param("firstVoteCommentId") Integer firstVoteCommentId);
//    List<BlogCommentDTO> selectCommentById(@Param("blogId") String blogId, @Param("parentId") String parentId, @Param("current") Integer current, @Param("size") Integer size);

    @Insert("insert into vote_comment (msg,vote_id,opinion,writer_id) values (#{msg},#{voteId},#{opinion},#{writerId})")
    public void insertFirstComment(VoteFirstComment voteFirstComment);

    @Insert("insert into vote_second_comment (msg,parent_comment_id,opinion,writer_id,first_vote_comment_id) values (#{msg},#{parentCommentId},#{opinion},#{writerId},#{firstVoteCommentId})")
    public void insertSecondComment(VoteSecondComment voteSecondComment);

    public List<VoteSecondComment> test(Integer id);
}

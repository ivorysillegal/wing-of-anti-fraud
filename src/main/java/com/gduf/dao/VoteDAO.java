package com.gduf.dao;

import com.gduf.pojo.community.*;
import com.gduf.util.TreeNode;
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

    @Select("select count(*) from vote_user where user_id = #{userId} AND vote_id = #{voteId}")
    public Integer checkIfVote(Integer voteId, Integer userId);

    @Insert("insert into vote_user (vote_id,user_id,opinion) values (#{voteId},#{userId},#{opinion})")
    public void insertVote(Integer voteId, Integer userId,Boolean opinion);

    //   手动寻找最新一期的投票
    @Select("select * from tb_vote where term = (select max(term) from tb_vote)")
    public List<Vote> showNewestVote();

    @Select("select * from tb_vote")
    public List<Vote> showAllLastVote();

    @Select("select vote_id from vote_comment where vote_comment_id = #{voteCommentId}")
    public Integer showVoteIdByCommentId(Integer voteCommentId);

    @Select("select * from vote_user where user_id = #{userId}")
    public List<VoteUser> showVoted(Integer userId);

    //    寻找一级评论
    @Select("select * from vote_comment where vote_id = #{voteId}")
    public List<VoteFirstComment> showFirstVoteCommentById(Integer voteId);

    //    根据一级评论的id找二级评论
    @Select("select * from vote_second_comment where first_vote_comment_id = #{firstVoteCommentId}")
//    public List<VoteSecondComment> showSecondCommentByFirstId(Integer firstVoteCommentId);
//    public List<VoteSecondCommentDTO> showSecondCommentByFirstId(Integer firstVoteCommentId);
//    public List<VoteSecondCommentDTO> showSecondCommentByFirstId(@Param("firstVoteCommentId") Integer firstVoteCommentId);
    public List<VoteSecondComment> showSecondCommentByFirstId(@Param("firstVoteCommentId") Integer firstVoteCommentId);
//    List<BlogCommentDTO> selectCommentById(@Param("blogId") String blogId, @Param("parentId") String parentId, @Param("current") Integer current, @Param("size") Integer size);

    @Insert("insert into vote_comment (msg,vote_id,opinion,writer_id) values (#{msg},#{voteId},#{opinion},#{writerId})")
    public void insertFirstComment(VoteFirstComment voteFirstComment);

    @Update("update tb_vote set comments = (comments + 1) where vote_id = #{voteId}")
    public void updateVoteComments(Integer voteId);

    @Insert("insert into vote_second_comment (msg,parent_comment_id,opinion,writer_id,first_vote_comment_id) values (#{msg},#{parentCommentId},#{opinion},#{writerId},#{firstVoteCommentId})")
    public void insertSecondComment(VoteSecondComment voteSecondComment);

    @Update("update vote_comment set reply = (reply + 1) where vote_comment_id = #{voteCommentId}")
    public void updateCommentReply(Integer voteCommentId);
}

package com.gduf.dao;

import com.gduf.pojo.community.VoteSecondComment;
import com.gduf.pojo.community.Vote;
import com.gduf.pojo.community.VoteChoice;
import com.gduf.pojo.community.VoteFirstComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface VoteDAO {
    @Select("select * from tb_vote where vote_id = #{voteId}")
    public Vote showVoteById(Integer voteId);

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
    @Select("select * from vote_second_comment where parent_comment_id = #{parentCommentId}")
    public List<VoteSecondComment> showSecondCommentByFirstId(Integer parentCommentId);

}

package com.itheima.dao;

import com.itheima.pojo.Question;
import com.itheima.pojo.ScriptNode;
import com.itheima.pojo.user.QuestionRelate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionDAO {
    @Insert("insert into tb_question (question_msg,right_answer,wrong_answer1,wrong_answer2) values (#{questionMsg},#{rightAnswer},#{wrongAnswer1},#{wrongAnswer2})")
    public int insertQuestion(Question question);

    @Select("select * from tb_question order by rand() limit 10")
    public List<Question> randomGetQuestions();

    @Insert("insert into user_question (question_id,user_id) values (#{questionId},#{userId})")
    public int insertWrongAnswer(QuestionRelate questionRelate);

    @Select("select * from user_question where user_id = #{userId}")
    public List<QuestionRelate> selectWrongQuestion(Integer userId);

    @Select("select * from tb_question where question_id = #{questionId}")
    public Question getQuestionById(Integer questionId);
}

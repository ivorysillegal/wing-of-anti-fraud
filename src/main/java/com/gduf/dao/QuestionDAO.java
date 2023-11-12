package com.gduf.dao;

import com.gduf.pojo.wikipedia.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionDAO {
    @Insert("insert into tb_question (question_msg,right_answer,wrong_answer1,wrong_answer2,classification) values (#{questionMsg},#{rightAnswer},#{wrongAnswer1},#{wrongAnswer2}，#{classification})")
    public int insertQuestion(Question question);

    @Select("select * from tb_question order by rand() limit 10")
    public List<Question> randomGetQuestions();

    @Select("select * from tb_question where classification = #{classification} order by rand() limit 4")
    public List<Question> showQuestionByClassification(String classification);

    @Select("select * from tb_question order by rand() limit #{limit}")
    public List<Question> randomGetQuestionByDan(Integer limit);
}

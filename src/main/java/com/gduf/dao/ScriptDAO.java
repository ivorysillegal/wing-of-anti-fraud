package com.gduf.dao;

import com.gduf.pojo.script.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScriptDAO {

    @Insert("insert into tb_script (script_name,script_background,classification) values (#{scriptName},#{scriptBackground},#{classification})")
    public int insertScript(ScriptMsg scriptMsg);

    @Insert("insert into tb_script_influence_name (influence1_name,influence2_name,influence3_name,influence4_name) values (#{influence1Name},#{influence2Name},#{influence3Name},#{influence4Name})")
    public void insertScriptInfluenceName(ScriptInfluenceName scriptInfluenceName);

//    @Insert("insert into tb_script_node (word,script_id,choice1,choice2,influence1,influence2,influence3,influence4,influence5,jump_for_choice1,jump_for_choice2,jump_for_stop) values (#{word},#{script_id},#{choice1},#{choice2},#{influence1},#{influence2},#{influence3},#{influence4},#{influence5},#{jumpForChoice1},#{jumpForChoice2},#{jumpForStop})")
//    public int insertScriptNode(ScriptNode scriptNode);

    @Select("select * from tb_script where script_id = #{scriptId}")
    public ScriptMsg getScriptMsg(Integer scriptId);

    @Select("select * from tb_script")
    @Options(useGeneratedKeys = true, keyProperty = "scriptId")
    public List<ScriptMsg> getAllScript();

    @Select("select * from tb_script_node where script_id = #{scriptId}")
    public List<ScriptNode> getScriptNode(Integer scriptId);

    @Select("select * from tb_script_node where script_id = #{scriptId}")
    public List<ScriptNodeMsg> getScriptNodeMsg(Integer scriptId);

//    @Select("select * from tb_script_node")
//    public List<ScriptNodeMsg> getScriptNodeMsg(Integer scriptId);

    @Select("select * from tb_script_choice where script_id = #{scriptId} AND choice_id = #{choiceId}")
    public ScriptChoice getScriptNodeChoice(Integer scriptId, Integer choiceId);

//    @Select("select * from tb_script_end where script_id = #{scriptId}")
//    public List<ScriptSpecialEnd> getScriptSpecialEnd(Integer scriptId);

    //    剧本游玩的时候 指标没了或者满了 剧本提前结束 判断触发什么特殊的结局
    @Select("SELECT end_msg FROM tb_script_end WHERE influence1 = #{influence1} OR influence2 = #{influence2} OR influence3 = #{influence3} OR influence4 = #{influence4} ORDER BY end_id DESC LIMIT 1")
//    public String getScriptSpecialEnd(Integer scriptId,ScriptInfluence scriptInfluence);
    public String getScriptSpecialEnd(Integer scriptId, Integer influence1, Integer influence2, Integer influence3, Integer influence4);

    //    根据前端返回的指标数据 手动衡量应该为什么结局
    @Select("SELECT CASE WHEN #{influence1} >= start_value1 AND #{influence1} <= end_value1 AND #{influence2} >= start_value2 AND #{influence2} <= end_value2 AND #{influence3} >= start_value3 AND #{influence3} <= end_value3 AND #{influence4} >= start_value4 AND #{influence4} <= end_value4 THEN normal_end1 ELSE normal_end2 END AS result FROM tb_script_normal_end WHERE script_id = #{scriptId}")
//    public String getScriptStoryEnd(ScriptInfluence scriptInfluence,Integer scriptId);
    public String getScriptNormalEnd(Integer influence1, Integer influence2, Integer influence3, Integer influence4, Integer scriptId);

    @Select("select * from tb_script_influence_name where script_id = #{scriptId}")
    public ScriptInfluenceName getInfluenceName(Integer scriptId);
}

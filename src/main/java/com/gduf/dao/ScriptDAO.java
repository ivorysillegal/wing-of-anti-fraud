package com.gduf.dao;

import com.gduf.pojo.script.ScriptChoice;
import com.gduf.pojo.script.commit.ScriptCommit;
import com.gduf.pojo.script.mapper.ScriptEnd;
import com.gduf.pojo.script.mapper.ScriptNode;
import com.gduf.pojo.script.*;
import com.gduf.pojo.script.ScriptStatus;
import com.gduf.pojo.script.mapper.ScriptNormalEnd;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ScriptDAO {

    @Insert("insert into tb_script (script_name,script_background,classification) values (#{scriptName},#{scriptBackground},#{classification})")
    @Options(useGeneratedKeys = true, keyProperty = "scriptId", keyColumn = "script_id")
    public void insertScript(ScriptMsg scriptMsg);

    @Update("update tb_script set script_name = #{scriptName},script_background = #{scriptBackground},classification = #{classification} where script_id = #{scriptId}")
    public void updateScript(ScriptMsg scriptMsg);

    @Insert("insert into tb_script_influence_name (influence1_name,influence2_name,influence3_name,influence4_name,script_id) values (#{influence1Name},#{influence2Name},#{influence3Name},#{influence4Name},#{scriptId})")
    public void insertScriptInfluenceName(ScriptInfluenceName scriptInfluenceName);

    @Update("update tb_script_influence_name set influence1_name = #{influence1Name} , influence2_name = #{influence2Name},influence3_name = #{influence3Name},influence4_name = #{influence4Name} where influence_name_id = #{influenceNameId}")
    public void updateScriptInfluenceName(ScriptInfluenceName scriptInfluenceName);

    //    增加剧本节点信息（node）
    @Insert("insert into tb_script_node (node_id,word,script_id,left_choice_id,right_choice_id) values (#{nodeId},#{word},#{scriptId},#{leftChoiceId},#{rightChoiceId})")
    public void insertScriptNodeMsg(ScriptNodeMsg scriptNodeMsg);

    //    更新剧本节点信息 (node)
    @Update("update tb_script_node set word = #{word}, left_choice_id = #{leftChoiceId}, right_choice_id = #{rightChoiceId} where script_id = #{scriptId} AND node_id = #{nodeId}")
    public void updateScriptNodeMsg(ScriptNodeMsg scriptNodeMsg);

    //    增加剧本选择信息（choice）
    @Insert("insert into tb_script_choice (choice_id,choice_msg,influence1,influence2,influence3,influence4,jump,script_id) values (#{choiceId},#{choiceMsg},#{influence1},#{influence2},#{influence3},#{influence4},#{jump},#{scriptId})")
    public void insertScriptChoice(ScriptChoice scriptChoice);

    @Update("update tb_script_choice set choice_msg = #{choiceMsg},influence1 = #{influence1},influence2 = #{influence2},influence3 = #{influence3},influence4 = #{influence4},jump = #{jump} where script_id = #{scriptId} AND choice_id = #{choiceId}")
    public void updateScriptChoice(ScriptChoice scriptChoice);

    //    查询剧本状态信息是否存在
    @Select("select status from tb_script_status where producer_id = #{producerId} AND script_id = #{scriptId}")
    public Integer selectIfScriptExist(Integer producerId, Integer scriptId);

    //    若为第一次保存剧本 则保存剧本信息
    @Insert("insert into tb_script_status (script_id,status,producer_id,is_official,is_del) values (#{scriptId},#{status},#{producerId},#{isOfficial},#{isDel})")
    public void insertScriptStatus(ScriptStatus scriptStatus);

    @Select("select * from tb_script")
    @Options(useGeneratedKeys = true, keyProperty = "scriptId")
    public List<ScriptMsg> getAllScript();

    @Select("select * from tb_script where classification = #{classification}")
    public List<ScriptMsg> getScriptMsgByClassification(String classification);

    @Select("select script_id from tb_script_status where is_official = 1")
    public List<Integer> getOfficialScriptId();

    @Select("select * from tb_script where script_id = #{scriptId}")
    public ScriptMsg getScriptById(Integer scriptId);

    @Select("select producer_id from tb_script_status where script_id = #{scriptId}")
    public Integer getProducerId(Integer scriptId);

    @Select("select status from tb_script_status where script_id = #{scriptId}")
    public Integer getScriptStatus(Integer scriptId);

    @Select("select script_id from tb_script_end where end_id = #{endId}")
    public Integer getScriptIdByEndId(Integer endId);

    @Select("select count(*) from tb_script_status where script_id = #{scriptId} AND is_del = 1")
    public Integer checkIfDel(Integer scriptId);

    @Update("update tb_script_status set is_del = 1 where script_id = #{scriptId}")
    public void logicalDelScript(Integer scriptId);

    @Select("select * from tb_script_node where script_id = #{scriptId}")
    public List<ScriptNode> getScriptNode(Integer scriptId);

    @Select("select * from tb_script_node where script_id = #{scriptId}")
    public List<ScriptNodeMsg> getScriptNodeMsg(Integer scriptId);

    @Select("SELECT COUNT(*) FROM tb_script_node WHERE script_id = #{scriptId} AND node_id = #{nodeId}")
    public Integer isNodeExist(Integer scriptId,Integer nodeId);

    @Select("select count(*) from tb_script_choice where script_id = #{scriptId} AND choice_id = #{choiceId}")
    public Integer isChoiceExist(Integer scriptId,Integer choiceId);

//    @Select("select * from tb_script_node")
//    public List<ScriptNodeMsg> getScriptNodeMsg(Integer scriptId);

    @Select("select * from tb_script_choice where script_id = #{scriptId} AND choice_id = #{choiceId}")
    public ScriptChoice getScriptNodeChoice(Integer scriptId, Integer choiceId);

//    @Select("select * from tb_script_end where script_id = #{scriptId}")
//    public List<ScriptSpecialEnd> getScriptSpecialEnd(Integer scriptId);

    @Select("select count(*) from tb_script_end where end_id = #{endId} AND script_id = #{scriptId}")
    public Integer selectIfEndExist(Integer endId, Integer scriptId);

    @Select("select script_id from tb_script_status where status = 100")
    public List<Integer> selectScriptOnline();

    @Select("select * from tb_script_end where script_id = #{scriptId}")
    public List<ScriptEnd> getScriptEndById(Integer scriptId);

    //    添加剧本结局
    @Insert("insert into tb_script_end (script_id,end_msg,influence1,influence2,influence3,influence4) values (#{scriptId},#{endMsg},#{influence1},#{influence2},#{influence3},#{influence4})")
    @Options(useGeneratedKeys = true, keyProperty = "endId", keyColumn = "end_id")
    public void insertScriptEnd(ScriptEnd scriptEnd);

    @Update("update tb_script_end set end_msg = #{endMsg},influence1 = #{influence1},influence2 = #{influence2}, influence3 = #{influence3},influence4 = #{influence4} where end_id = #{endId}")
    public void updateScriptEnd(ScriptEnd scriptEnd);

    @Select("select count(*) from tb_script_normal_end where script_id = #{scriptId}")
    public Integer selectIfScriptNormalEndExist(Integer normalEndId, Integer scriptId);

    @Select("select * from tb_script_normal_end where script_id = #{scriptId}")
    public ScriptNormalEnd getScriptNormalEndById(Integer scriptId);

    @Insert("insert into tb_script_normal_end (script_id,normal_end1,normal_end2,start_value1,end_value1,start_value2,end_value2,start_value3,end_value3,start_value4,end_value4) values (#{scriptId},#{normalEnd1},#{normalEnd2},#{startValue1},#{endValue1},#{startValue2},#{endValue2},#{startValue3},#{endValue3},#{startValue4},#{endValue4})")
    @Options(useGeneratedKeys = true, keyProperty = "normalEndId", keyColumn = "normal_end_id")
    public void insertScriptNormalEnd(ScriptNormalEnd scriptNormalEnd);

    @Update("update tb_script_normal_end set normal_end1 = #{normalEnd1},normal_end2 = #{normalEnd2} ,start_value1 = #{startValue1},end_value1 =#{endValue1},start_value2 = #{startValue2},end_value2 =#{endValue2},start_value3 = #{startValue3},end_value3 =#{endValue3},start_value4 = #{startValue4},end_value4 =#{endValue4} where normal_end_id = #{normalEndId}")
    public void updateScriptNormalEnd(ScriptNormalEnd scriptNormalEnd);

    //    剧本游玩的时候 指标没了或者满了 剧本提前结束 判断触发什么特殊的结局
    @Select("SELECT end_msg \n" +
            "FROM tb_script_end \n" +
            "WHERE (influence1 = #{influence1} OR influence2 = #{influence2} OR influence3 = #{influence3} OR influence4 = #{influence4}) \n" +
            "  AND script_id = #{scriptId}\n" +
            "ORDER BY end_id DESC \n" +
            "LIMIT 1;\n")
//    public String getScriptSpecialEnd(Integer scriptId,ScriptInfluence scriptInfluence);
    public String getScriptSpecialEnd(Integer scriptId, Integer influence1, Integer influence2, Integer influence3, Integer influence4);

    //    根据前端返回的指标数据 手动衡量应该为什么结局
    @Select("SELECT CASE WHEN #{influence1} >= start_value1 AND #{influence1} <= end_value1 AND #{influence2} >= start_value2 AND #{influence2} <= end_value2 AND #{influence3} >= start_value3 AND #{influence3} <= end_value3 AND #{influence4} >= start_value4 AND #{influence4} <= end_value4 THEN normal_end1 ELSE normal_end2 END AS result FROM tb_script_normal_end WHERE script_id = #{scriptId}")
//    public String getScriptStoryEnd(ScriptInfluence scriptInfluence,Integer scriptId);
    public String getScriptNormalEnd(Integer influence1, Integer influence2, Integer influence3, Integer influence4, Integer scriptId);

    @Select("select * from tb_script_influence_name where script_id = #{scriptId}")
    public ScriptInfluenceName getInfluenceName(Integer scriptId);

    //    记录玩家曾经玩过了什么剧本
    @Insert("insert into user_played_script (user_id,script_id) values (#{userId},#{scriptId})")
    public void rememberPlayed(Integer userId, Integer scriptId);

    @Insert("insert into script_user_score (user_id,script_id,score) values (#{userId},#{scriptId},#{score})")
    public void insertScriptScore(Integer userId, Integer scriptId, Integer score);

    @Select("select count(*) from script_user_score where user_id = #{userId} AND script_id = #{scriptId}")
    public Integer checkIfScored(Integer userId, Integer scriptId);

    @Select("select score from script_user_score where script_id =  #{scriptId}")
    public List<Integer> showScore(Integer scriptId);

    @Select("select script_id from user_played_script where user_id = #{userId}")
    public List<Integer> getPlayedScriptId(Integer userId);

    @Select("select user_id from user_played_script where user_id = #{userId} AND script_id = #{scriptId}")
    public Integer ifPlayedScript(Integer scriptId, Integer userId);

    @Select("select script_id from tb_script_status where producer_id = #{producerId}")
    public List<Integer> getScriptByProducerInRepository(Integer producerId);

    @Insert("insert into script_commit (script_id,user_id,commit_date) values (#{scriptId},#{userId},#{commitDate})")
    public void insertCommit(Integer scriptId, Integer userId, Date commitDate);

//    更新140状态 为审核中
    @Update("update tb_script_status set status = 140 where script_id = #{scriptId}")
    public void updateCommitStatus(Integer scriptId);

    @Update("update tb_script_status set status = 100 where script_id = #{scriptId}")
    public void onlineScript(Integer scriptId);

    @Update("update tb_script_status set status = 130 where script_id = #{scriptId}")
    public void rollBackScript(Integer scriptId);

    @Select("select count(*)  from script_node_position where script_id = #{scriptId} AND node_id = #{nodeId}")
    public Integer checkIfNodePositionExist(Integer scriptId, Integer nodeId);

    @Insert("insert into script_node_position (script_id,node_id,x,y) values (#{scriptId},#{nodeId},#{x},#{y})")
    public void insertNodePosition(ScriptNodePosition scriptNodePosition);

    @Update("update script_node_position set x = #{x},y = #{y} where script_id = #{scriptId} AND node_id = #{nodeId}")
    public void updateNodePosition(ScriptNodePosition scriptNodePosition);

    @Select("select * from script_node_position where script_id = #{scriptId}")
    public List<ScriptNodePosition> showNodePosition(Integer scriptId);

    @Delete("delete from tb_script_end where end_id = #{endId}")
    public void delSpecialEnd(Integer endId);

    @Select("select * from script_commit where user_id = #{userId}")
    public List<ScriptCommit> getUserCommit(Integer userId);

    @Delete("delete from tb_script_node where script_id = #{scriptId} AND node_id = #{nodeId}")
    public void delNode(Integer scriptId,Integer nodeId);

    @Update("update tb_script set classification = #{classification} where script_id = #{scriptId}")
    Integer updateScriptClassification(Integer scriptId,String classification);
}

package com.gduf.dao;

import com.gduf.pojo.script.commit.ScriptCommit;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminDAO {
    @Update("update script_commit set commit_status = 1 where commit_id = #{commitId}")
    public void pushCommit(Integer scriptId);

    @Update("update script_commit set commit_status = -1 where commit_id = #{commitId}")
    public void rollBackCommit(Integer scriptId);

    @Insert("insert into script_commit commit_msg values #{commitMsg} where commit_id = #{commitId}")
    public void insertRollBackMsg(String commitMsg,Integer commitId);

    @Select("select * from script_commit")
    public List<ScriptCommit> showAllScriptCommit();
}

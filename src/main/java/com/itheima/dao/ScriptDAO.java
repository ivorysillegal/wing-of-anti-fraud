package com.itheima.dao;

import com.itheima.domain.Book;
import com.itheima.pojo.Script;
import com.itheima.pojo.ScriptNode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScriptDAO {
//    @Insert("insert into tbl_book (type,name,description) values(#{type},#{name},#{description})")
//    public int save(Book book);

    @Insert("insert into tb_script (script_name) values (#{scriptName})")
    public int insertScript(Script script);

    @Insert("insert into tb_script_node (word,script_id,choice1,choice2,influence1,influence2,influence3,influence4,influence5,jump_for_choice1,jump_for_choice2,jump_for_stop) values (#{word},#{script_id},#{choice1},#{choice2},#{influence1},#{influence2},#{influence3},#{influence4},#{influence5},#{jump_for_choice1},#{jump_for_choice2},#{jump_for_stop})")
    public int insertScriptNode(ScriptNode scriptNode);

//    @Select("select * from tbl_book where id = #{id}")
//    public Book getById(Integer id);

    @Select("select * from tb_script where id = #{scriptId}")
    public Script getScript(Integer scriptId);

    @Select("select * from tb_script")
    @Options(useGeneratedKeys = true, keyProperty = "scriptId")
    public List<Script> getAllScript();

    @Select("select * from tb_script_node where script_id = #{scriptId}")
    public List<ScriptNode> getScriptNode(Integer scriptId);

}

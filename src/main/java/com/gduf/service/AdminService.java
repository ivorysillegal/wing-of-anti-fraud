package com.gduf.service;

import com.gduf.pojo.script.commit.ScriptCommit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface AdminService {
//    进行审核 通过opinion的值来判断是通过或否
    public boolean checkCommit(ScriptCommit scriptCommit);

//    展示所有审核过或待审核的剧本
    public List<ScriptCommit> showCommitScript(Integer scriptStatus);
}

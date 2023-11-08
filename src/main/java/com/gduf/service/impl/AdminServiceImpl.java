package com.gduf.service.impl;

import com.gduf.dao.AdminDAO;
import com.gduf.dao.ScriptDAO;
import com.gduf.pojo.script.commit.ScriptCommit;
import com.gduf.service.AdminService;
import com.gduf.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private ScriptDAO scriptDAO;

    @Autowired
    private RedisCache redisCache;

    @Override
    public boolean checkCommit(ScriptCommit scriptCommit) {
        Integer commitId = scriptCommit.getCommitId();
        String commitMsg = scriptCommit.getCommitMsg();
        Integer scriptId = scriptCommit.getScriptId();
        Integer commitStatus = scriptCommit.getCommitStatus();
        try {
            //        若通过审核 修改剧本的状态
            if (commitStatus.equals(1)) {
                adminDAO.pushCommit(commitId);
                scriptDAO.onlineScript(scriptId);
            } else {
//            若不通过审核 打回剧本状态 写评语
                adminDAO.rollBackCommit(commitId);
                adminDAO.insertRollBackMsg(commitMsg, commitId);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<ScriptCommit> showCommitScript(Integer scriptStatus) {
        //        io查数据 redis缓存加效率
        List<ScriptCommit> scriptCommits = new ArrayList<>();
        scriptCommits = redisCache.getCacheList("scriptCommit");
        if (scriptCommits.isEmpty()) {
            scriptCommits = adminDAO.showAllScriptCommit();
        }
        redisCache.setCacheList("scriptCommit", scriptCommits, 10, TimeUnit.MINUTES);

//        获取对应审核状态的剧本
        List<ScriptCommit> commits = retainAccordStatus(scriptCommits, scriptStatus);
        return commits;
    }

    private List<ScriptCommit> retainAccordStatus(List<ScriptCommit> scriptCommits, Integer scriptStatus) {
        if (!(scriptStatus.equals(1) || scriptStatus.equals(0) || scriptStatus.equals(-1)))
            return null;
        ArrayList<ScriptCommit> ret = new ArrayList<>();
        for (ScriptCommit scriptCommit : scriptCommits) {
            if (scriptCommit.getCommitStatus().equals(scriptStatus))
                ret.add(scriptCommit);
        }
        return ret;
    }
}

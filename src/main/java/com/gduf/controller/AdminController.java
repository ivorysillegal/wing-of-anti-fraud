package com.gduf.controller;

import com.gduf.pojo.script.commit.ScriptCommit;
import com.gduf.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

import static com.gduf.controller.Code.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/script/commit")
    public Result showScriptTobeCommitted(@RequestBody ScriptCommit scriptCommit) {
        boolean isCommit = adminService.checkCommit(scriptCommit);
        if (!isCommit)
            return new Result("保存审核失败", COMMIT_SCRIPT_OK, null);
        return new Result("保存审核成功", COMMIT_SCRIPT_ERR, null);
    }

    @PostMapping("/script/show")
    public Result showScriptToBeCommitted(@RequestBody Map map) {
        Integer scriptStatus = (Integer) map.get("scriptStatus");
        List<ScriptCommit> scriptCommits = adminService.showCommitScript(scriptStatus);
        if (scriptCommits.isEmpty())
            return new Result("展示审核记录失败 或审核记录已清空", COMMITTED_SHOW_ERR, null);
        return new Result("展示审核记录成功", COMMITTED_SHOW_OK, scriptCommits);
    }
}

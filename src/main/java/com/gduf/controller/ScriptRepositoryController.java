package com.gduf.controller;

import com.gduf.pojo.script.ScriptMsg;
import com.gduf.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.gduf.controller.Code.*;

@RestController
@RequestMapping("/script/repository")
public class ScriptRepositoryController {

    @Autowired
    private ScriptService scriptService;

    @GetMapping
    public Result getMyDesign(@RequestHeader String token) {
        List<ScriptMsg> scriptMsgs = scriptService.showMyDesign(token);
        if (scriptMsgs.isEmpty())
            return new Result("展示草稿箱中的剧本总体信息失败", SHOW_REPOSITORY_ERR, null);
        return new Result("展示草稿箱中的剧本总体信息成功", SHOW_REPOSITORY_OK, scriptMsgs);
    }

//    @PostMapping
//    public Result shareMyDesign() {
//
//    }

//    fork别人的剧本下来
    @PostMapping("/fork")
    public Result forkOtherDesign(@RequestHeader String token, @RequestBody Map map) {
        Integer scriptId = (Integer) map.get("scriptId");
        boolean isFork = scriptService.forkToRepository(token, scriptId);
        if (!isFork)
            return new Result("剧本复制失败", FORK_REPOSITORY_ERR, null);
        return new Result("剧本复制成功", FORK_REPOSITORY_OK, null);
    }

    @PostMapping("/commit")
    public Result commitDesign(@RequestHeader String token, @RequestBody Map map) {
        Integer scriptId = (Integer) map.get("scriptId");
        boolean isCommit = scriptService.commit(token, scriptId);
        if (!isCommit)
            return new Result("提交审核失败", COMMIT_REPOSITORY_ERR, null);
        return new Result("提交审核成功", COMMIT_REPOSITORY_OK, null);
    }

}

package com.gduf.controller;

import com.gduf.pojo.script.ScriptMsg;
import com.gduf.pojo.script.mapper.ScriptNodePositionList;
import com.gduf.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @PostMapping("/share")
    public Result shareMyDesign(@RequestHeader String token, @RequestBody Map map) {
        Integer scriptId = (Integer) map.get("scriptId");
        boolean isInsert = scriptService.insertScriptPost(token, scriptId);
        boolean isInsertFollower = scriptService.insertScriptFollower(scriptId);
//        现在先放入redis
        if (!isInsert)
            return new Result("分享剧本失败", SHARE_REPOSITORY_ERR, null);
        if (!isInsertFollower)
            return new Result("制作剧本副本失败", SHARE_REPOSITORY_ERR, null);
//        返回被fork的剧本的id
        return new Result("分享剧本成功", SHARE_REPOSITORY_OK, scriptId);
    }

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

    @PostMapping("/position")
    public Result updateNodePosition(@RequestBody ScriptNodePositionList scriptNodePositionList) {
//    public Result updateNodePosition(@RequestBody LinkedHashMap scriptNodePositionList) {
        boolean isUpload = scriptService.insertOrUpdateNodePosition(scriptNodePositionList);
        if (!isUpload)
            return new Result("节点位置信息上传失败", UPLOAD_NODE_POSITION_ERR, null);
        return new Result("节点位置信息上传成功", UPLOAD_NODE_POSITION_OK, null);
    }

    @PostMapping("/position/get")
    public Result showNodePosition(@RequestBody Map map) {
        Integer scriptId = (Integer) map.get("scriptId");
        ScriptNodePositionList scriptNodePositionList = scriptService.scriptNodePositionList(scriptId);
        if (Objects.isNull(scriptNodePositionList))
            return new Result("节点位置信息获取失败", SHOW_NODE_POSITION_ERR, null);
        return new Result("节点位置信息获取成功", SHOW_NODE_POSITION_OK, scriptNodePositionList);
    }

    @DeleteMapping()
    public Result delMyDesign(@RequestBody Map map) {
        Integer scriptId = (Integer) map.get("scriptId");
        boolean isDel = scriptService.delRepository(scriptId);
        if (isDel)
            return new Result("剧本删除成功", DEL_REPOSITORY_OK, null);
        return new Result("剧本删除失败", DEL_REPOSITORY_ERR, null);
    }
}
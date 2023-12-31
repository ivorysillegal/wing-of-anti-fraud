package com.gduf.controller;

import com.gduf.pojo.script.ScriptInfluenceChange;
import com.gduf.pojo.script.ScriptInfluenceName;
import com.gduf.pojo.script.ScriptMsg;
import com.gduf.pojo.script.mapper.*;
import com.gduf.service.ScriptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gduf.controller.Code.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/script")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    //    保存剧本信息 确认默认状态
    @PostMapping("/design/scriptMsgWithInfluence")
    public Result saveScriptMsgInfluence(@RequestHeader String token, @RequestBody ScriptMsgWithInfluenceName scriptMsgWithInfluenceName) {
        ScriptInfluenceName scriptInfluenceName = scriptMsgWithInfluenceName.getScriptInfluenceName();
        ScriptMsg scriptMsg = scriptMsgWithInfluenceName.getScriptMsg();
        ScriptMsg scriptMsgWithId;
        try {
            scriptMsgWithId = scriptService.insertOrUpdateScript(scriptMsg, scriptInfluenceName);
//             保存剧本信息
            if (Objects.isNull(scriptMsgWithId))
                return new Result("剧本总体信息保存或更新失败", SCRIPT_SAVE_ERR, null);
            boolean isAccessible = scriptService.checkScriptStatus(token, scriptMsgWithId.getScriptId());
//            确认 保存 或 更新剧本状态
            if (!isAccessible)
                return new Result("剧本总体信息保存或更新失败", SCRIPT_SAVE_ERR, null);
        } catch (Exception e) {
            return new Result("剧本总体信息保存或更新失败", SCRIPT_SAVE_ERR, null);
        }
        return new Result("剧本总体信息保存或更新成功", SCRIPT_SAVE_OK, scriptMsgWithId.getScriptId());
    }

    //    保存剧本节点
    @PostMapping("/design/scriptNodes")
    public Result saveScriptNode(@RequestBody ScriptNodes scriptNodeList) {
        List<ScriptNode> scriptNodes = scriptNodeList.getScriptNodes();
        boolean isInsert = false;
        try {
            isInsert = scriptService.insertOrUpdateScriptNodes(scriptNodes);
        } catch (Exception e) {
            return new Result("剧本节点保存或更新失败", SCRIPT_NODES_SAVE_ERR, null);
        }
        if (!isInsert)
            return new Result("剧本节点保存或更新失败", SCRIPT_NODES_SAVE_ERR, null);
        else return new Result("剧本节点保存或更新成功", SCRIPT_NODES_SAVE_OK, null);
    }

    @PostMapping("/design/scriptEnds")
    public Result saveScriptEnd(@RequestBody ScriptSpecialEnd scriptSpecialEnd) {
        Integer endId;
        try {
            endId = scriptService.insertOrUpdateScriptEnds(scriptSpecialEnd);
            if (Objects.isNull(endId))
                return new Result("剧本结局保存或更新失败", SCRIPT_ENDS_SAVE_ERR, null);
        } catch (Exception e) {
            return new Result("剧本结局保存或更新失败", SCRIPT_ENDS_SAVE_ERR, null);
        }
        return new Result("剧本结局保存或更新成功", SCRIPT_ENDS_SAVE_OK, endId);
    }

    @PostMapping("/design/scriptNormalEnds")
    public Result saveScriptNormalEnd(@RequestBody ScriptEnds scriptEnds) {
        Integer normalEndId;
        try {
            normalEndId = scriptService.insertOrUpdateScriptNormalEnds(scriptEnds);
            if (Objects.isNull(normalEndId))
                return new Result("剧本结局保存或更新失败", SCRIPT_ENDS_SAVE_ERR, null);
        } catch (Exception e) {
            return new Result("剧本结局保存或更新失败", SCRIPT_ENDS_SAVE_ERR, null);
        }
        return new Result("剧本结局保存或更新成功", SCRIPT_ENDS_SAVE_OK, normalEndId);
    }

//    @GetMapping("/node")
//    public Result getScript(@PathVariable Integer scriptId) {
//        List<ScriptNode> scriptNode = scriptService.getScriptNode(scriptId);
//        if (scriptNode != null)
//            return new Result("剧本节点读取成功", SCRIPT_READ_OK, scriptNode);
//        else return new Result("剧本节点读取失败", SCRIPT_READ_ERR, null);
//    }

    @GetMapping
    public Result showAllScript() {
        List<ScriptWithScore> allScriptOnline = scriptService.getAllScriptOnline();
        if (!(Objects.isNull(allScriptOnline)))
            return new Result("剧本读取成功", SCRIPT_READ_OK, allScriptOnline);
        else return new Result("剧本读取失败", SCRIPT_READ_ERR, null);
    }

    @PostMapping("/producer")
    public Result showProducer(@RequestBody Map map) {
        Integer scriptId = (Integer) map.get("scriptId");
        String scriptProducer = scriptService.getScriptProducer(scriptId);
        if (Objects.isNull(scriptProducer))
            return new Result("获取剧本制作者失败", SCRIPT_READ_ERR, null);
        return new Result("获取剧本制作者成功", SCRIPT_READ_OK, scriptProducer);
    }

    @PostMapping
    public Result showOfficialScript() {
        List<ScriptMsg> scriptMsg = scriptService.getOfficialScript();
        if (!Objects.isNull(scriptMsg))
            return new Result("剧本读取成功", SCRIPT_READ_OK, scriptMsg);
        else return new Result("剧本读取失败", SCRIPT_READ_ERR, null);
    }

    @PostMapping("/classification")
    public Result getScriptByClassification(@RequestBody Map map) {
        String classification = (String) map.get("classification");
        List<ScriptWithScore> scriptByClassification = scriptService.getScriptByClassification(classification);
        if (Objects.isNull(scriptByClassification)) {
            return new Result("输入类型错误 没有这种类型", SCRIPT_READ_ERR, null);
        } else if (scriptByClassification.isEmpty()) {
            return new Result("输入类型的剧本数为空", SCRIPT_READ_ERR, null);
        }
        return new Result("输入类型剧本读取成功", SCRIPT_READ_OK, scriptByClassification);
    }

    @PostMapping("/play")
    public Result loadScript(@RequestHeader String token, @RequestBody Map map) {
        int scriptId = (int) map.get("scriptId");
        Integer scriptStatus = (Integer) map.get("scriptStatus");
//        如果获取过来的scriptStatus的值是空的话 就表明要玩的是上线的剧本
//        如果不是空 就只是要玩草稿箱中的剧本而已
        Boolean isOnline = Objects.isNull(scriptStatus);
        Script script = new Script();
        try {
            if (isOnline) {
                boolean isRemember = scriptService.rememberScript(token, scriptId);
                script = mergeScript(scriptId, true);
                if (!isRemember)
                    return new Result("记录游玩操作失败", LOAD_SCRIPT_ERR, null);
            } else {
                script = mergeScript(scriptId, false);
            }
        } catch (Exception e) {
            return new Result("读取剧本失败", LOAD_SCRIPT_ERR, null);
        }
        return new Result("读取剧本成功", LOAD_SCRIPT_OK, script);
    }

    private Script mergeScript(Integer scriptId, Boolean isOnline) {
        List<ScriptNode> scriptDetail = scriptService.getScriptDetail(scriptId, isOnline);
        ScriptMsg scriptMsg = scriptService.getScriptMsg(scriptId, isOnline);
        ScriptInfluenceName scriptInfluenceName = scriptService.getScriptInfluenceName(scriptId, isOnline);
        Script script = new Script(scriptMsg, scriptDetail, scriptInfluenceName);
        return script;
    }

    @PostMapping("/play/end")
    public Result loadEnd(@RequestBody Map scriptEndValue) {
        int scriptId = (Integer) scriptEndValue.get("scriptId");
        Integer influence1 = (Integer) scriptEndValue.get("influence1");
        Integer influence2 = (Integer) scriptEndValue.get("influence2");
        Integer influence3 = (Integer) scriptEndValue.get("influence3");
        Integer influence4 = (Integer) scriptEndValue.get("influence4");
        ScriptInfluenceChange scriptInfluenceChange = new ScriptInfluenceChange(influence1, influence2, influence3, influence4);
        String scriptEnd;
        try {
            scriptEnd = scriptService.getScriptEnd(scriptId, scriptInfluenceChange);
        } catch (Exception e) {
            return new Result("读取剧本结局失败", LOAD_SCRIPT_END_ERR, null);
        }
        return new Result("剧本读取结局成功", LOAD_SCRIPT_END_OK, scriptEnd);
    }

    @PostMapping("/score")
    public Result scoreScript(@RequestHeader String token, @RequestBody Map map) {
        Integer score = (Integer) map.get("score");
        Integer scriptId = (Integer) map.get("scriptId");
        Integer isScore = scriptService.scoreScript(token, score, scriptId);
        if (isScore.equals(1))
            return new Result("评分成功", SCRIPT_SCORE_OK, null);
        else if (isScore.equals(0))
            return new Result("已经评过分 评分失败", SCRIPT_SCORE_SCORED, null);
        return new Result("评分错误", SCRIPT_SCORE_ERR, null);
    }

    @PostMapping("/node/del")
    public Result delScriptNode(@RequestHeader String token, @RequestBody Map map) {
        Integer nodeId = (Integer) map.get("nodeId");
        Integer scriptId = (Integer) map.get("scriptId");
        boolean isDel = scriptService.delExtraNode(scriptId, nodeId);
        if (isDel)
            return new Result("节点删除成功", DEL_NODE_OK, null);
        return new Result("节点删除失败", DEL_NODE_ERR, null);
    }
}

package com.gduf.controller;

import com.gduf.pojo.script.mapper.*;
import com.gduf.pojo.script.*;
import com.gduf.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static com.gduf.controller.Code.*;

@RestController
@CrossOrigin
@RequestMapping("/script")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    //    保存剧本信息 确认默认状态
    @PostMapping("/design/scriptMsgWithInfluence")
    public Result saveScriptMsgInfluence(@RequestHeader String token, @RequestBody ScriptMsgWithInfluenceName scriptMsgWithInfluenceName) {
        ScriptInfluenceName scriptInfluenceName = scriptMsgWithInfluenceName.getScriptInfluenceName();
        ScriptMsg scriptMsg = scriptMsgWithInfluenceName.getScriptMsg();
        try {
            ScriptMsg scriptMsgWithId = scriptService.insertOrUpdateScript(scriptMsg, scriptInfluenceName);
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
        return new Result("剧本总体信息保存或更新成功", SCRIPT_SAVE_OK, null);
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
    public Result saveScriptEnd(@RequestBody ScriptEnds scriptEnds) {
        try {
            boolean isInsert = scriptService.insertOrUpdateScriptEnds(scriptEnds);
            if (!isInsert)
                return new Result("剧本结局保存或更新失败", SCRIPT_ENDS_SAVE_ERR, null);
        } catch (Exception e) {
            return new Result("剧本结局保存或更新失败", SCRIPT_ENDS_SAVE_ERR, null);
        }
        return new Result("剧本结局保存或更新成功", SCRIPT_ENDS_SAVE_OK, null);
    }

    @GetMapping
    public Result getScript(@PathVariable Integer scriptId) {
        List<ScriptNode> scriptNode = scriptService.getScriptNode(scriptId);
        if (scriptNode != null)
            return new Result("剧本节点读取成功", SCRIPT_READ_OK, scriptNode);
        else return new Result("剧本节点读取失败", SCRIPT_READ_ERR, null);
    }

    @PostMapping
    public Result showScript() {
        List<ScriptMsg> scriptMsg = scriptService.getScript();
        if (scriptMsg != null) {
            return new Result("剧本读取成功", SCRIPT_READ_OK, scriptMsg);
        } else return new Result("剧本读取失败", SCRIPT_READ_ERR, null);
    }

    @PostMapping("/play")
    public Result loadScript(@RequestHeader String token, @RequestBody LinkedHashMap scriptValue) {
        int scriptId = (int) scriptValue.get("scriptId");
        Script script;
        try {
            boolean isRemember = scriptService.rememberScript(token, scriptId);
            script = mergeScript(scriptId);
            if (!isRemember) {
                return new Result("记录游玩操作失败", LOAD_SCRIPT_ERR, null);
            }
        } catch (Exception e) {
            return new Result("读取剧本失败", LOAD_SCRIPT_ERR, null);
        }
        return new Result("读取剧本成功", LOAD_SCRIPT_OK, script);
    }

    private Script mergeScript(Integer scriptId) {
        List<ScriptNode> scriptDetail = scriptService.getScriptDetail(scriptId);
        ScriptMsg scriptMsg = scriptService.getScriptMsg(scriptId);
        ScriptInfluenceName scriptInfluenceName = scriptService.getScriptInfluenceName(scriptId);
        Script script = new Script(scriptMsg, scriptDetail, scriptInfluenceName);
        return script;
    }

    @PostMapping("/play/end")
    public Result loadEnd(@RequestBody LinkedHashMap scriptEndValue) {
        int scriptId = (int) scriptEndValue.get("scriptId");
        int influence1 = (int) scriptEndValue.get("influence1");
        int influence2 = (int) scriptEndValue.get("influence2");
        int influence3 = (int) scriptEndValue.get("influence3");
        int influence4 = (int) scriptEndValue.get("influence4");
        ScriptInfluenceChange scriptInfluenceChange = new ScriptInfluenceChange(influence1, influence2, influence3, influence4);
        ScriptEndSent scriptEndSent;
        try {
            scriptEndSent = scriptService.getScriptEnd(scriptId, scriptInfluenceChange);
        } catch (Exception e) {
            return new Result("读取剧本结局失败", LOAD_SCRIPT_END_ERR, null);
        }
        return new Result("剧本读取结局成功", LOAD_SCRIPT_END_OK, scriptEndSent);
    }

}
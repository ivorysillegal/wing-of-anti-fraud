package com.gduf.controller;

import com.gduf.pojo.script.*;
import com.gduf.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

import static com.gduf.controller.Code.*;

@RestController
@CrossOrigin
@RequestMapping("/script")
public class ScriptController {
    @Autowired
    private ScriptService scriptService;

    //    保存剧本
    @PostMapping("/make")
    public Result saveScript(@RequestBody ScriptMsg scriptMsg) {
        try {
            if (!scriptService.insertScript(scriptMsg)
            ) return new Result("剧本保存失败", SCRIPT_READ_ERR, null);
        } catch (Exception e) {
            return new Result("剧本保存失败", SCRIPT_READ_ERR, null);
        }
        return new Result("剧本保存成功", SCRIPT_SAVE_OK, null);
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
        List<ScriptMsg> scriptMsgs = scriptService.getScript();
        if (scriptMsgs != null) {
            return new Result("剧本读取成功", SCRIPT_READ_OK, scriptMsgs);
        } else return new Result("剧本读取失败", SCRIPT_READ_ERR, null);
    }

    @PostMapping("/play")
    public Result loadScript(@RequestBody LinkedHashMap scriptValue) {
        int scriptId = (int) scriptValue.get("scriptId");
        Script script;
        try {
            script = createScript(scriptId);
        } catch (Exception e) {
            return new Result("读取剧本失败", LOAD_SCRIPT_ERR, null);
        }
        return new Result("读取剧本成功", LOAD_SCRIPT_OK, script);
    }

    @PostMapping("/play/end")
    public Result loadEnd(@RequestBody LinkedHashMap scriptEndValue) {
        int scriptId = (int) scriptEndValue.get("scriptId");
        int influence1 = (int) scriptEndValue.get("influence1");
        int influence2 = (int) scriptEndValue.get("influence2");
        int influence3 = (int) scriptEndValue.get("influence3");
        int influence4 = (int) scriptEndValue.get("influence4");
        ScriptInfluence scriptInfluence = new ScriptInfluence(influence1, influence2, influence3, influence4);
        ScriptEnd scriptEnd;
        try {
            scriptEnd = scriptService.getScriptEnd(scriptId, scriptInfluence);
        } catch (Exception e) {
            return new Result("读取剧本结局失败", LOAD_SCRIPT_END_ERR, null);
        }
        return new Result("剧本读取结局成功", LOAD_SCRIPT_OK, scriptEnd);

    }


    private Script createScript(Integer scriptId) {
        List<ScriptNode> scriptDetail = scriptService.getScriptDetail(scriptId);
        ScriptMsg scriptMsg = scriptService.getScriptMsg(scriptId);
        Script script = new Script(scriptMsg, scriptDetail);
        return script;
    }
}

package com.itheima.controller;

import com.itheima.pojo.Script;
import com.itheima.pojo.ScriptNode;
import com.itheima.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.itheima.controller.Code.*;

@RestController
@CrossOrigin
@RequestMapping("/script")
public class ScriptController {
    @Autowired
    private ScriptService scriptService;

//    保存剧本
    @PostMapping("/make")
    public Result saveScript(@RequestBody Script script) {
        try {
            if (!scriptService.insertScript(script)
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
//    @GetMapping
    public Result showScript() {
        List<Script> scripts = scriptService.getScript();
        if (scripts != null) {
            return new Result("剧本读取成功", SCRIPT_READ_OK, scripts);
        } else return new Result("剧本读取失败", SCRIPT_READ_ERR, null);
    }
}

package com.gduf.task;

import com.gduf.dao.ScriptDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

//定时任务实现类 在每天刷新剧本的平均分

@Component
public class RefreshScore {

//    此HashMap中
//    key表示scriptId，value表示剧本每天的平均分
//    springtask控制每天更新一次

    private HashMap<Integer, Integer> scoreHashMap;

    @Autowired
    private ScriptDAO scriptDAO;

    public RefreshScore() {
        if (Objects.isNull(scoreHashMap))
            this.scoreHashMap = new HashMap<>();
    }

    public HashMap<Integer, Integer> getScore() {
        return scoreHashMap;
    }

    //    corn表达式 每天执行一次
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshScore() {
//        这个方法理论上将所有的剧本平均分全部算一遍
//        先找到全部上线了的 剧本
        List<Integer> allScript = scriptDAO.selectScriptOnline();
        for (Integer scriptId : allScript) {
            List<Integer> scores = scriptDAO.showScore(scriptId);
//            针对每一个剧本 都算出他们的平均分

            double sum = 0;
            if (scores.size() == 0) return;
            for (Integer score : scores)
                sum += score;
            sum /= scores.size();

//            至此avg为每一个剧本的平均分
            int avg = (int) Math.round(sum);
            scoreHashMap.put(scriptId, avg);
        }

    }
}

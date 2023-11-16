package com.gduf.ws.utils;

import com.gduf.ws.entity.AnswerSituation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
//用户答题情况 工具类
public class AnswerSituationUtil {

    private static final Map<String, AnswerSituation> ANSWER_SITUATION = new HashMap<>();

//    新增答案
    public void addAnswer(String userId,String answer){
        boolean isScored = ANSWER_SITUATION.containsKey(userId);
        if (isScored)
            ANSWER_SITUATION.get(userId).getSelfAnswerSituations().add(answer);
        if (!isScored) {
            ArrayList<String> answers = new ArrayList<>();
            answers.add(answer);
            ANSWER_SITUATION.put(userId,new AnswerSituation(answers));
        }
    }

//    获取用户所有答案
    public AnswerSituation getAnswer(String userId){
        boolean isContain = ANSWER_SITUATION.containsKey(userId);
        if (!isContain)
            return null;
        return ANSWER_SITUATION.get(userId);
    }

//    移除答案
    public boolean removeAnswer(String userId){
        boolean isContain = ANSWER_SITUATION.containsKey(userId);
        if (!isContain)
            return false;
        ANSWER_SITUATION.remove(userId);
        return true;
    }

}

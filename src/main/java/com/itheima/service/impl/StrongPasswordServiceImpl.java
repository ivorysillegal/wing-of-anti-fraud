package com.itheima.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.druid.sql.visitor.functions.Char;
import com.fasterxml.jackson.databind.JsonNode;
import com.itheima.dao.StrongPasswordDAO;
import com.itheima.pojo.StrongPasswordQuestion;
import com.itheima.service.StrongPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Service
public class StrongPasswordServiceImpl implements StrongPasswordService {

    @Autowired
    private StrongPasswordDAO strongPasswordDAO;

    @Override
    public List<StrongPasswordQuestion> showQuestion() {
        List<StrongPasswordQuestion> questionForPassword = strongPasswordDAO.getQuestionForPassword();
        return questionForPassword;
    }

    private final String apiEndpoint = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private final String appid = "20231005001837017";
    private final String secret = "of08TE8j9Uc0CtjwSzw6";


    //    输入了中文之后 接收并且将其翻译为英文
    @Override
    public String translateText(String textToTranslate, String fromLanguage, String toLanguage) {
        // 构建请求参数
        String salt = Long.toString(System.currentTimeMillis());
        String sign = SecureUtil.md5(appid + textToTranslate + salt + secret);

        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("q", textToTranslate);
        paramMap.add("from", fromLanguage);
        paramMap.add("to", toLanguage);
        paramMap.add("appid", appid);
        paramMap.add("salt", salt);
        paramMap.add("sign", sign);

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(paramMap, headers);

        // 发送请求到翻译API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(apiEndpoint, httpEntity, JsonNode.class);

        // 处理翻译结果，你可以在这里执行你的业务逻辑
        JsonNode translationResult = response.getBody();

        JsonNode transResultArray = translationResult.get("trans_result");

        String password = "";
//        此字符串用来处理翻译之后的数据 并且加上标点符号
        for (JsonNode transResult : transResultArray) {
            String translatedText = transResult.get("dst").asText();
            System.out.println("Translated Text: " + translatedText);
            password += insertPunctuation();
            password += translatedText;
        }
        String s = password.replaceAll("\\s", "").replaceAll("'", "");
//        至此达到效果 所有的英文单词之间都将会有一个标点符号 并且删除空格
        return s;
    }

    private static char insertPunctuation() {
        char[] chars = {',', '.', '/', '?'};
        Random random = new Random();
        char eachChar = chars[random.nextInt(chars.length)];
        return eachChar;
    }

}

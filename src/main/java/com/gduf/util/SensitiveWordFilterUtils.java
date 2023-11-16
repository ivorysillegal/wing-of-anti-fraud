package com.gduf.util;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@Slf4j
public class SensitiveWordFilterUtils {

    public  <T> T sensitiveWordFilter(T t) {
        Class clazz = t.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
//            如果有属性是字符串类型的话 进行敏感词过滤
            if (type.equals(String.class)) {
                String value = null;
                try {
                    value = (String) declaredField.get(clazz);
                } catch (IllegalAccessException e) {
                    log.info(e.toString());
                }
                SensitiveWordBs.newInstance().replace(value);
            }
        }
        return t;
    }

}

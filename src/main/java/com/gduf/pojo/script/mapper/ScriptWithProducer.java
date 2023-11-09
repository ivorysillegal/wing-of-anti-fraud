package com.gduf.pojo.script.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptWithProducer {
    private ScriptWithEnd scriptWithEnd;
//    private ArrayList<String> producers;
    private String producer;
}

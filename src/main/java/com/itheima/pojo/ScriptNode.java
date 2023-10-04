package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptNode {
    private int scriptId;
    //    属于哪一个剧本?
    private int nodeId;
    //    节点的id
    private String word;
    //    交互的关键词
    private String choice1;
    private String choice2;
    //    用户的选择
    private String influence1;
    private String influence2;
    private String influence3;
    private String influence4;
    private String influence5;
    //    对五个指标所造成的影响
    private int jumpForChoice1;
    private int jumpForChoice2;
//    记录两个选择分别跳转到的节点位置 （除了随机之外均为null）
    private Integer jumpForStop;
}

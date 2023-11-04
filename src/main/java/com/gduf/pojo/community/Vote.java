package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    private Integer voteId;
    private String topic;
    private String main;
    private Integer term;
    private Integer voteChoice1;
    private Integer voteChoice2;
    private Date createTime;
    private Integer comments;
}

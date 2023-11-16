package com.gduf.ws.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreSelectedInfo {
    private UserMatchInfo userMatchInfo;
    private String userSelectedAnswer;
}

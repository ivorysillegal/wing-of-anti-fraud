package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostTopic {
    private Integer postId;
    private Integer isTelFraud;
    private Integer isCult;
    private Integer isWireFraud;
    private Integer isFinancialFraud;
    private Integer isOverseaFraud;
    private Integer isPyramidSale;
}

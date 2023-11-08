package com.gduf.pojo.community;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostTopic {
    private Integer postId;
    private Integer isTelFraud;
    private Integer isCult;
    private Integer isWireFraud;
    private Integer isFinancialFraud;
    private Integer isOverseaFraud;
    private Integer isPyramidSale;

    public PostTopic() {
        this.isTelFraud = 0;
        this.isCult = 0;
        this.isWireFraud = 0;
        this.isFinancialFraud = 0;
        this.isOverseaFraud = 0;
        this.isPyramidSale = 0;
    }
}

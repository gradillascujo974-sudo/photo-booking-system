package com.photobooking.dto;

import lombok.Data;

@Data
public class PortfolioRequest {
    private String name;
    private String descr;
    private String img;
    private String status;
    private Integer sortOrder;
    /** 管理员创建时可指定摄影师 */
    private Integer photographerId;
}

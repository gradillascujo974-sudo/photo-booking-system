package com.photobooking.dto;

import lombok.Data;

@Data
public class PortfolioVo {
    private Integer id;
    private String name;
    private String descr;
    private String img;
    private Integer photographerId;
    private String photographerName;
    private Integer readCount;
    private Integer likeCount;
    private String status;
    private Integer sortOrder;
    private String time;
}

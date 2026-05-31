package com.photobooking.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private Integer orderId;
    private Integer serverId;
    private Integer photographerId;
    private Double score;
    private String content;
}

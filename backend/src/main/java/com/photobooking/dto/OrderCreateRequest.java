package com.photobooking.dto;

import lombok.Data;

@Data
public class OrderCreateRequest {
    private Integer serverId;
    private String serverTime;
    private String phone;
    private String remark;
}

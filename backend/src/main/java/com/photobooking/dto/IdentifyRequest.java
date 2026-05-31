package com.photobooking.dto;

import lombok.Data;

@Data
public class IdentifyRequest {
    private String card;
    private String cardNo;
    private String img;
    private String intro;
}

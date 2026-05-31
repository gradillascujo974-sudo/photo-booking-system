package com.photobooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String role;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private BigDecimal account;
    private String introduce;
    private String city;
    private String styles;
    private Integer certified;
    private BigDecimal rating;
    private String wechat;
    @com.baomidou.mybatisplus.annotation.TableField("pay_qrcode")
    private String payQrcode;
}

package com.photobooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("service_order")
public class ServiceOrder {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("order_no")
    private String orderNo;
    @TableField("user_id")
    private Integer userId;
    @TableField("photographer_id")
    private Integer photographerId;
    @TableField("server_id")
    private Integer serverId;
    @TableField("server_name")
    private String serverName;
    private BigDecimal total;
    @TableField("server_time")
    private String serverTime;
    private String phone;
    private String remark;
    private String status;
    private String time;
}

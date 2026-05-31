package com.photobooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("likes")
public class LikeRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("portfolio_id")
    private Integer portfolioId;
    @TableField("user_id")
    private Integer userId;
    private String time;
}

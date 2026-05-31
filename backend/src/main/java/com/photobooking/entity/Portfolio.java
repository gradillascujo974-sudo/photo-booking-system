package com.photobooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("portfolio")
public class Portfolio {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String descr;
    private String img;
    @TableField("photographer_id")
    private Integer photographerId;
    @TableField("read_count")
    private Integer readCount;
    @TableField("like_count")
    private Integer likeCount;
    private String status;
    @TableField("sort_order")
    private Integer sortOrder;
    private String time;
}

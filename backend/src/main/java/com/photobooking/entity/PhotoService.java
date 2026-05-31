package com.photobooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("photo_service")
public class PhotoService {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String descr;
    private String content;
    private String img;
    private BigDecimal price;
    private String style;
    private String tags;
    @TableField("type_id")
    private Integer typeId;
    @TableField("photographer_id")
    private Integer photographerId;
    @TableField("read_count")
    private Integer readCount;
    @TableField("sale_count")
    private Integer saleCount;
    private String status;
}

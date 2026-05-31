package com.photobooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("photo_type")
public class PhotoType {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
}

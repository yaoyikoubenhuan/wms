package com.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色
 */
@Data
@TableName(value = "wms_role", autoResultMap = true)
public class Role {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    /** 权限 key 列表，数据库以 JSON 存储 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> permissions;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

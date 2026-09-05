package com.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门
 */
@Data
@TableName("wms_department")
public class Department {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    /** 上级部门 id，顶级为 null */
    private String parentId;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 子部门（仅构建部门树时使用） */
    @TableField(exist = false)
    private List<Department> children = new ArrayList<>();
}

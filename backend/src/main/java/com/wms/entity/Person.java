package com.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员
 */
@Data
@TableName("wms_person")
public class Person {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String phone;

    private String email;

    private String departmentId;

    private String roleId;

    private String position;

    private LocalDate entryDate;

    /** active 在职 / inactive 离职 */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

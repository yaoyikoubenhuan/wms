package com.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任务
 */
@Data
@TableName("wms_task")
public class Task {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String title;

    private String description;

    /** pending 待处理 / inProgress 进行中 / completed 已完成 */
    private String status;

    /** high 高 / medium 中 / low 低 */
    private String priority;

    private String category;

    /** 负责人（人员 id） */
    private String assigneeId;

    /** 部门 id */
    private String departmentId;

    private LocalDate deadline;

    @JsonProperty("completedAt")
    private LocalDateTime completedTime;

    @JsonProperty("createdAt")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonProperty("updatedAt")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

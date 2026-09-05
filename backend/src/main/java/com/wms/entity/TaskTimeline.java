package com.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务操作时间线
 */
@Data
@TableName("wms_task_timeline")
public class TaskTimeline {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String taskId;

    /** created 创建 / updated 更新 / completed 完成 */
    private String action;

    private String detail;

    @JsonProperty("timestamp")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

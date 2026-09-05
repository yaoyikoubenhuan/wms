package com.wms.vo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.wms.entity.Task;
import com.wms.entity.TaskTimeline;
import lombok.Data;

import java.util.List;

/**
 * 任务详情：任务字段 + 操作时间线
 */
@Data
public class TaskDetailVO {

    @JsonUnwrapped
    private Task task;

    private List<TaskTimeline> timeline;
}

package com.wms.vo;

import lombok.Data;

import java.util.Map;

/**
 * 任务统计
 */
@Data
public class TaskStatsVO {

    private long total;
    private Map<String, Long> byStatus;
    private Map<String, Long> byPriority;
}

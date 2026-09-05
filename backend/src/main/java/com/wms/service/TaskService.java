package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.BusinessException;
import com.wms.entity.Task;
import com.wms.entity.TaskTimeline;
import com.wms.mapper.TaskMapper;
import com.wms.mapper.TaskTimelineMapper;
import com.wms.vo.TaskDetailVO;
import com.wms.vo.TaskStatsVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskTimelineMapper timelineMapper;

    /** 字段名 -> 中文名（用于时间线记录） */
    private static final Map<String, String> FIELD_LABELS = new LinkedHashMap<>();

    static {
        FIELD_LABELS.put("title", "任务名称");
        FIELD_LABELS.put("description", "描述");
        FIELD_LABELS.put("priority", "优先级");
        FIELD_LABELS.put("assigneeId", "负责人");
        FIELD_LABELS.put("departmentId", "部门");
        FIELD_LABELS.put("category", "分类标签");
        FIELD_LABELS.put("deadline", "截止日期");
        FIELD_LABELS.put("status", "状态");
    }

    public TaskService(TaskMapper taskMapper, TaskTimelineMapper timelineMapper) {
        this.taskMapper = taskMapper;
        this.timelineMapper = timelineMapper;
    }

    public List<Task> list(String keyword, String status, String priority) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Task::getTitle, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Task::getStatus, status);
        }
        if (StringUtils.hasText(priority)) {
            wrapper.eq(Task::getPriority, priority);
        }
        wrapper.orderByDesc(Task::getCreateTime);
        return taskMapper.selectList(wrapper);
    }

    public TaskDetailVO getDetail(String id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        List<TaskTimeline> timeline = timelineMapper.selectList(
                new LambdaQueryWrapper<TaskTimeline>()
                        .eq(TaskTimeline::getTaskId, id)
                        .orderByDesc(TaskTimeline::getCreateTime));
        TaskDetailVO vo = new TaskDetailVO();
        vo.setTask(task);
        vo.setTimeline(timeline);
        return vo;
    }

    @Transactional
    public Task create(Task taskData) {
        if (!StringUtils.hasText(taskData.getTitle())) {
            throw new BusinessException("请输入任务名称");
        }
        taskData.setId(null);
        taskData.setStatus("pending");
        if (!StringUtils.hasText(taskData.getPriority())) {
            taskData.setPriority("medium");
        }
        taskData.setCompletedTime(null);
        taskMapper.insert(taskData);

        TaskTimeline timeline = new TaskTimeline();
        timeline.setTaskId(taskData.getId());
        timeline.setAction("created");
        timeline.setDetail("任务创建");
        timelineMapper.insert(timeline);
        return taskData;
    }

    @Transactional
    public void update(String id, Task updates) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        List<String> changed = new ArrayList<>();
        if (updates.getTitle() != null && !updates.getTitle().equals(task.getTitle())) {
            task.setTitle(updates.getTitle());
            changed.add(FIELD_LABELS.get("title"));
        }
        if (updates.getDescription() != null && !updates.getDescription().equals(task.getDescription())) {
            task.setDescription(updates.getDescription());
            changed.add(FIELD_LABELS.get("description"));
        }
        if (updates.getPriority() != null && !updates.getPriority().equals(task.getPriority())) {
            task.setPriority(updates.getPriority());
            changed.add(FIELD_LABELS.get("priority"));
        }
        if (!java.util.Objects.equals(updates.getAssigneeId(), task.getAssigneeId())) {
            task.setAssigneeId(updates.getAssigneeId());
            changed.add(FIELD_LABELS.get("assigneeId"));
        }
        if (updates.getCategory() != null && !updates.getCategory().equals(task.getCategory())) {
            task.setCategory(updates.getCategory());
            changed.add(FIELD_LABELS.get("category"));
        }
        if (!java.util.Objects.equals(updates.getDeadline(), task.getDeadline())) {
            task.setDeadline(updates.getDeadline());
            changed.add(FIELD_LABELS.get("deadline"));
        }
        taskMapper.updateById(task);

        if (!changed.isEmpty()) {
            TaskTimeline timeline = new TaskTimeline();
            timeline.setTaskId(id);
            timeline.setAction("updated");
            timeline.setDetail("更新字段: " + String.join(", ", changed));
            timelineMapper.insert(timeline);
        }
    }

    @Transactional
    public void changeStatus(String id, String newStatus) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        String oldStatus = task.getStatus();
        if (newStatus.equals(oldStatus)) {
            return;
        }
        task.setStatus(newStatus);
        if ("completed".equals(newStatus)) {
            task.setCompletedTime(LocalDateTime.now());
        } else {
            // 重新打开任务时清除完成时间
            task.setCompletedTime(null);
        }
        taskMapper.updateById(task);

        TaskTimeline timeline = new TaskTimeline();
        timeline.setTaskId(id);
        Map<String, String> statusLabels = new HashMap<>();
        statusLabels.put("pending", "待处理");
        statusLabels.put("inProgress", "进行中");
        statusLabels.put("completed", "已完成");
        if ("completed".equals(newStatus)) {
            timeline.setAction("completed");
            timeline.setDetail("任务标记为已完成");
        } else {
            timeline.setAction("updated");
            timeline.setDetail("状态由「" + statusLabels.getOrDefault(oldStatus, oldStatus)
                    + "」变更为「" + statusLabels.getOrDefault(newStatus, newStatus) + "」");
        }
        timelineMapper.insert(timeline);
    }

    @Transactional
    public void delete(String id) {
        taskMapper.deleteById(id);
        timelineMapper.delete(new LambdaQueryWrapper<TaskTimeline>().eq(TaskTimeline::getTaskId, id));
    }

    public TaskStatsVO statistics() {
        List<Task> tasks = taskMapper.selectList(null);
        TaskStatsVO vo = new TaskStatsVO();
        vo.setTotal(tasks.size());

        Map<String, Long> byStatus = new LinkedHashMap<>();
        byStatus.put("pending", 0L);
        byStatus.put("inProgress", 0L);
        byStatus.put("completed", 0L);
        byStatus.put("overdue", 0L);
        Map<String, Long> byPriority = new LinkedHashMap<>();
        byPriority.put("high", 0L);
        byPriority.put("medium", 0L);
        byPriority.put("low", 0L);

        LocalDate today = LocalDate.now();
        for (Task t : tasks) {
            if (byStatus.containsKey(t.getStatus())) {
                byStatus.merge(t.getStatus(), 1L, Long::sum);
            }
            if (byPriority.containsKey(t.getPriority())) {
                byPriority.merge(t.getPriority(), 1L, Long::sum);
            }
            if (t.getDeadline() != null && t.getDeadline().isBefore(today) && !"completed".equals(t.getStatus())) {
                byStatus.merge("overdue", 1L, Long::sum);
            }
        }
        vo.setByStatus(byStatus);
        vo.setByPriority(byPriority);
        return vo;
    }
}

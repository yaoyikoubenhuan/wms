package com.wms.controller;

import com.wms.common.Result;
import com.wms.entity.Task;
import com.wms.service.TaskService;
import com.wms.vo.TaskDetailVO;
import com.wms.vo.TaskStatsVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public Result<List<Task>> list(@RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String priority) {
        return Result.ok(taskService.list(keyword, status, priority));
    }

    @GetMapping("/statistics")
    public Result<TaskStatsVO> statistics() {
        return Result.ok(taskService.statistics());
    }

    @GetMapping("/{id}")
    public Result<TaskDetailVO> get(@PathVariable String id) {
        return Result.ok(taskService.getDetail(id));
    }

    @PostMapping
    public Result<Task> create(@RequestBody Task task) {
        return Result.ok(taskService.create(task));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable String id, @RequestBody Task task) {
        taskService.update(id, task);
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        taskService.changeStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        taskService.delete(id);
        return Result.ok();
    }
}

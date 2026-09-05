package com.wms.controller;

import com.wms.common.Result;
import com.wms.entity.Department;
import com.wms.service.DepartmentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/tree")
    public Result<List<Department>> tree() {
        return Result.ok(departmentService.tree());
    }

    @GetMapping
    public Result<List<Department>> list() {
        return Result.ok(departmentService.list());
    }

    @PostMapping
    public Result<Department> create(@RequestBody Department department) {
        return Result.ok(departmentService.create(department));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable String id, @RequestBody Department department) {
        departmentService.update(id, department);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        departmentService.delete(id);
        return Result.ok();
    }
}

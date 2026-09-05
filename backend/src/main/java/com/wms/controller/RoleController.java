package com.wms.controller;

import com.wms.common.Result;
import com.wms.entity.Role;
import com.wms.service.RoleService;
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
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public Result<List<Role>> list() {
        return Result.ok(roleService.list());
    }

    @PostMapping
    public Result<Role> create(@RequestBody Role role) {
        return Result.ok(roleService.create(role));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable String id, @RequestBody Role role) {
        roleService.update(id, role);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return Result.ok();
    }
}

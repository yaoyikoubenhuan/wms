package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.BusinessException;
import com.wms.entity.Person;
import com.wms.entity.Role;
import com.wms.mapper.PersonMapper;
import com.wms.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RoleService {

    private final RoleMapper roleMapper;
    private final PersonMapper personMapper;

    public RoleService(RoleMapper roleMapper, PersonMapper personMapper) {
        this.roleMapper = roleMapper;
        this.personMapper = personMapper;
    }

    public List<Role> list() {
        return roleMapper.selectList(
                new LambdaQueryWrapper<Role>().orderByAsc(Role::getCreateTime));
    }

    public Role create(Role role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new BusinessException("请输入角色名称");
        }
        role.setId(null);
        if (role.getPermissions() == null) {
            role.setPermissions(Collections.emptyList());
        }
        roleMapper.insert(role);
        return role;
    }

    public void update(String id, Role updates) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        role.setName(updates.getName());
        role.setDescription(updates.getDescription());
        if (updates.getPermissions() != null) {
            role.setPermissions(updates.getPermissions());
        }
        roleMapper.updateById(role);
    }

    public void delete(String id) {
        Long used = personMapper.selectCount(
                new LambdaQueryWrapper<Person>().eq(Person::getRoleId, id));
        if (used > 0) {
            throw new BusinessException("该角色正在使用中，无法删除");
        }
        roleMapper.deleteById(id);
    }
}

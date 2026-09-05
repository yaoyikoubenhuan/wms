package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.BusinessException;
import com.wms.entity.Department;
import com.wms.mapper.DepartmentMapper;
import com.wms.mapper.PersonMapper;
import com.wms.entity.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final PersonMapper personMapper;

    public DepartmentService(DepartmentMapper departmentMapper, PersonMapper personMapper) {
        this.departmentMapper = departmentMapper;
        this.personMapper = personMapper;
    }

    /** 平铺列表（按创建时间排序） */
    public List<Department> list() {
        return departmentMapper.selectList(
                new LambdaQueryWrapper<Department>().orderByAsc(Department::getCreateTime));
    }

    /** 部门树 */
    public List<Department> tree() {
        List<Department> all = list();
        Map<String, Department> map = new HashMap<>();
        for (Department d : all) {
            d.setChildren(new ArrayList<>());
            map.put(d.getId(), d);
        }
        List<Department> roots = new ArrayList<>();
        for (Department d : all) {
            Department parent = d.getParentId() == null ? null : map.get(d.getParentId());
            if (parent != null) {
                parent.getChildren().add(d);
            } else {
                roots.add(d);
            }
        }
        return roots;
    }

    public Department create(Department dept) {
        if (dept.getName() == null || dept.getName().trim().isEmpty()) {
            throw new BusinessException("请输入部门名称");
        }
        dept.setId(null);
        if (dept.getParentId() != null && departmentMapper.selectById(dept.getParentId()) == null) {
            dept.setParentId(null);
        }
        departmentMapper.insert(dept);
        return dept;
    }

    public void update(String id, Department updates) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        String newParentId = updates.getParentId();
        if (newParentId != null) {
            if (newParentId.equals(id)) {
                throw new BusinessException("上级部门不能选择自己");
            }
            // 不允许选择自己的子孙部门作为上级（会造成循环引用）
            if (isDescendant(id, newParentId)) {
                throw new BusinessException("不能将子部门设置为上级部门");
            }
        }
        dept.setName(updates.getName());
        dept.setParentId(newParentId);
        dept.setDescription(updates.getDescription());
        departmentMapper.updateById(dept);
    }

    /** 判断 targetId 是否是 ancestorId 的子孙 */
    private boolean isDescendant(String ancestorId, String targetId) {
        List<Department> all = list();
        Map<String, Department> map = new HashMap<>();
        all.forEach(d -> map.put(d.getId(), d));
        String cursor = targetId;
        while (cursor != null) {
            if (cursor.equals(ancestorId)) {
                return true;
            }
            Department node = map.get(cursor);
            cursor = node == null ? null : node.getParentId();
        }
        return false;
    }

    public void delete(String id) {
        Long childCount = departmentMapper.selectCount(
                new LambdaQueryWrapper<Department>().eq(Department::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("该部门下有子部门，无法删除");
        }
        Long memberCount = personMapper.selectCount(
                new LambdaQueryWrapper<Person>().eq(Person::getDepartmentId, id));
        if (memberCount > 0) {
            throw new BusinessException("该部门下有人员，无法删除");
        }
        departmentMapper.deleteById(id);
    }
}

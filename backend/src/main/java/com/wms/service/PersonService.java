package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wms.common.BusinessException;
import com.wms.entity.Person;
import com.wms.entity.Task;
import com.wms.mapper.PersonMapper;
import com.wms.mapper.TaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PersonService {

    private final PersonMapper personMapper;
    private final TaskMapper taskMapper;

    public PersonService(PersonMapper personMapper, TaskMapper taskMapper) {
        this.personMapper = personMapper;
        this.taskMapper = taskMapper;
    }

    public List<Person> list(String keyword, String departmentId) {
        LambdaQueryWrapper<Person> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Person::getName, keyword)
                    .or().like(Person::getPosition, keyword));
        }
        if (StringUtils.hasText(departmentId)) {
            wrapper.eq(Person::getDepartmentId, departmentId);
        }
        wrapper.orderByDesc(Person::getCreateTime);
        return personMapper.selectList(wrapper);
    }

    public Person getById(String id) {
        Person person = personMapper.selectById(id);
        if (person == null) {
            throw new BusinessException("人员不存在");
        }
        return person;
    }

    public Person create(Person person) {
        if (!StringUtils.hasText(person.getName())) {
            throw new BusinessException("请输入姓名");
        }
        if (!StringUtils.hasText(person.getDepartmentId())) {
            throw new BusinessException("请选择部门");
        }
        person.setId(null);
        if (!StringUtils.hasText(person.getRoleId())) {
            person.setRoleId("role-member");
        }
        person.setStatus("active");
        personMapper.insert(person);
        return person;
    }

    public void update(String id, Person updates) {
        Person person = personMapper.selectById(id);
        if (person == null) {
            throw new BusinessException("人员不存在");
        }
        person.setName(updates.getName());
        person.setPhone(updates.getPhone());
        person.setEmail(updates.getEmail());
        person.setDepartmentId(updates.getDepartmentId());
        person.setRoleId(updates.getRoleId());
        person.setPosition(updates.getPosition());
        person.setEntryDate(updates.getEntryDate());
        if (StringUtils.hasText(updates.getStatus())) {
            person.setStatus(updates.getStatus());
        }
        personMapper.updateById(person);
    }

    public void delete(String id) {
        personMapper.deleteById(id);
        // 解除该人员与任务的关联，避免产生孤儿数据
        taskMapper.update(null, new LambdaUpdateWrapper<Task>()
                .eq(Task::getAssigneeId, id)
                .set(Task::getAssigneeId, null));
    }
}

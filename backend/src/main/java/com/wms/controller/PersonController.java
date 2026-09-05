package com.wms.controller;

import com.wms.common.Result;
import com.wms.entity.Person;
import com.wms.service.PersonService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public Result<List<Person>> list(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String departmentId) {
        return Result.ok(personService.list(keyword, departmentId));
    }

    @GetMapping("/{id}")
    public Result<Person> get(@PathVariable String id) {
        return Result.ok(personService.getById(id));
    }

    @PostMapping
    public Result<Person> create(@RequestBody Person person) {
        return Result.ok(personService.create(person));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable String id, @RequestBody Person person) {
        personService.update(id, person);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        personService.delete(id);
        return Result.ok();
    }
}

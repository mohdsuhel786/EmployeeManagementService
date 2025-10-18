package com.hrms.employee_service.controllers;

import com.hrms.employee_service.dtos.EmployeeStatus;
import com.hrms.employee_service.dtos.EmployeeWithUserDto;
import com.hrms.employee_service.entities.Employee;
import com.hrms.employee_service.services.EmployeeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<EmployeeWithUserDto> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeDetails(id));
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<Employee>> getByManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(employeeService.getEmployeesByManager(managerId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Employee> updateStatus(@PathVariable Long id, @RequestParam EmployeeStatus status) {
        return ResponseEntity.ok(employeeService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
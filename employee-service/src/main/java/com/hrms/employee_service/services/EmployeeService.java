package com.hrms.employee_service.services;

import com.hrms.employee_service.dtos.EmployeeStatus;
import com.hrms.employee_service.dtos.EmployeeWithUserDto;
import com.hrms.employee_service.dtos.UserDto;
import com.hrms.employee_service.entities.Employee;
import com.hrms.employee_service.exception.EmployeeServiceException;
import com.hrms.employee_service.feign.UserClient;
import com.hrms.employee_service.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final UserClient userClient;

    public EmployeeService(EmployeeRepository employeeRepository, UserClient userClient) {
        this.employeeRepository = employeeRepository;
        this.userClient = userClient;
    }

    public Employee createEmployee(Employee employee) {
        UserDto user = userClient.getUserById(employee.getUserId());
        if (user == null) throw new EmployeeServiceException("User not found");
        Optional<Employee> optionalEmployee = employeeRepository.findByUserId(employee.getUserId());
        if(optionalEmployee.isPresent()) throw new EmployeeServiceException("user already employee with same user id");
        employee.setName(user.getName());
        employee.setStatus(EmployeeStatus.ACTIVE);
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public EmployeeWithUserDto getEmployeeDetails(Long id) {
        Employee employee = getEmployeeById(id);
        UserDto user = userClient.getUserById(employee.getUserId());
        return new EmployeeWithUserDto(employee, user);
    }

    public List<Employee> getEmployeesByManager(Long managerId) {
        return employeeRepository.findByManagerId(managerId);
    }

    public Employee updateStatus(Long id, EmployeeStatus status) {
        Employee employee = getEmployeeById(id);
        employee.setStatus(status);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
package com.hrms.employee_service.dtos;

import com.hrms.employee_service.entities.Employee;
import lombok.Generated;

@Generated
@SuppressWarnings("unused")
public class EmployeeWithUserDto {
    private Employee employee;
    private UserDto user;

    public EmployeeWithUserDto(Employee employee, UserDto user) {
        this.employee = employee;
        this.user = user;
    }

    // Getters and setters


    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
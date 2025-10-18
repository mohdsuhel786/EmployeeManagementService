package com.hrms.employee_service.entities;

import com.hrms.employee_service.dtos.EmployeeStatus;
import jakarta.persistence.*;
import lombok.Generated;

import java.time.LocalDate;

@Entity
@Generated
@SuppressWarnings("unused")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;      // Link to User Service
    private Long managerId;   // Link to another Employee

    private String name;
    private String department;
    private String designation;
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    @Embedded
    private Address address;

    public Employee() {
    }

    public Employee(Long id, Long userId, Long managerId, String department, String designation, LocalDate joiningDate, Address address) {
        this.id = id;
        this.userId = userId;
        this.managerId = managerId;
        this.department = department;
        this.designation = designation;
        this.joiningDate = joiningDate;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
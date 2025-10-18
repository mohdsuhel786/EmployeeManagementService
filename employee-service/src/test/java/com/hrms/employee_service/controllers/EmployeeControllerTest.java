package com.hrms.employee_service.controllers;

import com.hrms.employee_service.dtos.EmployeeStatus;
import com.hrms.employee_service.dtos.EmployeeWithUserDto;
import com.hrms.employee_service.dtos.UserDto;
import com.hrms.employee_service.entities.Address;
import com.hrms.employee_service.entities.Employee;
import com.hrms.employee_service.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee employee;

    @BeforeEach
    void setup() {
        employee = new Employee(1L,1L,100L,"Engineering","Developer",LocalDate.of(2023, 1, 1),new Address("123 Main St","Chennai","TN","600001"));
        employee.setName("John Doe");
        employee.setStatus(EmployeeStatus.ACTIVE);
    }

    @Test
    void testCreateEmployee() {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        ResponseEntity<Employee> response = employeeController.create(employee);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
    }

    @Test
    void testGetAllEmployees() {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        ResponseEntity<List<Employee>> response = employeeController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getName());
    }

    @Test
    void testGetEmployeeById() {
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);
        ResponseEntity<Employee> response = employeeController.getById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
    }
    @Test
    void testGetManagerId() {
        when(employeeService.getEmployeesByManager(1L)).thenReturn(List.of(new Employee(),new Employee()));
        ResponseEntity<List<Employee>> response = employeeController.getByManager(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }
    @Test
  void testDeleteEmployee() {
        doNothing().when(employeeService).deleteEmployee(1L);

        ResponseEntity<Void> response = employeeController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetEmployeeDetails() {
        UserDto userDto = new UserDto(100L,"johndoe","john@example.com","EMPLOYEE");


        EmployeeWithUserDto dto = new EmployeeWithUserDto(employee, userDto);
        when(employeeService.getEmployeeDetails(1L)).thenReturn(dto);

        ResponseEntity<EmployeeWithUserDto> response = employeeController.getDetails(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getEmployee().getName());
        assertEquals("johndoe", response.getBody().getUser().getName());
    }
}
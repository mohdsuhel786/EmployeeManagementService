package com.hrms.employee_service.services;

import com.hrms.employee_service.dtos.EmployeeStatus;
import com.hrms.employee_service.dtos.EmployeeWithUserDto;
import com.hrms.employee_service.dtos.UserDto;
import com.hrms.employee_service.entities.Employee;
import com.hrms.employee_service.exception.EmployeeServiceException;
import com.hrms.employee_service.exception.GlobalExceptionHandler;
import com.hrms.employee_service.feign.UserClient;
import com.hrms.employee_service.repositories.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("johndoe");
        employee.setUserId(100L);
        employee.setStatus(EmployeeStatus.ACTIVE);

        userDto = new UserDto();
        userDto.setId(100L);
        userDto.setName("johndoe");
        userDto.setEmail("john@example.com");
        userDto.setRole("EMPLOYEE");
    }

    @Test
    void testCreateEmployee() {
        when(userClient.getUserById(100L)).thenReturn(userDto);
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.createEmployee(employee);

        assertEquals("johndoe", result.getName());
        verify(employeeRepository).save(employee);
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertEquals(1L, result.getId());
        assertEquals(EmployeeStatus.ACTIVE, result.getStatus());
    }


    @Test
    void testFindAll() {
        List<Employee> mockEmployees = List.of(
                new Employee(),
                new Employee()
        );

        when(employeeRepository.findAll()).thenReturn(mockEmployees);

        List<Employee> result = employeeService.getAllEmployees();

        Assertions.assertEquals(2, result.size());
    }
    @Test
  void testCreateEmployeeUserNotFound() {


        when(userClient.getUserById(100L)).thenReturn(null);

        EmployeeServiceException exception = Assertions.assertThrows(
                EmployeeServiceException.class,
                () -> employeeService.createEmployee(employee)
        );

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetEmployeeDetails() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(userClient.getUserById(100L)).thenReturn(userDto);

        EmployeeWithUserDto result = employeeService.getEmployeeDetails(1L);

        assertEquals("johndoe", result.getEmployee().getName());
        assertEquals("johndoe", result.getUser().getName());
    }

    @Test
    void testUpdateStatus() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee updated = employeeService.updateStatus(1L, EmployeeStatus.ACTIVE);

        assertEquals(EmployeeStatus.ACTIVE, updated.getStatus());

    }

    @Test
    void testDeleteEmployee() {
        employeeService.deleteEmployee(1L);
        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void testGetEmployeesByManager() {
        List<Employee> employees = List.of(employee);
        when(employeeRepository.findByManagerId(2L)).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeesByManager(2L);

        assertEquals(1, result.size());
        assertEquals("johndoe", result.get(0).getName());
    }


    private final UserClient userClient2 = new UserClient() {
        @Override
        public UserDto getUserById(Long id) {
            return null; // Not needed for this test
        }
    };

@Test
 void testGetUserFallback() {
        Long userId = 123L;
        Throwable cause = new RuntimeException("Service unavailable");

        UserDto fallbackUser = userClient2.getUserFallback(userId, cause);

    assertNotNull(fallbackUser);

    assertEquals(userId, fallbackUser.getId());
        assertEquals("unknown", fallbackUser.getName());
        assertEquals("unavailable", fallbackUser.getEmail());
        assertEquals("GUEST", fallbackUser.getRole());
    }


    @Test
    void testHandleValidationException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        ResponseEntity<String> response = handler.handleValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody());
    }
}
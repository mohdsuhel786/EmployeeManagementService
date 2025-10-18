package com.hrms.employee_service.services;

import com.hrms.employee_service.dtos.*;
import com.hrms.employee_service.entities.Employee;
import com.hrms.employee_service.entities.Leave;
import com.hrms.employee_service.feign.UserClient;
import com.hrms.employee_service.repositories.EmployeeRepository;
import com.hrms.employee_service.repositories.LeaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private HolidayService holidayService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private LeaveService leaveService;

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
    void testApplyLeave_successful() {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setEmployeeId(1L);
        dto.setStartDate(LocalDate.of(2025, 8, 14));
        dto.setEndDate(LocalDate.of(2025, 8, 16));
        dto.setType(LeaveType.SICK);
        dto.setReason("Flu");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // Mock holidays and weekends
        when(holidayService.isHoliday(any())).thenReturn(false);
        when(leaveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));



        Leave result = leaveService.applyLeave(dto);

        assertEquals(LeaveStatus.PENDING, result.getStatus());
        assertEquals(dto.getEmployeeId(), result.getEmployeeId());
        assertEquals(dto.getReason(), result.getReason());
    }

    @Test
    void testApplyLeave_noWorkingDays_shouldThrowException() {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setEmployeeId(1L);
        dto.setStartDate(LocalDate.of(2025, 8, 15)); // Assume holiday
        dto.setEndDate(LocalDate.of(2025, 8, 15));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(holidayService.isHoliday(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> leaveService.applyLeave(dto));
    }

    @Test
    void testApproveLeave_successful() {
        Leave leave = new Leave();
        leave.setId(1L);
        leave.setStatus(LeaveStatus.PENDING);

        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(leaveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Leave result = leaveService.approveLeave(1L, 99L);

        assertEquals(LeaveStatus.APPROVED, result.getStatus());
        assertEquals(99L, result.getApprovedBy());
        assertNotNull(result.getDecisionDate());
    }

    @Test
    void testCancelLeave_successful() {
        Leave leave = new Leave();
        leave.setId(2L);
        leave.setStatus(LeaveStatus.PENDING);

        when(leaveRepository.findById(2L)).thenReturn(Optional.of(leave));
        when(leaveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Leave result = leaveService.cancelLeave(2L);

        assertEquals(LeaveStatus.CANCELLED, result.getStatus());
    }
    @Test
    void testGetAllLeavesByEmployee() {
        Long employeeId = 1L;
        List<Leave> mockLeaves = List.of(new Leave(), new Leave());
        when(leaveRepository.findByEmployeeId(employeeId)).thenReturn(mockLeaves);

        List<Leave> result = leaveService.getAllLeavesByEmployee(employeeId);

        assertEquals(2, result.size());
        verify(leaveRepository).findByEmployeeId(employeeId);
    }

    @Test
    void testGetAllAppliedLeavesForManager() {
        Long managerId = 10L;
        Employee emp1 = new Employee(); emp1.setId(1L);
        Employee emp2 = new Employee(); emp2.setId(2L);
        List<Employee> employees = List.of(emp1, emp2);
        List<Leave> pendingLeaves = List.of(new Leave());

        when(employeeRepository.findByManagerId(managerId)).thenReturn(employees);
        when(leaveRepository.findByEmployeeIdInAndStatus(List.of(1L, 2L), LeaveStatus.PENDING))
                .thenReturn(pendingLeaves);

        List<Leave> result = leaveService.getAllAppliedLeavesForManager(managerId);

        assertEquals(1, result.size());
        verify(employeeRepository).findByManagerId(managerId);
        verify(leaveRepository).findByEmployeeIdInAndStatus(List.of(1L, 2L), LeaveStatus.PENDING);
    }


}
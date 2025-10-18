package com.hrms.employee_service.services;

import com.hrms.employee_service.dtos.LeaveRequestDTO;
import com.hrms.employee_service.dtos.LeaveStatus;
import com.hrms.employee_service.entities.Employee;
import com.hrms.employee_service.entities.Leave;
import com.hrms.employee_service.exception.EmployeeServiceException;
import com.hrms.employee_service.repositories.EmployeeRepository;
import com.hrms.employee_service.repositories.LeaveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final HolidayService holidayService;
    private final EmployeeRepository employeeRepository;

    public LeaveService(LeaveRepository leaveRepository, HolidayService holidayService, EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.holidayService = holidayService;

        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Leave applyLeave(LeaveRequestDTO dto) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(dto.getEmployeeId());
        if(optionalEmployee.isEmpty()){
            throw new EmployeeServiceException("Employee not found!");
        }
        Leave leave = new Leave();
        leave.setEmployeeId(dto.getEmployeeId());
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setType(dto.getType());
        leave.setReason(dto.getReason());
        leave.setStatus(LeaveStatus.PENDING);
        leave.setAppliedDate(LocalDate.now());

        // Validate working days
        long workingDays = calculateWorkingDays(dto.getStartDate(), dto.getEndDate());
        if (workingDays <= 0) throw new IllegalArgumentException("No working days in selected range");

        return leaveRepository.save(leave);
    }

    public Leave approveLeave(Long leaveId, Long managerId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedBy(managerId);
        leave.setDecisionDate(LocalDate.now());
        return leaveRepository.save(leave);
    }

    public Leave cancelLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus(LeaveStatus.CANCELLED);
        leave.setDecisionDate(LocalDate.now());
        return leaveRepository.save(leave);
    }
    public List<Leave> getAllLeavesByEmployee(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }

    public List<Leave> getAllAppliedLeavesForManager(Long managerId) {
        List<Employee> employees = employeeRepository.findByManagerId(managerId);
        List<Long> employeeIds = employees.stream()
                .map(Employee::getId)
                .toList();
        return leaveRepository.findByEmployeeIdInAndStatus(employeeIds, LeaveStatus.PENDING);
    }

    private long calculateWorkingDays(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1))
                .filter(date -> !holidayService.isHoliday(date) && !isWeekend(date))
                .count();
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}

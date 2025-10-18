package com.hrms.employee_service.repositories;
import com.hrms.employee_service.dtos.LeaveStatus;
import com.hrms.employee_service.entities.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // Find all leaves for a specific employee
    List<Leave> findByEmployeeId(Long employeeId);

    // Find all leaves by status
    List<Leave> findByStatus(LeaveStatus status);

    // Find leaves within a date range
    List<Leave> findByStartDateBetween(LocalDate start, LocalDate end);

    // Find pending leaves for manager approval
    List<Leave> findByStatusAndApprovedByIsNull(LeaveStatus status);
    // Find pending leaves for manager approval
    List<Leave> findByEmployeeIdInAndStatus(List<Long> employeeIds, LeaveStatus status);
}
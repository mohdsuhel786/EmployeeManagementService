package com.hrms.employee_service.controllers;

import com.hrms.employee_service.dtos.LeaveRequestDTO;
import com.hrms.employee_service.entities.Leave;
import com.hrms.employee_service.services.LeaveService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/employees/leaves")
@Validated
public class LeaveController {

    private final LeaveService leaveService;


    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/apply")
    public ResponseEntity<Leave> applyLeave(@Valid @RequestBody LeaveRequestDTO dto) {
        return ResponseEntity.ok(leaveService.applyLeave(dto));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Leave> approveLeave(@PathVariable Long id, @RequestParam Long managerId) {
        return ResponseEntity.ok(leaveService.approveLeave(id, managerId));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Leave> cancelLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.cancelLeave(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Leave>> getAllLeavesByEmployee(@PathVariable Long employeeId) {
        List<Leave> leaves = leaveService.getAllLeavesByEmployee(employeeId);
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/manager/{managerId}/pending")
    public ResponseEntity<List<Leave>> getAllAppliedLeavesForManager(@PathVariable Long managerId) {
        List<Leave> pendingLeaves = leaveService.getAllAppliedLeavesForManager(managerId);
        return ResponseEntity.ok(pendingLeaves);
    }


}
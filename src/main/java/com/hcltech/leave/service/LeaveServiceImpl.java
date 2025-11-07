package com.hcltech.leave.service;

import com.hcltech.leave.dto.*;
import com.hcltech.leave.enums.LeaveStatus;
import com.hcltech.leave.enums.LeaveType;
import com.hcltech.leave.exception.*;
import com.hcltech.leave.model.*;
import com.hcltech.leave.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveServiceImpl implements LeaveService {
    
    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    
    @Override
    @Transactional
    public LeaveResponseDTO applyLeave(LeaveRequestDTO requestDTO) {
        log.info("Processing leave request for employee: {}", requestDTO.getEmployeeId());
        
        // Validate employee
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        // Validate dates
        validateDates(requestDTO.getStartDate(), requestDTO.getEndDate());
        
        // Calculate days
        int numberOfDays = calculateLeaveDays(requestDTO.getStartDate(), requestDTO.getEndDate());
        
        // Check leave balance
        validateLeaveBalance(employee.getLeaveBalance(), requestDTO.getLeaveType(), numberOfDays);
        
        // Create leave request
        Leave leave = Leave.builder()
            .employee(employee)
            .leaveType(requestDTO.getLeaveType())
            .startDate(requestDTO.getStartDate())
            .endDate(requestDTO.getEndDate())
            .numberOfDays(numberOfDays)
            .reason(requestDTO.getReason())
            .status(LeaveStatus.PENDING)
            .appliedDate(LocalDate.now())
            .build();
        
        Leave savedLeave = leaveRepository.save(leave);
        log.info("Leave request created with ID: {}", savedLeave.getId());
        
        return mapToResponseDTO(savedLeave);
    }
    
    @Override
    @Transactional
    public LeaveResponseDTO approveOrRejectLeave(LeaveApprovalDTO approvalDTO) {
        log.info("Processing leave approval/rejection: {}", approvalDTO.getLeaveId());
        
        Leave leave = leaveRepository.findById(approvalDTO.getLeaveId())
            .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveOperationException("Leave is already processed");
        }
        
        leave.setStatus(approvalDTO.getStatus());
        leave.setApprovedBy(approvalDTO.getManagerId());
        leave.setComments(approvalDTO.getComments());
        
        // Update leave balance if approved
        if (approvalDTO.getStatus() == LeaveStatus.APPROVED) {
            updateLeaveBalance(leave);
        }
        
        Leave updatedLeave = leaveRepository.save(leave);
        
        // Send notification
        notificationService.sendLeaveStatusNotification(updatedLeave);
        
        log.info("Leave {} successfully", approvalDTO.getStatus());
        return mapToResponseDTO(updatedLeave);
    }
    
    @Override
    @Transactional
    public LeaveResponseDTO cancelLeave(Long leaveId, Long employeeId) {
        log.info("Cancelling leave: {} for employee: {}", leaveId, employeeId);
        
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        
        if (!leave.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Not authorized to cancel this leave");
        }
        
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveOperationException("Only pending leaves can be cancelled");
        }
        
        leave.setStatus(LeaveStatus.CANCELLED);
        Leave cancelledLeave = leaveRepository.save(leave);
        
        log.info("Leave cancelled successfully");
        return mapToResponseDTO(cancelledLeave);
    }
    
    @Override
    public List<LeaveResponseDTO> getEmployeeLeaves(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId).stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<LeaveResponseDTO> getTeamLeaves(Long managerId) {
        return leaveRepository.findByManagerId(managerId).stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public LeaveBalance getLeaveBalance(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return employee.getLeaveBalance();
    }
    
    // Helper methods
    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateException("Start date cannot be after end date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Cannot apply leave for past dates");
        }
    }
    
    private int calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    private void validateLeaveBalance(LeaveBalance balance, LeaveType type, int days) {
        int availableLeave = switch (type) {
            case SICK -> balance.getSickLeave();
            case CASUAL -> balance.getCasualLeave();
            case EARNED -> balance.getEarnedLeave();
        };
        
        if (availableLeave < days) {
            throw new InsufficientLeaveBalanceException(
                String.format("Insufficient %s leave balance. Available: %d, Requested: %d",
                    type, availableLeave, days));
        }
    }
    
    private void updateLeaveBalance(Leave leave) {
        LeaveBalance balance = leave.getEmployee().getLeaveBalance();
        int days = leave.getNumberOfDays();
        
        switch (leave.getLeaveType()) {
            case SICK -> balance.setSickLeave(balance.getSickLeave() - days);
            case CASUAL -> balance.setCasualLeave(balance.getCasualLeave() - days);
            case EARNED -> balance.setEarnedLeave(balance.getEarnedLeave() - days);
        }
    }
    
    private LeaveResponseDTO mapToResponseDTO(Leave leave) {
        return LeaveResponseDTO.builder()
            .id(leave.getId())
            .employeeName(leave.getEmployee().getName())
            .leaveType(leave.getLeaveType())
            .startDate(leave.getStartDate())
            .endDate(leave.getEndDate())
            .numberOfDays(leave.getNumberOfDays())
            .reason(leave.getReason())
            .status(leave.getStatus())
            .appliedDate(leave.getAppliedDate())
            .comments(leave.getComments())
            .build();
    }
}
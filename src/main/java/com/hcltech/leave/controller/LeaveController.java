package com.hcltech.leave.controller;

import com.hcltech.leave.dto.*;
import com.hcltech.leave.model.LeaveBalance;
import com.hcltech.leave.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Management", description = "APIs for managing employee leaves")
public class LeaveController {
    
    private final LeaveService leaveService;
    
    @PostMapping
    @Operation(summary = "Apply for leave")
    public ResponseEntity<LeaveResponseDTO> applyLeave(@Valid @RequestBody LeaveRequestDTO requestDTO) {
        LeaveResponseDTO response = leaveService.applyLeave(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/approve")
    @Operation(summary = "Approve or reject leave")
    public ResponseEntity<LeaveResponseDTO> approveOrRejectLeave(@Valid @RequestBody LeaveApprovalDTO approvalDTO) {
        LeaveResponseDTO response = leaveService.approveOrRejectLeave(approvalDTO);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{leaveId}/cancel")
    @Operation(summary = "Cancel leave request")
    public ResponseEntity<LeaveResponseDTO> cancelLeave(@PathVariable Long leaveId, @RequestParam Long employeeId) {
        LeaveResponseDTO response = leaveService.cancelLeave(leaveId, employeeId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get employee leave history")
    public ResponseEntity<List<LeaveResponseDTO>> getEmployeeLeaves(@PathVariable Long employeeId) {
        List<LeaveResponseDTO> leaves = leaveService.getEmployeeLeaves(employeeId);
        return ResponseEntity.ok(leaves);
    }
    
    @GetMapping("/manager/{managerId}/team")
    @Operation(summary = "Get team leaves for manager")
    public ResponseEntity<List<LeaveResponseDTO>> getTeamLeaves(@PathVariable Long managerId) {
        List<LeaveResponseDTO> leaves = leaveService.getTeamLeaves(managerId);
        return ResponseEntity.ok(leaves);
    }
    
    @GetMapping("/balance/{employeeId}")
    @Operation(summary = "Get employee leave balance")
    public ResponseEntity<LeaveBalance> getLeaveBalance(@PathVariable Long employeeId) {
        LeaveBalance balance = leaveService.getLeaveBalance(employeeId);
        return ResponseEntity.ok(balance);
    }
}
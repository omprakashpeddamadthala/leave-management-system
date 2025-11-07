package com.hcltech.leave.service;

import com.hcltech.leave.dto.*;
import com.hcltech.leave.model.LeaveBalance;
import java.util.List;

public interface LeaveService {
    LeaveResponseDTO applyLeave(LeaveRequestDTO requestDTO);
    LeaveResponseDTO approveOrRejectLeave(LeaveApprovalDTO approvalDTO);
    LeaveResponseDTO cancelLeave(Long leaveId, Long employeeId);
    List<LeaveResponseDTO> getEmployeeLeaves(Long employeeId);
    List<LeaveResponseDTO> getTeamLeaves(Long managerId);
    LeaveBalance getLeaveBalance(Long employeeId);
}
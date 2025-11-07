package com.hcltech.leave.dto;

import com.hcltech.leave.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApprovalDTO {
    
    @NotNull(message = "Leave ID is required")
    private Long leaveId;
    
    @NotNull(message = "Manager ID is required")
    private Long managerId;
    
    @NotNull(message = "Status is required")
    private LeaveStatus status;
    
    private String comments;
}
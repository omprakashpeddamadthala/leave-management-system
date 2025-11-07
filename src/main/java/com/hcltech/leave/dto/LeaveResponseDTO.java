package com.hcltech.leave.dto;


import com.hcltech.leave.enums.LeaveStatus;
import com.hcltech.leave.enums.LeaveType;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponseDTO {
    private Long id;
    private String employeeName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfDays;
    private String reason;
    private LeaveStatus status;
    private LocalDate appliedDate;
    private String comments;
}
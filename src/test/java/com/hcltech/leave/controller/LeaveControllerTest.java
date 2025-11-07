package com.hcltech.leave.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.leave.dto.LeaveRequestDTO;
import com.hcltech.leave.dto.LeaveResponseDTO;
import com.hcltech.leave.dto.LeaveApprovalDTO;
import com.hcltech.leave.enums.LeaveStatus;
import com.hcltech.leave.enums.LeaveType;
import com.hcltech.leave.service.LeaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaveController.class)
class LeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LeaveService leaveService;

    @Test
    void testApplyLeave_Success() throws Exception {
        // Arrange
        LeaveRequestDTO requestDTO = LeaveRequestDTO.builder()
                .employeeId(1L)
                .leaveType(LeaveType.CASUAL)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .reason("Personal work - family function to attend")
                .build();

        LeaveResponseDTO responseDTO = LeaveResponseDTO.builder()
                .id(1L)
                .employeeName("John Doe")
                .leaveType(LeaveType.CASUAL)
                .status(LeaveStatus.PENDING)
                .numberOfDays(3)
                .build();

        when(leaveService.applyLeave(any(LeaveRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testApproveLeave_Success() throws Exception {
        // Arrange
        LeaveApprovalDTO approvalDTO = LeaveApprovalDTO.builder()
                .leaveId(1L)
                .managerId(2L)
                .status(LeaveStatus.APPROVED)
                .comments("Approved")
                .build();

        LeaveResponseDTO responseDTO = LeaveResponseDTO.builder()
                .id(1L)
                .status(LeaveStatus.APPROVED)
                .build();

        when(leaveService.approveOrRejectLeave(any(LeaveApprovalDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/leaves/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void testCancelLeave_Success() throws Exception {
        // Arrange
        LeaveResponseDTO responseDTO = LeaveResponseDTO.builder()
                .id(1L)
                .status(LeaveStatus.CANCELLED)
                .build();

        when(leaveService.cancelLeave(anyLong(), anyLong())).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/leaves/1/cancel")
                        .param("employeeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void testGetEmployeeLeaves_Success() throws Exception {
        // Arrange
        LeaveResponseDTO responseDTO = LeaveResponseDTO.builder()
                .id(1L)
                .employeeName("John Doe")
                .status(LeaveStatus.PENDING)
                .build();

        when(leaveService.getEmployeeLeaves(1L)).thenReturn(List.of(responseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/leaves/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetTeamLeaves_Success() throws Exception {
        // Arrange
        when(leaveService.getTeamLeaves(2L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/leaves/manager/2/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetLeaveBalance_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/leaves/balance/1"))
                .andExpect(status().isOk());
    }
}
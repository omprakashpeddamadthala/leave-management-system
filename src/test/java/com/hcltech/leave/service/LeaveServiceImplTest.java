package com.hcltech.leave.service;

import com.hcltech.leave.dto.*;
import com.hcltech.leave.enums.*;
import com.hcltech.leave.exception.*;
import com.hcltech.leave.model.*;
import com.hcltech.leave.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {
    
    @Mock
    private LeaveRepository leaveRepository;
    
    @Mock
    private EmployeeRepository employeeRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private LeaveServiceImpl leaveService;
    
    private Employee employee;
    private LeaveBalance leaveBalance;
    private LeaveRequestDTO leaveRequestDTO;
    
    @BeforeEach
    void setUp() {
        leaveBalance = LeaveBalance.builder()
            .id(1L)
            .sickLeave(10)
            .casualLeave(12)
            .earnedLeave(18)
            .year(2024)
            .build();
        
        employee = Employee.builder()
            .id(1L)
            .name("John Doe")
            .email("john@example.com")
            .department("IT")
            .managerId(2L)
            .leaveBalance(leaveBalance)
            .build();
        
        leaveRequestDTO = LeaveRequestDTO.builder()
            .employeeId(1L)
            .leaveType(LeaveType.CASUAL)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(3))
            .reason("Personal work")
            .build();
    }
    
    @Test
    void testApplyLeave_Success() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(leaveRepository.save(any(Leave.class))).thenAnswer(i -> {
            Leave leave = i.getArgument(0);
            leave.setId(1L);
            return leave;
        });
        
        // Act
        LeaveResponseDTO response = leaveService.applyLeave(leaveRequestDTO);
        
        // Assert
        assertNotNull(response);
        assertEquals("John Doe", response.getEmployeeName());
        assertEquals(LeaveType.CASUAL, response.getLeaveType());
        assertEquals(LeaveStatus.PENDING, response.getStatus());
        assertEquals(3, response.getNumberOfDays());
        
        verify(employeeRepository, times(1)).findById(1L);
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }
    
    @Test
    void testApplyLeave_EmployeeNotFound() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            leaveService.applyLeave(leaveRequestDTO);
        });
    }
    
    @Test
    void testApplyLeave_InsufficientBalance() {
        // Arrange
        leaveBalance.setCasualLeave(2);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        
        // Act & Assert
        assertThrows(InsufficientLeaveBalanceException.class, () -> {
            leaveService.applyLeave(leaveRequestDTO);
        });
    }
    
    @Test
    void testApplyLeave_PastDate() {
        // Arrange
        leaveRequestDTO.setStartDate(LocalDate.now().minusDays(1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        
        // Act & Assert
        assertThrows(InvalidDateException.class, () -> {
            leaveService.applyLeave(leaveRequestDTO);
        });
    }
    
    @Test
    void testApproveLeave_Success() {
        // Arrange
        Leave leave = Leave.builder()
            .id(1L)
            .employee(employee)
            .leaveType(LeaveType.CASUAL)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(3))
            .numberOfDays(3)
            .status(LeaveStatus.PENDING)
            .build();
        
        LeaveApprovalDTO approvalDTO = LeaveApprovalDTO.builder()
            .leaveId(1L)
            .managerId(2L)
            .status(LeaveStatus.APPROVED)
            .comments("Approved")
            .build();
        
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(leaveRepository.save(any(Leave.class))).thenReturn(leave);
        
        // Act
        LeaveResponseDTO response = leaveService.approveOrRejectLeave(approvalDTO);
        
        // Assert
        assertNotNull(response);
        assertEquals(LeaveStatus.APPROVED, response.getStatus());
        assertEquals(9, employee.getLeaveBalance().getCasualLeave());
        
        verify(notificationService, times(1)).sendLeaveStatusNotification(any(Leave.class));
    }
    
    @Test
    void testCancelLeave_Success() {
        // Arrange
        Leave leave = Leave.builder()
            .id(1L)
            .employee(employee)
            .status(LeaveStatus.PENDING)
            .build();
        
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(leaveRepository.save(any(Leave.class))).thenReturn(leave);
        
        // Act
        LeaveResponseDTO response = leaveService.cancelLeave(1L, 1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(LeaveStatus.CANCELLED, response.getStatus());
    }

}
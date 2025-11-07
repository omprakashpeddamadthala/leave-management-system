package com.hcltech.leave.service;

import com.hcltech.leave.model.Leave;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    
    public void sendLeaveStatusNotification(Leave leave) {
        // Mock email notification
        log.info("Sending email notification to {} - Leave {} is {}",
            leave.getEmployee().getEmail(),
            leave.getId(),
            leave.getStatus());
        
        // In real scenario, integrate with email service
        // emailService.send(leave.getEmployee().getEmail(), subject, body);
    }
}
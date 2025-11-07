package com.hcltech.leave.repository;

import com.hcltech.leave.model.Leave;
import com.hcltech.leave.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    
    List<Leave> findByEmployeeId(Long employeeId);
    
    List<Leave> findByStatus(LeaveStatus status);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.managerId = :managerId")
    List<Leave> findByManagerId(Long managerId);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.managerId = :managerId AND l.status = :status")
    List<Leave> findByManagerIdAndStatus(Long managerId, LeaveStatus status);
}
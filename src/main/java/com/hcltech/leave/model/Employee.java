package com.hcltech.leave.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String department;

    private Long managerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "leave_balance_id")
    private LeaveBalance leaveBalance;
}
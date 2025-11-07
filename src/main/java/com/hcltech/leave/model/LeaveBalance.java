package com.hcltech.leave.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer sickLeave = 10;
    private Integer casualLeave = 12;
    private Integer earnedLeave = 18;

    @Column(nullable = false)
    private Integer year;
}

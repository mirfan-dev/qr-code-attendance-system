package com.irfan.entity;

import com.irfan.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceLog {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate date;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Status status;

    // Getters, Setters, and Constructors
}

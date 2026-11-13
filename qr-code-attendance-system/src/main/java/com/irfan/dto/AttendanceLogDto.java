package com.irfan.dto;

import com.irfan.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceLogDto {

    private String id;

    private String userId;

    private String userName;

    private LocalDate date;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private Status status;
}

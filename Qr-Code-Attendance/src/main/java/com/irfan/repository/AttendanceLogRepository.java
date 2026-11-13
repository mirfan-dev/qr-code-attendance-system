package com.irfan.repository;

import com.irfan.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, String> {


    List<AttendanceLog> findByUser_UserIdOrderByDateDesc(String userId);


    Optional<AttendanceLog> findByUser_UserIdAndDate(String userId, LocalDate date);

    List<AttendanceLog> findByDateOrderByCheckInTimeAsc(LocalDate date);

    // Find attendance between from date and to date
    List<AttendanceLog> findByDateBetweenOrderByDateDesc(
            LocalDate fromDate,
            LocalDate toDate
    );
}
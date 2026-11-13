package com.irfan.repository;

import com.irfan.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, String> {

    // ⭐ Notice the underscore: User_Id
    List<AttendanceLog> findByUser_IdOrderByDateDesc(String userId);

    // ⭐ Notice the underscore: User_Id
    Optional<AttendanceLog> findByUser_IdAndDate(String userId, LocalDate date);

    List<AttendanceLog> findByDateOrderByCheckInTimeAsc(LocalDate date);
}
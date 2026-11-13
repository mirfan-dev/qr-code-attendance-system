package com.irfan.service;


import com.irfan.dto.AttendanceLogDto;
import com.irfan.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    /** Scan a QR token and record attendance (auto check-in / check-out). */
    AttendanceLogDto scan(String qrCodeToken) throws ResourceNotFoundException;

    /** All attendance logs for a user, newest first. */
    List<AttendanceLogDto> getLogsByUser(String userId);

    /** Logs for a user on a specific date. */
    List<AttendanceLogDto> getLogsByUserAndDate(String userId, LocalDate date);

    /** Logs for everyone on a specific date. */
    List<AttendanceLogDto> getLogsByDate(LocalDate date);

    /** All logs (admin view). */
    public Page<AttendanceLogDto> getAllLogs(int pageNumber, int pageSize, String sortBy, String sortDir);

    public ByteArrayInputStream exportDataInExcel(LocalDate fromDate, LocalDate toDate);
}

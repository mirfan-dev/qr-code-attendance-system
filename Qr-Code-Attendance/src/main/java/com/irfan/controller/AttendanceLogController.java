package com.irfan.controller;


import com.irfan.dto.AttendanceLogDto;
import com.irfan.dto.ScanRequest;
import com.irfan.exception.ResourceNotFoundException;
import com.irfan.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceLogController {

    private final AttendanceService attendanceService;

    /** Scan a QR payload and record attendance (auto CHECK_IN / LATE / CHECK_OUT). */
    @PostMapping("/scan")
    public ResponseEntity<AttendanceLogDto> scan(@RequestBody ScanRequest request) throws ResourceNotFoundException {
        return ResponseEntity.ok(attendanceService.scan(request.qrPayload()));
    }

    /** All logs for a user, newest first. */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceLogDto>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(attendanceService.getLogsByUser(userId));
    }

    /** Logs for a user on a specific date (?date=2026-11-10). */
    @GetMapping("/user/{userId}/date")
    public ResponseEntity<List<AttendanceLogDto>> getByUserAndDate(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getLogsByUserAndDate(userId, date));
    }

    /** All logs for everyone on a specific date. */
    @GetMapping("/date")
    public ResponseEntity<List<AttendanceLogDto>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getLogsByDate(date));
    }

    /** Every log (admin view). */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<AttendanceLogDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "date", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {

        return ResponseEntity.ok(attendanceService.getAllLogs(pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> exportDataInExcel(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {

        String fileName = "attendances_" + System.currentTimeMillis() + ".xlsx";

        ByteArrayInputStream actualData = attendanceService.exportDataInExcel(fromDate,toDate);


        InputStreamResource file = new InputStreamResource(actualData);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .contentType(MediaType.parseMediaType(
                        "application/vnd.ms-excel"
                ))
                .body(file);
    }

}
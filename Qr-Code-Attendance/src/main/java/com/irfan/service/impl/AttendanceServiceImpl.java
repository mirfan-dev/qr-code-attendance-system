package com.irfan.service.impl;


import com.irfan.dto.AttendanceLogDto;
import com.irfan.entity.AttendanceLog;
import com.irfan.entity.User;
import com.irfan.enums.Status;
import com.irfan.exception.ResourceNotFoundException;
import com.irfan.repository.AttendanceLogRepository;
import com.irfan.repository.UserRepository;
import com.irfan.service.AttendanceService;
import com.irfan.util.AttendanceHelper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceLogRepository attendanceLogRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // ------------------------------------------------------------------
    // SCAN  →  auto check-in / check-out
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public AttendanceLogDto scan(String qrPayload) throws ResourceNotFoundException {
        if (qrPayload == null || qrPayload.isBlank()) {
            throw new ResourceNotFoundException("QR payload must not be blank");
        }
        String token = qrPayload.replace("ATTENDANCE:", "").trim();

        System.out.println(" STRIPPED TOKEN: [" + token + "]");


        if (token.isBlank()) {
            throw new ResourceNotFoundException("QR payload contains no token");
        }

        User user = userRepository.findByQrCodeToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR token"));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        AttendanceLog log = attendanceLogRepository
                .findByUser_UserIdAndDate(user.getUserId(), today)
                .orElse(null);

        // --- First scan of the day → CHECK IN ---
        if (log == null) {
            AttendanceLog created = new AttendanceLog();
            created.setId(UUID.randomUUID().toString());
            created.setUser(user);
            created.setDate(today);
            created.setCheckInTime(now);
            created.setCheckOutTime(null);
            created.setStatus(Status.CHECKED_IN);
            return toDto(attendanceLogRepository.save(created));
        }

        // --- Already checked out → reject ---
        if (log.getCheckOutTime() != null) {
            throw new ResourceNotFoundException("Attendance already completed for today");
        }

        // --- Second scan of the day → CHECK OUT ---
        log.setCheckOutTime(now);
        log.setStatus(Status.CHECKED_OUT);
        return toDto(attendanceLogRepository.save(log));
    }

    // ------------------------------------------------------------------
    // QUERIES
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceLogDto> getLogsByUser(String userId) {

        return attendanceLogRepository.findByUser_UserIdOrderByDateDesc(userId)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceLogDto> getLogsByUserAndDate(String userId, LocalDate date) {

        return attendanceLogRepository.findByUser_UserIdAndDate(userId, date)
                .map(this::toDto)
                .map(List::of)
                .orElseGet(List::of);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceLogDto> getLogsByDate(LocalDate date) {
        return attendanceLogRepository.findByDateOrderByCheckInTimeAsc(date)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceLogDto> getAllLogs(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ?
                (Sort.by(sortBy).descending()) :
                (Sort.by(sortBy).ascending());

        // pageNumber default starts from 0
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        System.out.println("Total DB Records: " + attendanceLogRepository.count());
        Page<AttendanceLog> page=attendanceLogRepository.findAll(pageable);
        return page.map(attendance-> modelMapper.map(attendance,AttendanceLogDto.class));
    }

    @Override
    public ByteArrayInputStream exportDataInExcel(LocalDate fromDate, LocalDate toDate) {

        List<AttendanceLog> all =
                attendanceLogRepository.findByDateBetweenOrderByDateDesc(
                        fromDate, toDate
                );

        ByteArrayInputStream byteArrayInputStream =
                AttendanceHelper.dataToExcel(all);

        return byteArrayInputStream;
    }

    // ------------------------------------------------------------------
    // MAPPING
    // ------------------------------------------------------------------

    private AttendanceLogDto toDto(AttendanceLog log) {
        AttendanceLogDto dto = modelMapper.map(log, AttendanceLogDto.class);
        dto.setId(UUID.randomUUID().toString());
        dto.setUserId(log.getUser().getUserId());
        dto.setUserName(log.getUser().getName());
        dto.setCheckInTime(log.getCheckInTime());
        dto.setCheckOutTime(log.getCheckOutTime());
        return dto;
    }
}

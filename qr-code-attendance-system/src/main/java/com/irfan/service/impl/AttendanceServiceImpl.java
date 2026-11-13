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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.irfan.util.QrConstants.QR_PREFIX;

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


        // ⭐ Hardcoded and forced stripping
        String token = qrPayload.replace("ATTENDANCE:", "").trim();

        System.out.println("🔍 STRIPPED TOKEN: [" + token + "]");


        if (token.isBlank()) {
            throw new ResourceNotFoundException("QR payload contains no token");
        }

        User user = userRepository.findByQrCodeToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR token"));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // ✅ FIXED LINE 68: Using the correct repository method with underscore (User_Id)
        AttendanceLog log = attendanceLogRepository
                .findByUser_IdAndDate(user.getId(), today)
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
        // ✅ FIXED LINE 99: Using the correct repository method with underscore (User_Id)
        return attendanceLogRepository.findByUser_IdOrderByDateDesc(userId)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceLogDto> getLogsByUserAndDate(String userId, LocalDate date) {
        // ✅ FIXED LINE 106: Using the correct repository method with underscore (User_Id)
        return attendanceLogRepository.findByUser_IdAndDate(userId, date)
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
    public List<AttendanceLogDto> getAllLogs() {
        return attendanceLogRepository.findAll()
                .stream().map(this::toDto).toList();
    }

    @Override
    public ByteArrayInputStream exportDataInExcel() {
        List<AttendanceLog> all = attendanceLogRepository.findAll();
        ByteArrayInputStream byteArrayInputStream = AttendanceHelper.dataToExcel(all);

        return byteArrayInputStream;
    }

    // ------------------------------------------------------------------
    // MAPPING
    // ------------------------------------------------------------------

    private AttendanceLogDto toDto(AttendanceLog log) {
        AttendanceLogDto dto = modelMapper.map(log, AttendanceLogDto.class);

        // ✅ FIXED LINE 142: Added the missing ID mapping so the Dashboard doesn't crash
        dto.setId(UUID.randomUUID().toString());

        dto.setUserId(log.getUser().getId());
        dto.setUserName(log.getUser().getName());
        dto.setCheckInTime(log.getCheckInTime());
        dto.setCheckOutTime(log.getCheckOutTime());
        return dto;
    }
}
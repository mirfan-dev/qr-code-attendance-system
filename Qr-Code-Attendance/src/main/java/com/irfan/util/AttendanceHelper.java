package com.irfan.util;


import com.irfan.entity.AttendanceLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
public final class AttendanceHelper {

    public static final String[] HEADERS = {
            "ID",
            "NAME",
            "DATE",
            "CHECKEDINTIME",
            "CHECKEDOUTTIME"
    };

    public static final String SHEET_NAME = "attendance_data";

    private static final ZoneId ZONE = ZoneId.systemDefault();

    private AttendanceHelper() { }

    public static ByteArrayInputStream dataToExcel(List<AttendanceLog> list) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME);

            // ---- Styles ----
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Date / time styles — centered too
            CreationHelper helper = workbook.getCreationHelper();

            CellStyle dateStyle = createCenteredBaseStyle(workbook);
            dateStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-mm-dd"));

            CellStyle timeStyle = createCenteredBaseStyle(workbook);
            timeStyle.setDataFormat(helper.createDataFormat().getFormat("hh:mm:ss AM/PM"));

            CellStyle textStyle = createCenteredBaseStyle(workbook);

            // ---- Header row ----
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }
            headerRow.setHeightInPoints(24);   // so vertical centering is visible

            // ---- Data rows ----
            int rowIndex = 1;
            for (AttendanceLog log : list) {

                Row dataRow = sheet.createRow(rowIndex++);

                // 0. ID
                Cell idCell = dataRow.createCell(0);
                if (log.getId() != null) {
                    idCell.setCellValue(log.getId());
                }
                idCell.setCellStyle(textStyle);


                Cell nameCell = dataRow.createCell(1);
                nameCell.setCellValue(log.getUser().getName());
                nameCell.setCellStyle(textStyle);

                // 2. DATE
                Cell dateCell = dataRow.createCell(2);
                if (log.getDate() != null) {
                    dateCell.setCellValue(toExcelDate(log.getDate()));
                    dateCell.setCellStyle(dateStyle);
                } else {
                    dateCell.setCellStyle(textStyle);
                }

                // 3. CHECKEDINTIME
                Cell inCell = dataRow.createCell(3);
                if (log.getCheckInTime() != null) {
                    inCell.setCellValue(toExcelTime(log.getCheckInTime()));
                    inCell.setCellStyle(timeStyle);
                } else {
                    inCell.setCellStyle(textStyle);
                }

                // 4. CHECKEDOUTTIME
                Cell outCell = dataRow.createCell(4);
                if (log.getCheckOutTime() != null) {
                    outCell.setCellValue(toExcelTime(log.getCheckOutTime()));
                    outCell.setCellStyle(timeStyle);
                } else {
                    outCell.setCellStyle(textStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("Failed to export attendance logs to Excel", e);
            throw new RuntimeException("Failed to export attendance data to Excel", e);
        }
    }

    // ---------- helpers ----------

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    /** Header style: bold + centered (H and V). */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = createCenteredBaseStyle(workbook);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    /** Base style centered on both axes — reused by data cells. */
    private static CellStyle createCenteredBaseStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static Date toExcelDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZONE).toInstant());
    }

    private static Date toExcelTime(LocalDateTime dateTime) {
        LocalDateTime timeOnly = LocalDate.ofEpochDay(0).atTime(dateTime.toLocalTime());
        return Date.from(timeOnly.atZone(ZONE).toInstant());
    }
}
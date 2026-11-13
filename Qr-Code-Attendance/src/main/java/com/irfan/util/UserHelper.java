package com.irfan.util;


import com.irfan.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@Slf4j
public final class UserHelper {

    public static final String[] HEADERS = {
            "ID",
            "NAME",
            "EMAIL",
            "GENDER",
            "ABOUT",
            "CREATED_AT",
            "UPDATED_AT"
    };

    public static final String SHEET_NAME = "user_data";

    private UserHelper() { }   // utility class — prevent instantiation

    public static ByteArrayInputStream dataToExcel(List<User> list) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME);

            // ---- Header row ----
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // ---- Date cell style ----
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper helper = workbook.getCreationHelper();
            dateStyle.setDataFormat(helper.createDataFormat()
                    .getFormat("yyyy-mm-dd hh:mm:ss"));

            // ---- Data rows ----
            int rowIndex = 1;
            for (User user : list) {

                Row dataRow = sheet.createRow(rowIndex++);

                dataRow.createCell(0).setCellValue(safe(user.getUserId()));
                dataRow.createCell(1).setCellValue(safe(user.getName()));
                dataRow.createCell(2).setCellValue(safe(user.getEmail()));
                dataRow.createCell(3).setCellValue(safe(user.getGender()));
                dataRow.createCell(4).setCellValue(safe(user.getAbout()));

                // Created at
                Cell createdCell = dataRow.createCell(5);
                if (user.getCreatedAt() != null) {
                    createdCell.setCellValue(Date.from(user.getCreatedAt()));
                    createdCell.setCellStyle(dateStyle);
                }

                // Updated at
                Cell updatedCell = dataRow.createCell(6);
                if (user.getUpdatedAt() != null) {
                    updatedCell.setCellValue(Date.from(user.getUpdatedAt()));
                    updatedCell.setCellStyle(dateStyle);
                }


            }

            // Auto-size (optional, but nice for readability)
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("Failed to export users to Excel", e);
            throw new RuntimeException("Failed to export user data to Excel", e);
        }
    }

    // ---------- helpers ----------

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}

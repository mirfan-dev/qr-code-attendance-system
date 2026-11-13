package com.irfan.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String id;

    private String name;

    private String email;

    private String about;

    private String gender;

    private String qrCodeToken; // Unique hash mapped inside the QR code
}

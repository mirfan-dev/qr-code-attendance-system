package com.irfan.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {

    private String accessToken;

    private String refreshToken;

    private UserDto user;
}

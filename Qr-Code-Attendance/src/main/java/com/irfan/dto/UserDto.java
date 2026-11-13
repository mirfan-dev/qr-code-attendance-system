package com.irfan.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String userId;

    @Size(min = 3, max = 20, message = "Invalid Name !!")
    private String name;

    //    @Email(message = "Invalid User Email !!")
    @Pattern(regexp = "^[a-z0-9][-a-z0-9._]+@([-a-z0-9]+\\.)+[a-z]{2,5}$", message = "Invalid User Email !!")
    @NotBlank(message = "Email is required !!")
    private String email;

    @NotBlank(message = "Password is required !!")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(min = 4, max = 6, message = "Invalid gender !!")
    private String gender;

    @NotBlank(message = "Write something about yourself !!")
    private String about;

    private String qrCodeToken;


//    @Pattern
//    Custom validator

    //    @ImageNameValid
    private String imageName;

    private Set<RoleDto> roles = new HashSet<>();

    private Instant createdAt;

    private Instant updatedAt;
}

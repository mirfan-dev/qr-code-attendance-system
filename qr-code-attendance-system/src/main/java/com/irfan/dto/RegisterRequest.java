package com.irfan.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {



    private String name;


    private String email;

    private String about;

    private String gender;

    private String password;
}

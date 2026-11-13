package com.irfan.controller;


import com.irfan.dto.*;
import com.irfan.exception.ResourceNotFoundException;
import com.irfan.repository.UserRepository;
import com.irfan.security.JwtToken;
import com.irfan.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final ModelMapper modelMapper;

    private final JwtToken jwtToken;

    private final UserRepository userRepository;

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {


            //created authentication
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            //authenticating
            authenticationManager.authenticate(authentication);

            //getting userdetail
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());


            UserDto userDto = modelMapper.map(userRepository.findByEmail(userDetails.getUsername()).get(), UserDto.class);
            //getting token
            String token = jwtToken.generateToken(userDto.getEmail(), true);
            String refreshToken = jwtToken.generateToken(userDto.getEmail(), false);
            JwtResponse build = JwtResponse
                    .builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .user(userDto)
                    .build();
            return ResponseEntity.ok(build);

        }catch (Exception e){
            e.printStackTrace();
            throw new ResourceNotFoundException("Email or password is incorrect");
        }


    }


    //refresh token api

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequeest
    ) {


        //refresh token ko valildate
        if (jwtToken.validateToken(refreshTokenRequeest.getRefreshToken()) && jwtToken.isRefreshToken(refreshTokenRequeest.getRefreshToken())) {

            //refresh token se username nikal rehe
            String usernameFromRefreshToken = jwtToken.extractEmail(refreshTokenRequeest.getRefreshToken());
            //user data fetch kiya hia from database
            UserDto userDto = modelMapper.map(userRepository.findByEmail(usernameFromRefreshToken).get(), UserDto.class);

            //new access token generate kiya hai.
            String accessToken = jwtToken.generateToken(userDto.getEmail(), true);
            //new refresh token
            String newRefreshToken = jwtToken.generateToken(userDto.getEmail(), false);
            JwtResponse response = JwtResponse
                    .builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken)
                    .user(userDto).build();

            return ResponseEntity.ok(response);

        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@RequestParam String email){
        return ResponseEntity.ok(email != null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/resend-customer-otp")
    public ResponseEntity<String> sendOtpForCustomer(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        authService.sendOtp(email);

        return ResponseEntity.ok("Customer verification OTP sent successfully");
    }

    @PostMapping("/resend-reset-otp")
    public ResponseEntity<String> sendOtpForPasswordReset(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        authService.sendResetOtp(email);

        return ResponseEntity.ok("Password reset OTP sent successfully");
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyEmail(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || otp == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email or OTP is missing"
            );
        }

        authService.verifyOtp(email, otp);

        return ResponseEntity.ok("OTP verified successfully");
    }


}

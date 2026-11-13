package com.irfan.service;

public interface AuthService {

    // Send OTP for password reset
    void sendResetOtp(String email);

    // Reset password using OTP
    void resetPassword(String email, String otp, String newPassword);

    // Send OTP for login or verification
    void sendOtp(String email);

    // Verify OTP sent to user
    void verifyOtp(String email, String otp);
}

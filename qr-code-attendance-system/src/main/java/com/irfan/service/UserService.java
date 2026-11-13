package com.irfan.service;



import com.irfan.dto.RegisterRequest;
import com.irfan.dto.UserDto;

import java.util.List;

public interface UserService {

    /** Register a user and generate a unique qrCodeToken. */
    UserDto register(RegisterRequest request);

    /** Fetch a user by id. */
    UserDto getById(String id);

    /** All users (admin). */
    List<UserDto> getAll();

    /** Regenerate the QR token (e.g. lost phone). */
    UserDto regenerateToken(String userId);

    public void delete(String userId);
}

package com.irfan.service.impl;

import com.irfan.dto.RegisterRequest;
import com.irfan.dto.UserDto;
import com.irfan.entity.User;
import com.irfan.repository.UserRepository;
import com.irfan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 24;   // 192 bits → 32 chars base64url

    // ---------------------------------------------------------------
    // REGISTER
    // ---------------------------------------------------------------

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setPassword(request.getPassword());
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail());
        user.setAbout(request.getAbout());
        user.setGender(request.getGender());
        user.setQrCodeToken(generateUniqueToken());

        return toDto(userRepository.save(user));
    }

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(String id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ---------------------------------------------------------------
    // REGENERATE TOKEN
    // ---------------------------------------------------------------

    @Override
    @Transactional
    public UserDto regenerateToken(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setQrCodeToken(generateUniqueToken());
        return toDto(userRepository.save(user));
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------

    @Override
    @Transactional
    public void delete(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------

    private String generateUniqueToken() {
        for (int attempt = 0; attempt < 5; attempt++) {
            byte[] bytes = new byte[TOKEN_BYTES];
            RANDOM.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            if (!userRepository.existsByQrCodeToken(token)) {
                return token;
            }
        }
        throw new IllegalStateException("Could not generate a unique QR token after 5 attempts");
    }

    private UserDto toDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
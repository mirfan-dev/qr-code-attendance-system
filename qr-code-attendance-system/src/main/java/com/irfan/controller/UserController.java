package com.irfan.controller;

import com.irfan.dto.RegisterRequest;
import com.irfan.dto.UserDto;
import com.irfan.service.QrService;
import com.irfan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final QrService qrService;

    /** Register a new user (generates a unique qrCodeToken). */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    /** Get a single user by id. */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    /** List all users. */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    /** Get the QR image (base64 PNG) for a user. */
    @GetMapping("/{id}/qr")
    public ResponseEntity<Map<String, String>> getQr(@PathVariable String id) throws Exception {
        UserDto user = userService.getById(id);
        String base64 = qrService.generateQrBase64(user.getQrCodeToken());
        return ResponseEntity.ok(Map.of(
                "qrCodeToken", user.getQrCodeToken(),
                "qrImageBase64", base64
        ));
    }

    /** Regenerate the QR token (e.g. lost device). */
    @PostMapping("/{id}/regenerate-token")
    public ResponseEntity<UserDto> regenerateToken(@PathVariable String id) {
        return ResponseEntity.ok(userService.regenerateToken(id));
    }

    /** Delete a user. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
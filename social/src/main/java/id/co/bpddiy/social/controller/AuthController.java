package id.co.bpddiy.social.controller;

import id.co.bpddiy.social.dto.AuthRequest;
import id.co.bpddiy.social.dto.AuthResponse;
import id.co.bpddiy.social.dto.UserRegistrationRequest;
import id.co.bpddiy.social.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller untuk Authentication
 * Endpoints untuk login, register, dan validasi
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API untuk autentikasi user (login, register)")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User Login
     */
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Login dengan username/email dan password")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login gagal: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * User Registration
     */
    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Registrasi user baru")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registrasi gagal: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check Username Availability
     */
    @GetMapping("/check-username/{username}")
    @Operation(summary = "Check Username", description = "Cek apakah username tersedia")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        boolean available = authService.isUsernameAvailable(username);
        
        response.put("username", username);
        response.put("available", available);
        response.put("message", available ? "Username tersedia" : "Username sudah digunakan");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check Email Availability
     */
    @GetMapping("/check-email/{email}")
    @Operation(summary = "Check Email", description = "Cek apakah email tersedia")
    public ResponseEntity<Map<String, Object>> checkEmail(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        boolean available = authService.isEmailAvailable(email);
        
        response.put("email", email);
        response.put("available", available);
        response.put("message", available ? "Email tersedia" : "Email sudah digunakan");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Logout (Client-side handled, invalidate token)
     */
    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Logout user (client-side token invalidation)")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout berhasil. Hapus token dari client.");
        return ResponseEntity.ok(response);
    }
}
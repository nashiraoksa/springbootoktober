package id.co.bpddiy.social.controller;

import id.co.bpddiy.social.dto.UserDto;
import id.co.bpddiy.social.dto.UserDtoRequest;
import id.co.bpddiy.social.model.User;
import id.co.bpddiy.social.repository.UserRepository;
import id.co.bpddiy.social.security.UserPrincipal;
import id.co.bpddiy.social.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller untuk User Profile Management
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API untuk user profile dan management")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

     @Autowired
    private UserServiceImpl userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getMethodName() {
        List<UserDto> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDtoRequest body) {
        UserDto userDto = userService.createUser(body.getUsername(), body.getEmail(), body.getPassword(),
                body.getRole());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDtoRequest body) {
        UserDto userDto = userService.updateUser(id, body.getUsername(), body.getEmail(), body.getPassword(),
                body.getRole());
        return userDto;
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable Long id){
        UserDto userDto = userService.deleteUser(id);
        return userDto;
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Current User Profile", description = "Ambil profile user yang sedang login")
    public ResponseEntity<User> getCurrentUserProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        // Hide password from response
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update User Profile", description = "Update profile user yang sedang login")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request,
                                             Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Update fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        user = userRepository.save(user);
        user.setPassword(null); // Hide password from response
        return ResponseEntity.ok(user);
    }

    /**
     * Change password
     */
    @PostMapping("/profile/change-password")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change Password", description = "Ganti password user")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Password lama tidak cocok");
            return ResponseEntity.badRequest().body(error);
        }

        // Update with new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password berhasil diubah");
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID (public profile)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get User by ID", description = "Ambil profile user berdasarkan ID (public)")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        // Hide sensitive data for public profile
        user.setPassword(null);
        user.setEmail(null);
        return ResponseEntity.ok(user);
    }

    /**
     * Search users
     */
    @GetMapping("/search")
    @Operation(summary = "Search Users", description = "Cari users berdasarkan username atau nama lengkap")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> users = userRepository.searchByUsernameOrFullName(keyword);
        
        // Hide sensitive data
        users.forEach(user -> {
            user.setPassword(null);
            user.setEmail(null);
        });
        
        return ResponseEntity.ok(users);
    }

    /**
     * Get all users dengan pagination (Admin only)
     */
    @GetMapping("/admin/all_user")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get All Users", description = "Ambil semua users dengan pagination (Admin only)")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<User> users = userRepository.findAll(pageable);
        
        // Hide passwords
        users.forEach(user -> user.setPassword(null));
        
        return ResponseEntity.ok(users);
    }

    /**
     * Get top users by post count
     */
    @GetMapping("/top-authors")
    @Operation(summary = "Get Top Authors", description = "Ambil users dengan post terbanyak")
    public ResponseEntity<List<User>> getTopAuthors(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<User> users = userRepository.findTopUsersByPostCount(pageable);
        
        // Hide sensitive data
        users.forEach(user -> {
            user.setPassword(null);
            user.setEmail(null);
        });
        
        return ResponseEntity.ok(users);
    }

    /**
     * Get user statistics (Admin only)
     */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get User Statistics", description = "Ambil statistik user (post count, comment count)")
    public ResponseEntity<List<Object[]>> getUserStatistics(@PathVariable Long id) {
        List<Object[]> statistics = userRepository.getUserStatistics(id);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Activate/Deactivate user (Admin only)
     */
    @PostMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle User Active Status", description = "Aktifkan/nonaktifkan user (Admin only)")
    public ResponseEntity<Map<String, String>> toggleUserActiveStatus(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User " + (user.getIsActive() ? "activated" : "deactivated"));
        return ResponseEntity.ok(response);
    }

    /**
     * Update user role (Admin only)
     */
    @PostMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update User Role", description = "Update role user (Admin only)")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id,
                                              @RequestParam User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        user.setRole(role);
        user = userRepository.save(user);
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }

    // DTO Classes untuk request bodies
    public static class UserProfileUpdateRequest {
        private String fullName;
        private String bio;
        private String profileImageUrl;

        // Getters and setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        
        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        // Getters and setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
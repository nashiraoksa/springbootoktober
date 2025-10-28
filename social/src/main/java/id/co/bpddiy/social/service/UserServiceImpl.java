package id.co.bpddiy.social.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import id.co.bpddiy.social.dto.UserDto;
import id.co.bpddiy.social.dto.UserProfileUpdateDto;
import id.co.bpddiy.social.exception.ResourceNotFoundException;
import id.co.bpddiy.social.model.User;
import id.co.bpddiy.social.model.User.Role;
import id.co.bpddiy.social.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



@Service
public class UserServiceImpl implements UserService {
    // DI
    @Autowired
    // mengurangi deklarasi dengan new
    // mengurangi pembuatan objek baru di memory
    private UserRepository userRepository;
    
    @Autowired
    private FileStorageService fileStorageService;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(
                u -> new UserDto(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getBio(),
                        u.getRole()))
                .toList();
    }

    @Override
    public UserDto createUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        User userResult = userRepository.save(user);

        UserDto userDto = new UserDto();
        userDto.setUsername(userResult.getUsername());
        userDto.setEmail(userResult.getEmail());
        userDto.setRole(userResult.getRole());
        return userDto;
    }

    @Override
    public UserDto updateUser(Long id, String username, String email, String password, Role role) {
        User userResult = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Tidak Ditemukan"));

        userResult.setUsername(username);
        userResult.setEmail(email);
        userResult.setPassword(password);
        userResult.setRole(role);
        userRepository.save(userResult);

        UserDto userDto = new UserDto();
        userDto.setUsername(userResult.getUsername());
        userDto.setEmail(userResult.getEmail());
        userDto.setRole(userResult.getRole());
        return userDto;
    }

    @Override
    public UserDto deleteUser(Long id) {
        User userResult = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Tidak Ditemukan"));

        userRepository.delete(userResult);

        UserDto userDto = new UserDto();
        userDto.setUsername(userResult.getUsername());
        userDto.setEmail(userResult.getEmail());
        userDto.setRole(userResult.getRole());
        return userDto;
    }

    // START HERE

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserDto updateProfile(Long userId, UserProfileUpdateDto updateDto) {
        User user = getUserById(userId);

        if (updateDto.getFullName() != null) {
            user.setFullName(updateDto.getFullName());
        }

        if (updateDto.getBio() != null) {
            user.setBio(updateDto.getBio());
        }

        if (updateDto.getProfileImageUrl() != null) {
            // Delete old profile image if exists
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                String oldFileName = extractFileNameFromUrl(user.getProfileImageUrl());
                fileStorageService.deleteFile(oldFileName);
            }
            user.setProfileImageUrl(updateDto.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        return UserDto.fromEntity(updatedUser);
    }

    /**
     * Update profile image
     */
    @Transactional
    public UserDto updateProfileImage(Long userId, String imageUrl) {
        User user = getUserById(userId);

        // Delete old profile image if exists
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            String oldFileName = extractFileNameFromUrl(user.getProfileImageUrl());
            fileStorageService.deleteFile(oldFileName);
        }

        user.setProfileImageUrl(imageUrl);
        User updatedUser = userRepository.save(user);
        return UserDto.fromEntity(updatedUser);
    }

    /**
     * Get all users with pagination, filtering, and sorting
     */
    public Page<UserDto> getAllUsers(String search, User.Role role, Pageable pageable) {
        Page<User> users;

        if (search != null && !search.isEmpty() && role != null) {
            // Filter by search and role
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRole(
                    search, search, role, pageable);
        } else if (search != null && !search.isEmpty()) {
            // Filter by search only
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable);
        } else if (role != null) {
            // Filter by role only
            users = userRepository.findByRole(role, pageable);
        } else {
            // No filter
            users = userRepository.findAll(pageable);
        }

        return users.map(UserDto::fromEntity);
    }

    /**
     * Extract filename from URL
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        int lastSlash = url.lastIndexOf('/');
        return lastSlash >= 0 ? url.substring(lastSlash + 1) : url;
    }
}
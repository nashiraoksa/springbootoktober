package id.co.bpddiy.social.dto;

import id.co.bpddiy.social.model.User;

/**
 * DTO untuk Authentication Response (Login berhasil)
 */
public class AuthResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private User.Role role;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String accessToken, Long userId, String username, String email, 
                       String fullName, User.Role role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public User.Role getRole() {
        return role;
    }
    
    public void setRole(User.Role role) {
        this.role = role;
    }
}
package id.co.bpddiy.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO untuk Authentication Request (Login)
 */
public class AuthRequest {
    
    @NotBlank(message = "Username atau email tidak boleh kosong")
    private String usernameOrEmail;
    
    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;
    
    // Constructors
    public AuthRequest() {}
    
    public AuthRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
    
    // Getters and Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }
    
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
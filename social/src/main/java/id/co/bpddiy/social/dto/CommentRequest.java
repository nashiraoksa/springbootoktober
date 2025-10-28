package id.co.bpddiy.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {
    
    @NotBlank(message = "Konten komentar tidak boleh kosong")
    @Size(min = 1, max = 1000, message = "Komentar harus antara 1-1000 karakter")
    private String content;
    
    // Constructors
    public CommentRequest() {}
    
    public CommentRequest(String content) {
        this.content = content;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
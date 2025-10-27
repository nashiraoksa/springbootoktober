package id.co.bpddiy.social.dto;

import id.co.bpddiy.social.model.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {
    
    @NotBlank(message = "Judul tidak boleh kosong")
    @Size(min = 5, max = 200, message = "Judul harus antara 5-200 karakter")
    private String title;
    
    @Size(max = 500, message = "Excerpt maksimal 500 karakter")
    private String excerpt;
    
    @NotBlank(message = "Konten tidak boleh kosong")
    private String content;
    
    private String featuredImageUrl;
    
    private Post.PostStatus status = Post.PostStatus.DRAFT;
    
    private Boolean isFeatured = false;
    
    // Constructors
    public PostRequest() {}
    
    public PostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getExcerpt() {
        return excerpt;
    }
    
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }
    
    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }
    
    public Post.PostStatus getStatus() {
        return status;
    }
    
    public void setStatus(Post.PostStatus status) {
        this.status = status;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
}

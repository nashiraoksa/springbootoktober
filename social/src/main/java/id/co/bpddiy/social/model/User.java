package id.co.bpddiy.social.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity untuk User dengan relationship ke Post dan Comment
 * Relasi: User (1) -> Posts (N), User (1) -> Comments (N)
 */
@Entity
@Table(name = "users", indexes = {
        // indexing dibutuhkan untuk mempercepat query (pengambilan data) pada kolom
        // tertentu
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username")
})
public class User {

    // Membuat primary key dengan nama kolom id,
    // dengan auto increment,
    // auto convert data type ke postgres setara Long
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NotNull kolom username
    // min 3 max 50
    // unik
    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 50, message = "Username harus antara 3-50 karakter")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    @Column(nullable = false)
    private String password;

    @Size(max = 100, message = "Nama lengkap maksimal 100 karakter")
    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // kolom enum, dengan nama kolom role
    // harus berupa string ADMIN, MODERATOR, atau USER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // CascadeType.ALL, user terhapus maka post terhapus
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    // one to many comment
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    // Enum untuk Role
    public enum Role {
        USER, ADMIN, MODERATOR
    }

    // Constructors
    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
package id.co.bpddiy.social.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="users", indexes = {
    // indexing dibutuhkan untuk mempercepat query/pengambilan data pada kolom tertentu
    @Index(name="idx_user_email", columnList="email"),
    @Index(name="idx_user_username", columnList="username"),
})
public class User {

    // membuat primary key, dengan auto increment, auto convert data type ke postgres setara dengan Long
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NotNull kolom username
    // min 3 max 50
    // unik
    @NotBlank(message = "Username harus diisi")
    @Size(min = 3, max = 50, message = "Karakter username antara 3 sampai 50")
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @NotBlank(message = "Email harus diisi")
    @Email(message = "Harus format email")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Password harus diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // kolom enum, dengan nama kolom role
    // harus berupa string ADMIN, MODERATOR, atau USER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // CascadeType.ALL, user terhapus maka post terhapus
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Post> posts;

    // one to many comment
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    public enum Role{
        ADMIN, MODERATOR, USER
    }

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

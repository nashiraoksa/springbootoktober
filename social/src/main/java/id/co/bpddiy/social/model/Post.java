package id.co.bpddiy.social.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "posts", indexes = {
    @Index(name = "idx_post_author", columnList = "author_id"),
    @Index(name = "idx_post_slug", columnList = "slug")
})
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Harus ada judul")
    @Size(min = 5, max = 200, message = "Judul 5 hingga 200 karakter")
    @Column(nullable = false, length = 200)
    private String title;

    // slug, teks singkat, biasanya untuk url
    @Column(unique = true, nullable = false, length = 100)
    private String slug;
    
    @NotBlank(message = "Konten harus diisi")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "publised_at")
    private LocalDateTime publisedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable=false, updatable = false)
    private LocalDateTime createdAt;
    
    @CreationTimestamp
    @Column(name = "updated_at", nullable=false, updatable = false)
    private LocalDateTime updatedAt;

    // Relasi ke user,
    // FetchType.LAZY, optimalisasi performa, akses db berdasarkan relasi, jika ditemukan, maka dicari data di tabel foreign key nya
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable=false, foreignKey = @ForeignKey(name="fk_post_user_author"))
    private User author;

    // one to many dengan comment
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<Comment> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublisedAt() {
        return publisedAt;
    }

    public void setPublisedAt(LocalDateTime publisedAt) {
        this.publisedAt = publisedAt;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    
}

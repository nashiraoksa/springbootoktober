package id.co.bpddiy.social.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Index(name = "idx_post_status", columnList = "status"),
    @Index(name = "idx_post_created_at", columnList = "created_at"),
    @Index(name = "idx_post_slug", columnList = "slug")
})
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Judul tidak boleh kosong")
    @Size(min = 5, max = 200, message = "Judul harus antara 5-200 karakter")
    @Column(nullable = false, length = 200)
    private String title;
    
    // slug, teks singkat, biasanya untuk URL
    @Column(unique = true, nullable = false, length = 250)
    private String slug;
    
    @Column(columnDefinition = "TEXT")
    private String excerpt;
    
    @NotBlank(message = "Konten tidak boleh kosong")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "featured_image_url")
    private String featuredImageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.DRAFT;
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;
    
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relasi ke user,
    // FetchType.LAZY , optimalisasi performa, akses db berdasarkan relasi. jika
    // ditemukan, maka dicari data di tabel foreignkey
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, 
               foreignKey = @ForeignKey(name = "fk_post_author"))
    private User author;
    
    // One to Many dengan comment
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, 
               fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Comment> comments;
    
    // Enum untuk Status Post
    public enum PostStatus {
        DRAFT, PUBLISHED, ARCHIVED, DELETED
    }
    
    // Constructors
    public Post() {}
    
    public Post(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.slug = generateSlug(title);
    }
    
    // Helper method untuk generate slug
    private String generateSlug(String title) {
        return title.toLowerCase()
                   .replaceAll("[^a-z0-9\\s]", "")
                   .replaceAll("\\s+", "-")
                   .trim();
    }
    
    // Method untuk update comment count
    public void updateCommentCount() {
        this.commentCount = (this.comments != null) ? this.comments.size() : 0;
    }
    
    // Getters and Setters
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
        if (title != null) {
            this.slug = generateSlug(title);
        }
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
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
    
    public PostStatus getStatus() {
        return status;
    }
    
    public void setStatus(PostStatus status) {
        this.status = status;
        if (status == PostStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
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
        updateCommentCount();
    }
}
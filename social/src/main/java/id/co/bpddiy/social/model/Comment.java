package id.co.bpddiy.social.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_comment_post", columnList = "post_id"),
    @Index(name = "idx_comment_author", columnList = "author_id"),
    @Index(name = "idx_comment_created_at", columnList = "created_at"),
    @Index(name = "idx_comment_status", columnList = "status")
})
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Konten komentar tidak boleh kosong")
    @Size(min = 1, max = 1000, message = "Komentar harus antara 1-1000 karakter")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status = CommentStatus.PENDING;
    
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    
    @Column(name = "is_edited", nullable = false)
    private Boolean isEdited = false;
    
    @Column(name = "edited_at")
    private LocalDateTime editedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relasi Many-to-One dengan User (author)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false,
               foreignKey = @ForeignKey(name = "fk_comment_author"))
    private User author;
    
    // Relasi Many-to-One dengan Post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false,
               foreignKey = @ForeignKey(name = "fk_comment_post"))
    private Post post;
    
    // Enum untuk Status Comment
    public enum CommentStatus {
        PENDING,    // Menunggu moderasi
        APPROVED,   // Disetujui
        REJECTED,   // Ditolak
        DELETED     // Dihapus
    }
    
    // Constructors
    public Comment() {}
    
    public Comment(String content, User author, Post post) {
        this.content = content;
        this.author = author;
        this.post = post;
    }
    
    // Method untuk menandai comment sudah di-edit
    public void markAsEdited() {
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
    }
    
    // Method untuk approve comment
    public void approve() {
        this.status = CommentStatus.APPROVED;
    }
    
    // Method untuk reject comment
    public void reject() {
        this.status = CommentStatus.REJECTED;
    }
    
    // Method untuk soft delete comment
    public void delete() {
        this.status = CommentStatus.DELETED;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        if (this.createdAt != null) { // Jika bukan comment baru
            markAsEdited();
        }
    }
    
    public CommentStatus getStatus() {
        return status;
    }
    
    public void setStatus(CommentStatus status) {
        this.status = status;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Boolean getIsEdited() {
        return isEdited;
    }
    
    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }
    
    public LocalDateTime getEditedAt() {
        return editedAt;
    }
    
    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
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
    
    public Post getPost() {
        return post;
    }
    
    public void setPost(Post post) {
        this.post = post;
    }
}
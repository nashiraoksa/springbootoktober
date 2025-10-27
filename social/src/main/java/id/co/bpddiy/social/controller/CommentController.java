package id.co.bpddiy.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import id.co.bpddiy.social.dto.CommentRequest;
import id.co.bpddiy.social.model.Comment;
import id.co.bpddiy.social.security.UserPrincipal;
import id.co.bpddiy.social.service.CommentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller untuk Comment Management
 * CRUD operations untuk comments dengan moderation
 */
@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "API untuk management comments (CRUD, moderation)")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * Get comments by post ID (approved only)
     */
    @GetMapping("/post/{postId}")
    @Operation(summary = "Get Comments by Post", description = "Ambil semua comments yang approved untuk post tertentu")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Create new comment (Authenticated users only)
     */
    @PostMapping("/post/{postId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create Comment", description = "Buat comment baru untuk post (perlu authentication)")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId,
                                                @Valid @RequestBody CommentRequest request,
                                                Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Comment comment = commentService.createComment(postId, request, userPrincipal.getId());
        return ResponseEntity.ok(comment);
    }

    /**
     * Update existing comment
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update Comment", description = "Update comment (hanya owner)")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id,
                                                @Valid @RequestBody CommentRequest request,
                                                Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Comment comment = commentService.updateComment(id, request, userPrincipal.getId());
        return ResponseEntity.ok(comment);
    }

    /**
     * Delete comment
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete Comment", description = "Hapus comment (owner atau admin)")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long id,
                                                            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        commentService.deleteComment(id, userPrincipal.getId());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment berhasil dihapus");
        return ResponseEntity.ok(response);
    }

    /**
     * Get comments by author
     */
    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get Comments by Author", description = "Ambil semua comments dari author tertentu")
    public ResponseEntity<Page<Comment>> getCommentsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentService.getCommentsByAuthor(authorId, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Search comments
     */
    @GetMapping("/search")
    @Operation(summary = "Search Comments", description = "Cari comments berdasarkan keyword di content")
    public ResponseEntity<Page<Comment>> searchComments(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentService.searchComments(keyword, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Get recent comments
     */
    @GetMapping("/recent")
    @Operation(summary = "Get Recent Comments", description = "Ambil comments terbaru dalam beberapa hari terakhir")
    public ResponseEntity<List<Comment>> getRecentComments(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Comment> comments = commentService.getRecentComments(days, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Get popular comments
     */
    @GetMapping("/popular")
    @Operation(summary = "Get Popular Comments", description = "Ambil comments populer berdasarkan like count")
    public ResponseEntity<List<Comment>> getPopularComments(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Comment> comments = commentService.getPopularComments(pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Like/Unlike comment
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Like Comment", description = "Like atau unlike comment")
    public ResponseEntity<Map<String, String>> likeComment(@PathVariable Long id,
                                                          @RequestParam boolean isLike) {
        commentService.toggleLike(id, isLike);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", isLike ? "Comment liked" : "Comment unliked");
        return ResponseEntity.ok(response);
    }

    /**
     * Get my comments (current user)
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get My Comments", description = "Ambil comments milik user yang login")
    public ResponseEntity<Page<Comment>> getMyComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentService.getCommentsByAuthor(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(comments);
    }

    // ==================== ADMIN/MODERATOR ENDPOINTS ====================

    /**
     * Get pending comments for moderation (Admin/Moderator only)
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Pending Comments", description = "Ambil comments yang pending moderation (Admin/Moderator)")
    public ResponseEntity<List<Comment>> getPendingComments() {
        List<Comment> comments = commentService.getPendingComments();
        return ResponseEntity.ok(comments);
    }

    /**
     * Get comments by status (Admin/Moderator only)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Comments by Status", description = "Ambil comments berdasarkan status (Admin/Moderator)")
    public ResponseEntity<Page<Comment>> getCommentsByStatus(
            @PathVariable Comment.CommentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentService.getCommentsByStatus(status, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Approve comment (Admin/Moderator only)
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Approve Comment", description = "Approve comment (Admin/Moderator)")
    public ResponseEntity<Comment> approveComment(@PathVariable Long id,
                                                 Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Comment comment = commentService.approveComment(id, userPrincipal.getId());
        return ResponseEntity.ok(comment);
    }

    /**
     * Reject comment (Admin/Moderator only)
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reject Comment", description = "Reject comment (Admin/Moderator)")
    public ResponseEntity<Comment> rejectComment(@PathVariable Long id,
                                                Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Comment comment = commentService.rejectComment(id, userPrincipal.getId());
        return ResponseEntity.ok(comment);
    }

    /**
     * Bulk approve comments (Admin/Moderator only)
     */
    @PostMapping("/bulk-approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Bulk Approve Comments", description = "Approve multiple comments sekaligus")
    public ResponseEntity<Map<String, String>> bulkApproveComments(
            @RequestBody List<Long> commentIds,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        commentService.bulkApproveComments(commentIds, userPrincipal.getId());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", commentIds.size() + " comments berhasil di-approve");
        return ResponseEntity.ok(response);
    }

    /**
     * Bulk reject comments (Admin/Moderator only)
     */
    @PostMapping("/bulk-reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Bulk Reject Comments", description = "Reject multiple comments sekaligus")
    public ResponseEntity<Map<String, String>> bulkRejectComments(
            @RequestBody List<Long> commentIds,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        commentService.bulkRejectComments(commentIds, userPrincipal.getId());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", commentIds.size() + " comments berhasil di-reject");
        return ResponseEntity.ok(response);
    }

    /**
     * Get top commenters (Admin/Moderator only)
     */
    @GetMapping("/top-commenters")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Top Commenters", description = "Ambil users dengan comment terbanyak")
    public ResponseEntity<List<Object[]>> getTopCommenters(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> topCommenters = commentService.getTopCommenters(pageable);
        return ResponseEntity.ok(topCommenters);
    }

    /**
     * Get comment statistics (Admin/Moderator only)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Comment Statistics", description = "Ambil statistik comments per post")
    public ResponseEntity<List<Object[]>> getCommentStatistics() {
        List<Object[]> statistics = commentService.getCommentStatistics();
        return ResponseEntity.ok(statistics);
    }
}
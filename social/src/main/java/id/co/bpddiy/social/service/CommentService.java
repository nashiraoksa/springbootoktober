package id.co.bpddiy.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.co.bpddiy.social.dto.CommentRequest;
import id.co.bpddiy.social.model.Comment;
import id.co.bpddiy.social.model.Post;
import id.co.bpddiy.social.model.User;
import id.co.bpddiy.social.repository.CommentRepository;
import id.co.bpddiy.social.repository.PostRepository;
import id.co.bpddiy.social.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service untuk Comment management dengan moderation features
 */
@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create new comment
     */
    public Comment createComment(Long postId, CommentRequest request, Long authorId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));

        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setStatus(Comment.CommentStatus.PENDING); // Default pending moderation

        Comment savedComment = commentRepository.save(comment);

        // Update post comment count
        postRepository.updateCommentCount(postId);

        return savedComment;
    }

    /**
     * Update existing comment
     */
    public Comment updateComment(Long commentId, CommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment tidak ditemukan"));

        // Check if user is the author
        validateCommentAccess(comment, userId);

        comment.setContent(request.getContent());
        comment.markAsEdited(); // This sets isEdited = true and editedAt = now

        return commentRepository.save(comment);
    }

    /**
     * Get comments by post (approved only for public view)
     */
    public List<Comment> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));

        return commentRepository.findApprovedCommentsWithAuthor(post);
    }

    /**
     * Get all comments by post dengan pagination (for moderation)
     */
    public Page<Comment> getAllCommentsByPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));

        return commentRepository.findByPost(post, pageable);
    }

    /**
     * Get comments by author
     */
    public Page<Comment> getCommentsByAuthor(Long authorId, Pageable pageable) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        return commentRepository.findByAuthor(author, pageable);
    }

    /**
     * Get pending comments for moderation
     */
    public List<Comment> getPendingComments() {
        return commentRepository.findPendingCommentsWithDetails();
    }

    /**
     * Get comments by status dengan pagination
     */
    public Page<Comment> getCommentsByStatus(Comment.CommentStatus status, Pageable pageable) {
        return commentRepository.findByStatus(status, pageable);
    }

    /**
     * Approve comment (Admin/Moderator only)
     */
    public Comment approveComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment tidak ditemukan"));

        validateModerationAccess(userId);

        comment.approve();
        Comment savedComment = commentRepository.save(comment);

        // Update post comment count
        postRepository.updateCommentCount(comment.getPost().getId());

        return savedComment;
    }

    /**
     * Reject comment (Admin/Moderator only)
     */
    public Comment rejectComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment tidak ditemukan"));

        validateModerationAccess(userId);

        comment.reject();
        Comment savedComment = commentRepository.save(comment);

        // Update post comment count
        postRepository.updateCommentCount(comment.getPost().getId());

        return savedComment;
    }

    /**
     * Delete comment
     */
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment tidak ditemukan"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        boolean isOwner = comment.getAuthor().getId().equals(userId);
        boolean isModerator = user.getRole() == User.Role.ADMIN || 
                            user.getRole() == User.Role.MODERATOR;

        if (!isOwner && !isModerator) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk menghapus comment ini");
        }

        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);

        // Update post comment count
        postRepository.updateCommentCount(postId);
    }

    /**
     * Like/Unlike comment
     */
    @Transactional
    public void toggleLike(Long commentId, boolean isLike) {
        int increment = isLike ? 1 : -1;
        commentRepository.updateLikeCount(commentId, increment);
    }

    /**
     * Search comments
     */
    public Page<Comment> searchComments(String keyword, Pageable pageable) {
        return commentRepository.searchApprovedComments(keyword, pageable);
    }

    /**
     * Get recent comments
     */
    public List<Comment> getRecentComments(int days, Pageable pageable) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return commentRepository.findRecentApprovedComments(since, pageable);
    }

    /**
     * Get popular comments
     */
    public List<Comment> getPopularComments(Pageable pageable) {
        return commentRepository.findPopularComments(pageable);
    }

    /**
     * Bulk approve comments (Admin/Moderator only)
     */
    @Transactional
    public void bulkApproveComments(List<Long> commentIds, Long userId) {
        validateModerationAccess(userId);
        commentRepository.bulkApproveComments(commentIds);

        // Update comment counts for affected posts
        List<Comment> comments = commentRepository.findAllById(commentIds);
        comments.stream()
                .map(comment -> comment.getPost().getId())
                .distinct()
                .forEach(postRepository::updateCommentCount);
    }

    /**
     * Bulk reject comments (Admin/Moderator only)
     */
    @Transactional
    public void bulkRejectComments(List<Long> commentIds, Long userId) {
        validateModerationAccess(userId);
        commentRepository.bulkRejectComments(commentIds);

        // Update comment counts for affected posts
        List<Comment> comments = commentRepository.findAllById(commentIds);
        comments.stream()
                .map(comment -> comment.getPost().getId())
                .distinct()
                .forEach(postRepository::updateCommentCount);
    }

    /**
     * Get top commenters
     */
    public List<Object[]> getTopCommenters(Pageable pageable) {
        return commentRepository.findTopCommenters(pageable);
    }

    /**
     * Get comment statistics by post
     */
    public List<Object[]> getCommentStatistics() {
        return commentRepository.getCommentStatisticsByPost();
    }

    /**
     * Validate comment access (owner only for editing)
     */
    private void validateCommentAccess(Comment comment, Long userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk comment ini");
        }
    }

    /**
     * Validate moderation access (Admin/Moderator only)
     */
    private void validateModerationAccess(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.MODERATOR) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk moderasi");
        }
    }

    /**
     * Auto-approve comments dari trusted users (optional feature)
     */
    public void autoApproveFromTrustedUsers() {
        // Logic untuk auto-approve comments dari users yang trusted
        // Misalnya users dengan role ADMIN atau dengan reputation tinggi
    }

    /**
     * Clean up old rejected comments
     */
    @Transactional
    public int cleanupOldRejectedComments(int days) {
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(days);
        return commentRepository.deleteOldRejectedComments(beforeDate);
    }
}
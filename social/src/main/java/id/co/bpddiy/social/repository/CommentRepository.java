package id.co.bpddiy.social.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.co.bpddiy.social.model.Comment;
import id.co.bpddiy.social.model.Post;
import id.co.bpddiy.social.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find comments by post dengan pagination dan sorting
     * Menggunakan index pada post_id untuk optimasi
     */
    Page<Comment> findByPost(Post post, Pageable pageable);

    /**
     * Find approved comments by post - untuk public display
     * Optimasi dengan compound index pada post_id dan status
     */
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.status = 'APPROVED' " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findApprovedCommentsByPost(@Param("post") Post post);

    /**
     * Find comments by author dengan pagination
     */
    Page<Comment> findByAuthor(User author, Pageable pageable);

    /**
     * Find comments by author dan status
     */
    Page<Comment> findByAuthorAndStatus(User author, Comment.CommentStatus status, Pageable pageable);

    /**
     * Find comments by status - untuk moderation
     * Menggunakan index pada status
     */
    Page<Comment> findByStatus(Comment.CommentStatus status, Pageable pageable);

    /**
     * Find pending comments untuk moderation dengan JOIN fetch
     * Eager loading untuk avoid N+1 problem pada moderation page
     */
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.author " +
           "JOIN FETCH c.post " +
           "WHERE c.status = 'PENDING' " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findPendingCommentsWithDetails();

    /**
     * Count comments by status
     */
    @Query("SELECT c.status, COUNT(c) FROM Comment c GROUP BY c.status")
    List<Object[]> countCommentsByStatus();

    /**
     * Count approved comments by post
     */
    Long countByPostAndStatus(Post post, Comment.CommentStatus status);

    /**
     * Count comments by author
     */
    Long countByAuthor(User author);

    /**
     * Find recent comments dengan time limit
     * Untuk dashboard atau recent activity
     */
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.author " +
           "JOIN FETCH c.post " +
           "WHERE c.status = 'APPROVED' AND c.createdAt >= :since " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findRecentApprovedComments(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find comments yang perlu moderation (pending > X days)
     */
    @Query("SELECT c FROM Comment c WHERE c.status = 'PENDING' " +
           "AND c.createdAt < :beforeDate " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findPendingCommentsOlderThan(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Search comments by content - case insensitive
     */
    @Query("SELECT c FROM Comment c WHERE c.status = 'APPROVED' AND " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Comment> searchApprovedComments(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find top commenters - users dengan most approved comments
     */
    @Query("SELECT c.author, COUNT(c) as commentCount FROM Comment c " +
           "WHERE c.status = 'APPROVED' " +
           "GROUP BY c.author " +
           "ORDER BY COUNT(c) DESC")
    List<Object[]> findTopCommenters(Pageable pageable);

    /**
     * Find comments by post dengan author details (avoid N+1)
     */
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.author " +
           "WHERE c.post = :post AND c.status = 'APPROVED' " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findApprovedCommentsWithAuthor(@Param("post") Post post);

    /**
     * Update like count for comment
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + :increment WHERE c.id = :commentId")
    void updateLikeCount(@Param("commentId") Long commentId, @Param("increment") Integer increment);

    /**
     * Bulk approve comments by IDs
     */
    @Modifying
    @Query("UPDATE Comment c SET c.status = 'APPROVED' WHERE c.id IN :commentIds")
    void bulkApproveComments(@Param("commentIds") List<Long> commentIds);

    /**
     * Bulk reject comments by IDs
     */
    @Modifying
    @Query("UPDATE Comment c SET c.status = 'REJECTED' WHERE c.id IN :commentIds")
    void bulkRejectComments(@Param("commentIds") List<Long> commentIds);

    /**
     * Delete old rejected comments - cleanup
     */
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.status = 'REJECTED' " +
           "AND c.updatedAt < :beforeDate")
    int deleteOldRejectedComments(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Find comments yang di-edit dalam periode tertentu
     */
    @Query("SELECT c FROM Comment c WHERE c.isEdited = true " +
           "AND c.editedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY c.editedAt DESC")
    List<Comment> findEditedCommentsBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Get comment statistics by post
     */
    @Query("SELECT p.id, p.title, " +
           "COUNT(CASE WHEN c.status = 'APPROVED' THEN 1 END) as approvedCount, " +
           "COUNT(CASE WHEN c.status = 'PENDING' THEN 1 END) as pendingCount, " +
           "COUNT(CASE WHEN c.status = 'REJECTED' THEN 1 END) as rejectedCount " +
           "FROM Post p LEFT JOIN p.comments c " +
           "GROUP BY p.id, p.title " +
           "ORDER BY approvedCount DESC")
    List<Object[]> getCommentStatisticsByPost();

    /**
     * Find comments dengan most likes - popular comments
     */
    @Query("SELECT c FROM Comment c WHERE c.status = 'APPROVED' " +
           "AND c.likeCount > 0 " +
           "ORDER BY c.likeCount DESC, c.createdAt DESC")
    List<Comment> findPopularComments(Pageable pageable);

    /**
     * Auto-delete spam comments (rejected > X days)
     */
    @Modifying
    @Query("UPDATE Comment c SET c.status = 'DELETED' " +
           "WHERE c.status = 'REJECTED' AND c.updatedAt < :beforeDate")
    int autoDeleteSpamComments(@Param("beforeDate") LocalDateTime beforeDate);
}
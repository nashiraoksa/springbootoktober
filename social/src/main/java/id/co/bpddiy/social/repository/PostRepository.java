package id.co.bpddiy.social.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.co.bpddiy.social.model.Post;
import id.co.bpddiy.social.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Find post by slug - untuk SEO friendly URLs
     * Slug di-index untuk performa optimal
     */
    Optional<Post> findBySlug(String slug);

    /**
     * Find published posts dengan pagination dan sorting
     * Menggunakan index pada status dan created_at
     */
    Page<Post> findByStatus(Post.PostStatus status, Pageable pageable);

    /**
     * Find posts by author dengan pagination
     * Menggunakan index pada author_id
     */
    Page<Post> findByAuthor(User author, Pageable pageable);

    /**
     * Find posts by author dan status
     */
    Page<Post> findByAuthorAndStatus(User author, Post.PostStatus status, Pageable pageable);

    /**
     * Find featured posts yang published
     * Optimasi dengan compound index pada isFeatured dan status
     */
    @Query("SELECT p FROM Post p WHERE p.isFeatured = true AND p.status = :status " +
           "ORDER BY p.publishedAt DESC")
    List<Post> findFeaturedPosts(@Param("status") Post.PostStatus status);

    /**
     * Search posts by title atau content (full text search)
     * Case insensitive search dengan LIKE optimization
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find posts dengan JOIN fetch untuk avoid N+1 problem
     * Menggunakan JOIN FETCH untuk eager loading author dan comments
     */
    @Query("SELECT DISTINCT p FROM Post p " +
           "LEFT JOIN FETCH p.author " +
           "LEFT JOIN FETCH p.comments c " +
           "WHERE p.status = 'PUBLISHED' " +
           "ORDER BY p.publishedAt DESC")
    List<Post> findPublishedPostsWithAuthorAndComments();

    /**
     * Find popular posts by view count
     * Menggunakan index pada view_count
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
           "ORDER BY p.viewCount DESC, p.publishedAt DESC")
    List<Post> findPopularPosts(Pageable pageable);

    /**
     * Find trending posts (berdasarkan like dan comment dalam periode tertentu)
     * Complex query untuk trending algorithm
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
           "AND p.publishedAt >= :since " +
           "ORDER BY (p.likeCount + p.commentCount) DESC, p.publishedAt DESC")
    List<Post> findTrendingPosts(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find posts published in date range
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
           "AND p.publishedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY p.publishedAt DESC")
    Page<Post> findPostsPublishedBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       Pageable pageable);

    /**
     * Count posts by status
     */
    @Query("SELECT p.status, COUNT(p) FROM Post p GROUP BY p.status")
    List<Object[]> countPostsByStatus();

    /**
     * Count posts by author
     */
    Long countByAuthor(User author);

    /**
     * Count posts by author dan status
     */
    Long countByAuthorAndStatus(User author, Post.PostStatus status);

    /**
     * Update view count - optimized single query
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    /**
     * Update like count
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + :increment WHERE p.id = :postId")
    void updateLikeCount(@Param("postId") Long postId, @Param("increment") Integer increment);

    /**
     * Update comment count berdasarkan actual count dari comments
     */
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = " +
           "(SELECT COUNT(c) FROM Comment c WHERE c.post = p AND c.status = 'APPROVED') " +
           "WHERE p.id = :postId")
    void updateCommentCount(@Param("postId") Long postId);

    /**
     * Find posts yang belum ada comment
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
           "NOT EXISTS (SELECT c FROM Comment c WHERE c.post = p)")
    List<Post> findPostsWithoutComments();

    /**
     * Get post statistics dengan JOIN aggregation
     */
    @Query("SELECT p.id, p.title, COUNT(DISTINCT c.id), p.viewCount, p.likeCount " +
           "FROM Post p LEFT JOIN p.comments c " +
           "WHERE p.id = :postId " +
           "GROUP BY p.id, p.title, p.viewCount, p.likeCount")
    List<Object[]> getPostStatistics(@Param("postId") Long postId);

    /**
     * Find related posts berdasarkan author (exclude current post)
     */
    @Query("SELECT p FROM Post p WHERE p.author = :author " +
           "AND p.status = 'PUBLISHED' AND p.id != :excludePostId " +
           "ORDER BY p.publishedAt DESC")
    List<Post> findRelatedPostsByAuthor(@Param("author") User author, 
                                      @Param("excludePostId") Long excludePostId, 
                                      Pageable pageable);

    /**
     * Archive old posts - update status ke ARCHIVED
     */
    @Modifying
    @Query("UPDATE Post p SET p.status = 'ARCHIVED' " +
           "WHERE p.publishedAt < :beforeDate AND p.status = 'PUBLISHED'")
    int archiveOldPosts(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Find draft posts older than specified days
     */
    @Query("SELECT p FROM Post p WHERE p.status = 'DRAFT' " +
           "AND p.createdAt < :beforeDate")
    List<Post> findOldDraftPosts(@Param("beforeDate") LocalDateTime beforeDate);
}
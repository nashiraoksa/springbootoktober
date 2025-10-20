package id.co.bpddiy.social.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.co.bpddiy.social.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository untuk User dengan optimasi query dan custom methods
 * Menggunakan JPA Repository dengan custom query untuk performance
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username atau email (untuk login)
     * Menggunakan index pada username dan email untuk optimasi
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Find user by username only
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email only
     */
    Optional<User> findByEmail(String email);

    /**
     * Check apakah username sudah exists
     */
    boolean existsByUsername(String username);

    /**
     * Check apakah email sudah exists
     */
    boolean existsByEmail(String email);

    /**
     * Find active users only
     */
    List<User> findByIsActive(Boolean isActive);

    /**
     * Find users by role dengan pagination
     */
    Page<User> findByRole(User.Role role, Pageable pageable);

    /**
     * Find active users by role
     */
    List<User> findByRoleAndIsActive(User.Role role, Boolean isActive);

    /**
     * Search users by username atau full name (case insensitive)
     * Menggunakan LIKE dengan LOWER untuk search case insensitive
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByUsernameOrFullName(@Param("keyword") String keyword);

    /**
     * Find users dengan post count menggunakan JOIN
     * Optimasi dengan single query untuk menghitung posts
     */
    @Query("SELECT u, COUNT(p) FROM User u LEFT JOIN u.posts p " +
           "WHERE u.isActive = true " +
           "GROUP BY u " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> findActiveUsersWithPostCount();

    /**
     * Find top users by post count dengan limit
     */
    @Query("SELECT u FROM User u LEFT JOIN u.posts p " +
           "WHERE u.isActive = true " +
           "GROUP BY u " +
           "ORDER BY COUNT(p) DESC")
    List<User> findTopUsersByPostCount(Pageable pageable);

    /**
     * Find users dengan comment count
     */
    @Query("SELECT u, COUNT(c) FROM User u LEFT JOIN u.comments c " +
           "WHERE u.isActive = true " +
           "GROUP BY u " +
           "ORDER BY COUNT(c) DESC")
    List<Object[]> findActiveUsersWithCommentCount();

    /**
     * Get user statistics - total posts dan comments
     */
    @Query("SELECT u.id, u.username, u.email, COUNT(DISTINCT p), COUNT(DISTINCT c) " +
           "FROM User u " +
           "LEFT JOIN u.posts p " +
           "LEFT JOIN u.comments c " +
           "WHERE u.id = :userId " +
           "GROUP BY u.id, u.username, u.email")
    List<Object[]> getUserStatistics(@Param("userId") Long userId);

    /**
     * Find users registered dalam periode tertentu
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY u.createdAt DESC")
    List<User> findUsersRegisteredBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                        @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Count active users by role
     */
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.isActive = true GROUP BY u.role")
    List<Object[]> countActiveUsersByRole();

    /**
     * Find users yang belum pernah posting
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "NOT EXISTS (SELECT p FROM Post p WHERE p.author = u)")
    List<User> findUsersWithoutPosts();

    /**
     * Update user last activity (bisa digunakan untuk tracking)
     */
    @Query("UPDATE User u SET u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastActivity(@Param("userId") Long userId);
}
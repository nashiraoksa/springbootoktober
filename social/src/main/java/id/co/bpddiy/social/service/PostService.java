package id.co.bpddiy.social.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import id.co.bpddiy.social.dto.PostRequest;
import id.co.bpddiy.social.model.Post;
import id.co.bpddiy.social.model.User;
import id.co.bpddiy.social.repository.PostRepository;
import id.co.bpddiy.social.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create new post
     */
    public Post createPost(PostRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setFeaturedImageUrl(request.getFeaturedImageUrl());
        post.setStatus(request.getStatus());
        post.setIsFeatured(request.getIsFeatured());
        post.setAuthor(author);

        // Auto-generate excerpt if not provided
        if (post.getExcerpt() == null || post.getExcerpt().isEmpty()) {
            post.setExcerpt(generateExcerpt(post.getContent()));
        }

        // Set published date if status is PUBLISHED
        if (post.getStatus() == Post.PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        return postRepository.save(post);
    }

    /**
     * Update existing post
     */
    public Post updatePost(Long postId, PostRequest request, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));

        // Check if user is the author or has admin/moderator role
        validatePostAccess(post, userId);

        // Update fields
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setFeaturedImageUrl(request.getFeaturedImageUrl());
        
        // Handle status change
        Post.PostStatus oldStatus = post.getStatus();
        post.setStatus(request.getStatus());
        
        // Set published date if status changed to PUBLISHED
        if (oldStatus != Post.PostStatus.PUBLISHED && 
            request.getStatus() == Post.PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        post.setIsFeatured(request.getIsFeatured());

        // Auto-generate excerpt if not provided
        if (post.getExcerpt() == null || post.getExcerpt().isEmpty()) {
            post.setExcerpt(generateExcerpt(post.getContent()));
        }

        return postRepository.save(post);
    }

    /**
     * Get post by ID (increment view count)
     */
    @Transactional
    public Post getPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));
        
        // Increment view count
        postRepository.incrementViewCount(id);
        post.setViewCount(post.getViewCount() + 1);
        
        return post;
    }

    /**
     * Get post by slug (for SEO friendly URLs)
     */
    @Transactional
    public Post getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));
        
        // Increment view count
        postRepository.incrementViewCount(post.getId());
        post.setViewCount(post.getViewCount() + 1);
        
        return post;
    }

    /**
     * Get published posts dengan pagination
     */
    public Page<Post> getPublishedPosts(Pageable pageable) {
        return postRepository.findByStatus(Post.PostStatus.PUBLISHED, pageable);
    }

    /**
     * Get posts by author
     */
    public Page<Post> getPostsByAuthor(Long authorId, Pageable pageable) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        return postRepository.findByAuthor(author, pageable);
    }

    /**
     * Get published posts by author
     */
    public Page<Post> getPublishedPostsByAuthor(Long authorId, Pageable pageable) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        return postRepository.findByAuthorAndStatus(author, Post.PostStatus.PUBLISHED, pageable);
    }

    /**
     * Search posts
     */
    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchPosts(keyword, pageable);
    }

    /**
     * Get featured posts
     */
    public List<Post> getFeaturedPosts() {
        return postRepository.findFeaturedPosts(Post.PostStatus.PUBLISHED);
    }

    /**
     * Get popular posts
     */
    public List<Post> getPopularPosts(Pageable pageable) {
        return postRepository.findPopularPosts(pageable);
    }

    /**
     * Get trending posts (last 7 days)
     */
    public List<Post> getTrendingPosts(Pageable pageable) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        return postRepository.findTrendingPosts(since, pageable);
    }

    /**
     * Delete post
     */
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));

        validatePostAccess(post, userId);
        postRepository.delete(post);
    }

    /**
     * Like/Unlike post
     */
    @Transactional
    public void toggleLike(Long postId, boolean isLike) {
        int increment = isLike ? 1 : -1;
        postRepository.updateLikeCount(postId, increment);
    }

    /**
     * Get related posts by same author
     */
    public List<Post> getRelatedPosts(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));
        
        return postRepository.findRelatedPostsByAuthor(post.getAuthor(), postId, pageable);
    }

    /**
     * Validate user access to post (owner, admin, or moderator)
     */
    private void validatePostAccess(Post post, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        boolean isOwner = post.getAuthor().getId().equals(userId);
        boolean isAdminOrModerator = user.getRole() == User.Role.ADMIN || 
                                   user.getRole() == User.Role.MODERATOR;

        if (!isOwner && !isAdminOrModerator) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk post ini");
        }
    }

    /**
     * Generate excerpt from content
     */
    private String generateExcerpt(String content) {
        if (content == null) return "";
        
        // Remove HTML tags if any
        String cleanContent = content.replaceAll("<[^>]*>", "");
        
        // Take first 200 characters
        if (cleanContent.length() <= 200) {
            return cleanContent;
        }
        
        String excerpt = cleanContent.substring(0, 200);
        int lastSpace = excerpt.lastIndexOf(' ');
        
        if (lastSpace > 150) {
            excerpt = excerpt.substring(0, lastSpace);
        }
        
        return excerpt + "...";
    }

    /**
     * Get draft posts by author
     */
    public Page<Post> getDraftPostsByAuthor(Long authorId, Pageable pageable) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        return postRepository.findByAuthorAndStatus(author, Post.PostStatus.DRAFT, pageable);
    }

    /**
     * Publish draft post
     */
    public Post publishPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post tidak ditemukan"));

        validatePostAccess(post, userId);

        post.setStatus(Post.PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());

        return postRepository.save(post);
    }
}
package id.co.bpddiy.social.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.co.bpddiy.social.dto.PostRequest;
import id.co.bpddiy.social.model.Post;
import id.co.bpddiy.social.security.UserPrincipal;
import id.co.bpddiy.social.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "API untuk management posts (CRUD, search, trending)")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * Get all published posts dengan pagination
     */
    @GetMapping
    @Operation(summary = "Get Published Posts", description = "Ambil semua posts yang published dengan pagination")
    public ResponseEntity<Page<Post>> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Post> posts = postService.getPublishedPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get post by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Post by ID", description = "Ambil post berdasarkan ID (increment view count)")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    /**
     * Get post by slug (SEO friendly)
     */
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get Post by Slug", description = "Ambil post berdasarkan slug untuk SEO friendly URLs")
    public ResponseEntity<Post> getPostBySlug(@PathVariable String slug) {
        Post post = postService.getPostBySlug(slug);
        return ResponseEntity.ok(post);
    }

    /**
     * Create new post (Authenticated users only)
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create Post", description = "Buat post baru (perlu authentication)")
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest request, 
                                          Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.createPost(request, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }

    /**
     * Update existing post
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update Post", description = "Update post yang sudah ada (hanya owner atau admin)")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,
                                          @Valid @RequestBody PostRequest request,
                                          Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.updatePost(id, request, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }

    /**
     * Delete post
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete Post", description = "Hapus post (hanya owner atau admin)")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id,
                                                         Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        postService.deletePost(id, userPrincipal.getId());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post berhasil dihapus");
        return ResponseEntity.ok(response);
    }

    /**
     * Search posts
     */
    @GetMapping("/search")
    @Operation(summary = "Search Posts", description = "Cari posts berdasarkan keyword di title atau content")
    public ResponseEntity<Page<Post>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Post> posts = postService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get featured posts
     */
    @GetMapping("/featured")
    @Operation(summary = "Get Featured Posts", description = "Ambil posts yang difeatured")
    public ResponseEntity<List<Post>> getFeaturedPosts() {
        List<Post> posts = postService.getFeaturedPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * Get popular posts
     */
    @GetMapping("/popular")
    @Operation(summary = "Get Popular Posts", description = "Ambil posts populer berdasarkan view count")
    public ResponseEntity<List<Post>> getPopularPosts(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postService.getPopularPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get trending posts
     */
    @GetMapping("/trending")
    @Operation(summary = "Get Trending Posts", description = "Ambil posts trending (like + comment count dalam 7 hari terakhir)")
    public ResponseEntity<List<Post>> getTrendingPosts(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postService.getTrendingPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get posts by author
     */
    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get Posts by Author", description = "Ambil semua posts dari author tertentu")
    public ResponseEntity<Page<Post>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postService.getPostsByAuthor(authorId, pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get published posts by author
     */
    @GetMapping("/author/{authorId}/published")
    @Operation(summary = "Get Published Posts by Author", description = "Ambil posts published dari author tertentu")
    public ResponseEntity<Page<Post>> getPublishedPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Post> posts = postService.getPublishedPostsByAuthor(authorId, pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get related posts
     */
    @GetMapping("/{id}/related")
    @Operation(summary = "Get Related Posts", description = "Ambil posts terkait dari author yang sama")
    public ResponseEntity<List<Post>> getRelatedPosts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postService.getRelatedPosts(id, pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Like/Unlike post
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Like Post", description = "Like atau unlike post")
    public ResponseEntity<Map<String, String>> likePost(@PathVariable Long id,
                                                       @RequestParam boolean isLike) {
        postService.toggleLike(id, isLike);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", isLike ? "Post liked" : "Post unliked");
        return ResponseEntity.ok(response);
    }

    /**
     * Publish draft post
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Publish Post", description = "Publish draft post")
    public ResponseEntity<Post> publishPost(@PathVariable Long id,
                                           Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.publishPost(id, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }

    /**
     * Get draft posts by current user
     */
    @GetMapping("/drafts")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Draft Posts", description = "Ambil draft posts milik user yang login")
    public ResponseEntity<Page<Post>> getDraftPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Post> posts = postService.getDraftPostsByAuthor(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(posts);
    }
}
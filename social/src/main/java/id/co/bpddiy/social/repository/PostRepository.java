package id.co.bpddiy.social.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.co.bpddiy.social.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // default >> findById(Long id)
    Optional<Post> findBySlug(String slug);
}

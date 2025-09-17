package com.example.domain.post.repository;

import com.example.domain.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByGroupId(Long groupId, Pageable pageable);
    Page<Post> findByHostId(Long hostId, Pageable pageable);
    boolean existsByGroupId(Long groupId);
}

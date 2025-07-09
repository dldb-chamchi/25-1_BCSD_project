package com.example.repository;

import com.example.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostId(Long postId);
    List<PostComment> findByMemberId(Long memberId);
    long countByPostId(Long postId);
}

package com.example.repository;

import com.example.model.GroupPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    Page<GroupPost> findByGroupId(Long groupId, Pageable pageable);
    Page<GroupPost> findByHostId(Long hostId, Pageable pageable);
    boolean existsByGroupId(Long groupId);
}

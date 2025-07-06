package com.example.repository;

import com.example.model.GroupPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    List<GroupPost> findByGroupId(Long groupId);
    List<GroupPost> findByHostId(Long HostId);
}

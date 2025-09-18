package com.example.domain.group.repository;

import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByStatusAndExpiresAtBefore(GroupStatus status, LocalDateTime time);
    Page<Group> findByStatus(GroupStatus status, Pageable pageable);

    //status 가 OPEN, 참가자 수 < maxMembers 인 그룹
    @Query("SELECT g FROM Group g " +
            "WHERE g.status = :status " +
            "  AND SIZE(g.participants) < g.maxMember")
    Page<Group> findAvailableByStatusAndMaxMembers(@Param("status") GroupStatus status, Pageable pageable);
    Page<Group> findByHostId(Long memberId, Pageable pageable);
}

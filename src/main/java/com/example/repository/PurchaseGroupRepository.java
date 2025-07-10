package com.example.repository;

import com.example.model.PurchaseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseGroupRepository extends JpaRepository<PurchaseGroup, Long> {
    List<PurchaseGroup> findByStatusAndExpiresAtBefore(String status, LocalDateTime time);
    List<PurchaseGroup> findByStatus(String status);

    //status 가 OPEN, 참가자 수 < maxMembers 인 그룹
    @Query("SELECT g FROM PurchaseGroup g " +
            "WHERE g.status = :status " +
            "  AND SIZE(g.participants) < g.maxMembers")
    List<PurchaseGroup> findAvailableByStatusAndMaxMembers(@Param("status") String status);
    List<PurchaseGroup> findByHostId(Long memberId);
}

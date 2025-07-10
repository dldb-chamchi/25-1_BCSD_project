package com.example.repository;

import com.example.model.PurchaseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseGroupRepository extends JpaRepository<PurchaseGroup, Long> {
    List<PurchaseGroup> findByStatusAndExpiresAtBefore(String status, LocalDateTime time);
}

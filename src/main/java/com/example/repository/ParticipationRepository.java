package com.example.repository;

import com.example.model.Participation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByGroupId(Long groupId);
    List<Participation> findByMemberId(Long memberId);
    boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);
}

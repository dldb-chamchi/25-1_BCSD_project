package com.example.domain.member.repository;

import com.example.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndDeletedFalse(String email);
    Optional<Member> findByIdAndDeletedFalse(Long id);
}

package com.example.service;

import com.example.dto.request.MemberRequestDto;
import com.example.model.Member;
import com.example.model.Participation;
import com.example.model.PurchaseGroup;
import com.example.repository.MemberRepository;
import com.example.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepo;
    private final ParticipationRepository partRepo;

    public Member register(MemberRequestDto dto) {
        memberRepo.findByEmail(dto.getEmail())
                .ifPresent(m -> { throw new RuntimeException("이미 존재하는 이메일입니다: " + dto.getEmail()); });

        Member m = Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())  //인증 도입 시 암호화 추가
                .name(dto.getName())
                .build();

        return memberRepo.save(m);
    }

    @Transactional(readOnly = true)
    public Member get(Long id) {
        return memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 없습니다: " + id));
    }

    public void delete(Long id) {
        if (!memberRepo.existsById(id)) {
            throw new RuntimeException("회원이 없습니다: " + id);
        }
        memberRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PurchaseGroup> getJoinedGroups(Long memberId) {
        List<Participation> parts = partRepo.findByMemberId(memberId);
        return parts.stream()
                .map(Participation::getGroup)
                .collect(Collectors.toList());
    }
}

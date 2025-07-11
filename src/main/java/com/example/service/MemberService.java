package com.example.service;

import com.example.dto.request.MemberRequestDto;
import com.example.exception.DuplicateEmailException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.Member;
import com.example.model.Participation;
import com.example.model.PurchaseGroup;
import com.example.repository.MemberRepository;
import com.example.repository.ParticipationRepository;
import com.example.repository.PurchaseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PurchaseGroupRepository groupRepo;
    private final PasswordEncoder passwordEncoder;

    public Member register(MemberRequestDto dto) {
        memberRepo.findByEmail(dto.email())
                .ifPresent(m -> { throw new DuplicateEmailException("이미 존재하는 이메일입니다: " + dto.email()); });
        Member m = dto.toEntity(passwordEncoder);
        return memberRepo.save(m);
    }

    @Transactional(readOnly = true)
    public Member get(Long id) {
        return memberRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원이 없습니다: " + id));
    }

    @Transactional(readOnly=true)
    public Member getByEmail(String email) {
        return memberRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("회원이 없습니다: " + email));
    }

    @Transactional(readOnly = true)
    public List<PurchaseGroup> getJoinedGroups(Long memberId) {
        if(!memberRepo.existsById(memberId)) {
            throw new ResourceNotFoundException("회원이 없습니다: " + memberId);
        }
        List<Participation> parts = partRepo.findByMemberId(memberId);
        return parts.stream()
                .map(Participation::getGroup)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PurchaseGroup> getHostGroups(Long memberId) {
        if (!memberRepo.existsById(memberId)) {
            throw new ResourceNotFoundException("회원이 없습니다: " + memberId);
        }
        return groupRepo.findByHostId(memberId);
    }

    public void delete(Long id) {
        if (!memberRepo.existsById(id)) {
            throw new ResourceNotFoundException("회원이 없습니다: " + id);
        }
        memberRepo.deleteById(id);
    }
}

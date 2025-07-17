package com.example.service;

import com.example.dto.request.MemberRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.dto.response.GroupResponseDto;
import com.example.dto.response.MemberResponseDto;
import com.example.dto.response.PostResponseDto;
import com.example.exception.ExceptionList;
import com.example.exception.errorCode.MemberErrorCode;
import com.example.model.Member;
import com.example.model.Participation;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepo;
    private final ParticipationRepository partRepo;
    private final PurchaseGroupRepository groupRepo;
    private final GroupPostRepository postRepo;
    private final PostCommentRepository commentRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDto register(MemberRequestDto dto) {
        memberRepo.findByEmail(dto.email())
                .ifPresent(m -> { throw new ExceptionList(MemberErrorCode.DUPLICATE_EMAIL); });
        Member member = dto.toEntity(passwordEncoder);
        return MemberResponseDto.fromEntity(memberRepo.save(member));
    }

    public MemberResponseDto get(Long memberId) {
        Member member = memberRepo.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new ExceptionList(MemberErrorCode.NOT_FOUND_USER));
        return MemberResponseDto.fromEntity(member);
    }

    public Member getByEmail(String email) {
        return memberRepo.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ExceptionList(MemberErrorCode.NOT_FOUND_USER));
    }

    public Page<GroupResponseDto> getJoinedGroups(Long memberId, Pageable pageable) {
        if(!memberRepo.existsById(memberId)) {
            throw new ExceptionList(MemberErrorCode.NOT_FOUND_USER);
        }
        return partRepo.findByMemberId(memberId, pageable)
                .map(participation ->
                        GroupResponseDto.fromEntity(participation.getGroup())
                );
    }

    public Page<GroupResponseDto> getHostGroups(Long memberId, Pageable pageable) {
        if (!memberRepo.existsById(memberId)) {
            throw new ExceptionList(MemberErrorCode.NOT_FOUND_USER);
        }
        return groupRepo.findByHostId(memberId, pageable)
                .map(GroupResponseDto::fromEntity);
    }

    public Page<PostResponseDto> getMemberPosts(Long memberId, Pageable pageable) {
        if (!memberRepo.existsById(memberId)) {
            throw new ExceptionList(MemberErrorCode.NOT_FOUND_USER);
        }
        return postRepo.findByHostId(memberId, pageable)
                .map(PostResponseDto::fromEntity);
    }

    public Page<CommentResponseDto> getMemberComments(Long memberId, Pageable pageable) {
        if (!memberRepo.existsById(memberId)) {
            throw new ExceptionList(MemberErrorCode.NOT_FOUND_USER);
        }
        return commentRepo.findByMemberId(memberId, pageable)
                .map(CommentResponseDto::fromEntity);
    }

    @Transactional
    public void delete(Long id) {
        Member m = memberRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ExceptionList(MemberErrorCode.NOT_FOUND_USER));
        List<Participation> parts = partRepo.findByMemberId(id);
        if (!parts.isEmpty()) {
            partRepo.deleteAll(parts);
        }
        m.deleteMember();
    }
}

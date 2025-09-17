package com.example.domain.member.service;

import com.example.domain.comment.repository.CommentRepository;
import com.example.domain.group.repository.GroupRepository;
import com.example.domain.member.dto.MemberRequestDto;
import com.example.domain.comment.dto.CommentResponseDto;
import com.example.domain.group.dto.GroupResponseDto;
import com.example.domain.member.dto.MemberResponseDto;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.participation.repository.ParticipationRepository;
import com.example.domain.post.dto.PostResponseDto;
import com.example.domain.post.repository.PostRepository;
import com.example.global.exception.ExceptionList;
import com.example.global.exception.errorCode.MemberErrorCode;
import com.example.domain.member.model.Member;
import com.example.domain.participation.model.Participation;

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
    private final GroupRepository groupRepo;
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
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

package com.example.service;

import com.example.dto.request.CommentRequestDto;
import com.example.model.GroupPost;
import com.example.model.PostComment;
import com.example.repository.GroupPostRepository;
import com.example.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommentService {
    private final PostCommentRepository commentRepo;
    private final GroupPostRepository postRepo;
    private final ParticipationService participationService;

    public PostComment create(Long postId, CommentRequestDto dto) {
        GroupPost p = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));

        boolean isMember = participationService.listByGroup(p.getGroup().getId())
                .stream()
                .anyMatch(x -> x.getMemberId().equals(dto.getMemberId()));

        if (!isMember)
            throw new RuntimeException("그룹 참여자만 댓글을 달 수 있습니다");

        PostComment c = PostComment.builder()
                .post(p)
                .memberId(dto.getMemberId())
                .content(dto.getContent())
                .build();
        return commentRepo.save(c);
    }

    @Transactional(readOnly=true)
    public List<PostComment> list(Long postId) {
        return commentRepo.findByPostId(postId);
    }

    @Transactional(readOnly=true)
    public List<PostComment> listByMember(Long memberId) {
        return commentRepo.findByMemberId(memberId);
    }
}

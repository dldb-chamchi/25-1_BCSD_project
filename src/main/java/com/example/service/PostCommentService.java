package com.example.service;

import com.example.dto.request.CommentRequestDto;
import com.example.exception.*;
import com.example.model.PostComment;
import com.example.repository.GroupPostRepository;
import com.example.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommentService {
    private final PostCommentRepository commentRepo;
    private final GroupPostRepository postRepo;
    private final ParticipationService participationService;

    public PostComment create(Long postId, Long memberId, CommentRequestDto dto) {
        var p = postRepo.findById(postId)
                .orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        boolean isMember = participationService.listByGroup(p.getGroup().getId())
                .stream().anyMatch(x -> x.getMemberId().equals(memberId));

        if (!isMember) throw new ExceptionList(CommentErrorCode.ONLY_GROUP_MEMBER);

        PostComment c = dto.toEntity(p, memberId);
        return commentRepo.save(c);
    }

    @Transactional(readOnly=true)
    public Page<PostComment> list(Long postId, Pageable pageable) {
        return commentRepo.findByPostId(postId, pageable);
    }

    @Transactional(readOnly=true)
    public Page<PostComment> listByMember(Long memberId, Pageable pageable) {
        return commentRepo.findByMemberId(memberId, pageable);
    }

    public PostComment update(
            Long groupId,
            Long postId,
            Long commentId,
            Long memberId,
            CommentRequestDto dto
    ) {
        PostComment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ExceptionList(CommentErrorCode.NOT_FOUND_COMMENT));

        if (!comment.getPost().getId().equals(postId)
                || !comment.getPost().getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 경로(groupId/postId) 입니다");
        }
        if (!comment.getMemberId().equals(memberId)) {
            throw new ExceptionList(CommentErrorCode.ONLY_WRITER_MEMBER_UPDATE);
        }

        comment.update(dto.content());
        return comment;
    }
    public void delete(Long groupId, Long postId, Long commentId, Long memberId) {
        PostComment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new ExceptionList(CommentErrorCode.NOT_FOUND_COMMENT));

        if (!c.getPost().getId().equals(postId)
                || !c.getPost().getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 경로입니다");
        }
        if (!c.getMemberId().equals(memberId)) {
            throw new ExceptionList(CommentErrorCode.ONLY_WRITER_MEMBER_DELETE);
        }
        commentRepo.delete(c);
    }
}

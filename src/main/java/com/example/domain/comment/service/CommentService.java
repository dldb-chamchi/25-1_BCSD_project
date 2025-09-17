package com.example.domain.comment.service;

import com.example.domain.comment.dto.CommentRequestDto;
import com.example.domain.comment.dto.CommentResponseDto;
import com.example.global.exception.ExceptionList;
import com.example.global.exception.errorCode.CommentErrorCode;
import com.example.global.exception.errorCode.PathErrorCode;
import com.example.global.exception.errorCode.PostErrorCode;
import com.example.domain.post.model.Post;
import com.example.domain.comment.model.Comment;
import com.example.domain.post.repository.PostRepository;
import com.example.domain.participation.repository.ParticipationRepository;
import com.example.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepo;
    private final PostRepository postRepo;
    private final ParticipationRepository partRepo;

    @Transactional
    public CommentResponseDto create(Long postId, Long memberId, CommentRequestDto dto) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        boolean isMember = partRepo.existsByGroupIdAndMemberId(
                post.getGroup().getId(), memberId);

        if (!isMember) throw new ExceptionList(CommentErrorCode.ONLY_GROUP_MEMBER);

        Comment comment = dto.toEntity(post, memberId);
        return CommentResponseDto.fromEntity(commentRepo.save(comment));
    }

    public Page<CommentResponseDto> list(Long postId, Pageable pageable) {
        return commentRepo.findByPostId(postId, pageable)
                .map(CommentResponseDto::fromEntity);
    }

    @Transactional
    public CommentResponseDto update(
            Long groupId,
            Long postId,
            Long commentId,
            Long memberId,
            CommentRequestDto dto
    ) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ExceptionList(CommentErrorCode.NOT_FOUND_COMMENT));

        if (!comment.getPost().getId().equals(postId)
                || !comment.getPost().getGroup().getId().equals(groupId)) {
            throw new ExceptionList(PathErrorCode.NOT_VALID_PATH);
        }
        if (!comment.getMemberId().equals(memberId)) {
            throw new ExceptionList(CommentErrorCode.ONLY_WRITER_MEMBER_UPDATE);
        }

        comment.update(dto.content());
        return CommentResponseDto.fromEntity(comment);
    }

    @Transactional
    public void delete(Long groupId, Long postId, Long commentId, Long memberId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ExceptionList(CommentErrorCode.NOT_FOUND_COMMENT));

        if (!comment.getPost().getId().equals(postId)
                || !comment.getPost().getGroup().getId().equals(groupId)) {
            throw new ExceptionList(PathErrorCode.NOT_VALID_PATH);
        }
        if (!comment.getMemberId().equals(memberId)) {
            throw new ExceptionList(CommentErrorCode.ONLY_WRITER_MEMBER_DELETE);
        }
        commentRepo.delete(comment);
    }
}

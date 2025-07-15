package com.example.service;

import com.example.dto.request.CommentRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.exception.*;
import com.example.exception.errorCode.CommentErrorCode;
import com.example.exception.errorCode.PathErrorCode;
import com.example.exception.errorCode.PostErrorCode;
import com.example.model.GroupPost;
import com.example.model.PostComment;
import com.example.repository.GroupPostRepository;
import com.example.repository.ParticipationRepository;
import com.example.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommentService {
    private final PostCommentRepository commentRepo;
    private final GroupPostRepository postRepo;
    private final ParticipationRepository partRepo;

    public CommentResponseDto create(Long postId, Long memberId, CommentRequestDto dto) {
        GroupPost post = postRepo.findById(postId)
                .orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        boolean isMember = partRepo.existsByGroupIdAndMemberId(
                post.getGroup().getId(), memberId);

        if (!isMember) throw new ExceptionList(CommentErrorCode.ONLY_GROUP_MEMBER);

        PostComment comment = dto.toEntity(post, memberId);
        return CommentResponseDto.fromEntity(commentRepo.save(comment));
    }

    @Transactional(readOnly=true)
    public Page<CommentResponseDto> list(Long postId, Pageable pageable) {
        return commentRepo.findByPostId(postId, pageable)
                .map(CommentResponseDto::fromEntity);
    }

    public CommentResponseDto update(
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
            throw new ExceptionList(PathErrorCode.NOT_VALID_PATH);
        }
        if (!comment.getMemberId().equals(memberId)) {
            throw new ExceptionList(CommentErrorCode.ONLY_WRITER_MEMBER_UPDATE);
        }

        comment.update(dto.content());
        return CommentResponseDto.fromEntity(comment);
    }
    public void delete(Long groupId, Long postId, Long commentId, Long memberId) {
        PostComment comment = commentRepo.findById(commentId)
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

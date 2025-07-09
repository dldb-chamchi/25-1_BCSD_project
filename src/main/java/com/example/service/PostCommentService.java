package com.example.service;

import com.example.dto.request.CommentRequestDto;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.PostComment;
import com.example.repository.GroupPostRepository;
import com.example.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

    public PostComment create(Long postId, Long memberId, String content) {
        var p = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        boolean isMember = participationService.listByGroup(p.getGroup().getId())
                .stream().anyMatch(x -> x.getMemberId().equals(memberId));
        if (!isMember) throw new BadRequestException("그룹 참여자만 댓글을 달 수 있습니다");
        var c = PostComment.builder()
                .post(p)
                .memberId(memberId)
                .content(content)
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

    public PostComment update(
            Long groupId,
            Long postId,
            Long commentId,
            Long memberId,
            CommentRequestDto dto
    ) {
        PostComment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다: " + commentId));

        if (!comment.getPost().getId().equals(postId)
                || !comment.getPost().getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 경로(groupId/postId) 입니다");
        }
        if (!comment.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("댓글 작성자만 수정할 수 있습니다");
        }

        comment.update(dto.getContent());
        return comment;
    }
}

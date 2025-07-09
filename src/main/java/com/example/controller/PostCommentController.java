package com.example.controller;

import com.example.dto.request.CommentRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.exception.BadRequestException;
import com.example.model.Member;
import com.example.model.PostComment;
import com.example.service.MemberService;
import com.example.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {
    private final PostCommentService commentService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user,
            @RequestBody CommentRequestDto dto
    ) {
        var member = memberService.getByEmail(user.getUsername());
        var c = commentService.create(postId, member.getId(), dto.getContent());

        if (!c.getPost().getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 그룹 경로입니다");
        }
        var res = new CommentResponseDto(
                c.getId(), c.getMemberId(), c.getContent(), c.getCreatedAt()
        );
        return ResponseEntity
                .created(URI.create("/api/groups/" + groupId + "/posts/" + postId + "/comments/" + c.getId()))
                .body(res);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> list(
            @PathVariable Long groupId,
            @PathVariable Long postId
    ) {
        var dtos = commentService.list(postId).stream()
                .filter(c -> c.getPost().getGroup().getId().equals(groupId))
                .map(c -> new CommentResponseDto(
                        c.getId(), c.getMemberId(), c.getContent(), c.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails user,
            @RequestBody CommentRequestDto dto
    ) {
        Member m = memberService.getByEmail(user.getUsername());
        PostComment updated = commentService.update(
                groupId, postId, commentId,
                m.getId(), dto
        );
        CommentResponseDto res = new CommentResponseDto(
                updated.getId(),
                updated.getMemberId(),
                updated.getContent(),
                updated.getCreatedAt()
        );
        return ResponseEntity.ok(res);
    }
}

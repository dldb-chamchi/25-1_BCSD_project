package com.example.controller;

import com.example.controller.swagger.CommentApi;
import com.example.dto.request.CommentRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.model.Member;
import com.example.service.MemberService;
import com.example.service.PostCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController implements CommentApi {
    private final PostCommentService commentService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CommentRequestDto dto
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        CommentResponseDto response = commentService.create(postId, memberId, dto);
        return ResponseEntity
                .created(URI.create("/api/groups/" + groupId + "/posts/" + postId + "/comments/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> list(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(commentService.list(postId, pageable).getContent());
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CommentRequestDto dto
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        CommentResponseDto updated = commentService.update(groupId, postId, commentId, memberId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails user
    ) {
        Member m = memberService.getByEmail(user.getUsername());
        commentService.delete(groupId, postId, commentId, m.getId());
        return ResponseEntity.noContent().build();
    }
}

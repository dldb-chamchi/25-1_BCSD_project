package com.example.controller;

import com.example.dto.request.CommentRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.model.PostComment;
import com.example.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {
    private final PostCommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto dto
    ) {
        PostComment c = commentService.create(postId, dto);
        return ResponseEntity
                .created(URI.create("/api/groups/" + dto.getMemberId()
                        + "/posts/" + postId + "/comments/" + c.getId()))
                .body(new CommentResponseDto(c.getId(), c.getMemberId(),
                        c.getContent(), c.getCreatedAt()));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> list(@PathVariable Long postId) {
        var dtos = commentService.list(postId).stream()
                .map(c -> new CommentResponseDto(c.getId(), c.getMemberId(),
                        c.getContent(), c.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}

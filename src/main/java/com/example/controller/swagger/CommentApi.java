package com.example.controller.swagger;

import com.example.dto.request.CommentRequestDto;
import com.example.dto.response.CommentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/groups/{groupId}/posts/{postId}/comments")
@Tag(name = "Comment", description = "댓글 관련 API")
public interface CommentApi {

    @Operation(summary = "댓글 생성", description = "그룹 참여자만 가능합니다")
    @ApiResponse(responseCode = "200", description = "댓글 생성 성공", content = @Content(mediaType = "application/json"))
    @PostMapping
    ResponseEntity<CommentResponseDto> create(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CommentRequestDto dto
    );

    @Operation(summary = "댓글 모두 조회")
    @ApiResponse(responseCode = "200", description = "댓글 모두 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping
    ResponseEntity<List<CommentResponseDto>> list(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "댓글 변경", description = "작성자만 가능합니다")
    @ApiResponse(responseCode = "200", description = "댓글 변경 성공", content = @Content(mediaType = "application/json"))
    @PutMapping("/{commentId}")
    ResponseEntity<CommentResponseDto> update(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CommentRequestDto dto
    );

    @Operation(summary = "댓글 삭제", description = "작성자만 가능합니다")
    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공", content = @Content(mediaType = "application/json"))
    @DeleteMapping("/{commentId}")
    ResponseEntity<Void> delete(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails user
    );
}

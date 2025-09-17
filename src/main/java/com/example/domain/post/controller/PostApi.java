package com.example.domain.post.controller;

import com.example.domain.post.dto.PostRequestDto;
import com.example.domain.post.dto.PostResponseDto;
import com.example.global.exception.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/groups/{groupId}/posts")
@Tag(name = "Post", description = "게시글 관련 API")
public interface PostApi {

    @Operation(summary = "그룹 게시글 생성", description = "호스트만 가능합니다")
    @ApiResponse(responseCode = "201", description = "그룹 게시글 생성 성공", content = @Content(mediaType = "application/json"))
    @PostMapping
    ResponseEntity<PostResponseDto> create(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PostRequestDto dto
    );

    @Operation(summary = "그룹 게시글 조회")
    @ApiResponse(responseCode = "200", description = "그룹 게시글 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping("/{postId}")
    ResponseEntity<PostResponseDto> getById(
            @PathVariable Long groupId,
            @PathVariable Long postId
    );

    @Operation(summary = "모든 그룹 게시글 조회")
    @ApiResponse(responseCode = "200", description = "모든 그룹 게시글 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping
    ResponseEntity<List<PostResponseDto>> list(
            @PathVariable Long groupId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "그룹 게시글 변경", description = "호스트만 가능합니다")
    @ApiResponse(responseCode = "200", description = "그룹 게시글 변경 성공", content = @Content(mediaType = "application/json"))
    @PutMapping("/{postId}")
    ResponseEntity<PostResponseDto> update(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PostRequestDto dto
    );

    @Operation(summary = "그룹 게시글 삭제", description = "호스트만 가능합니다/댓글이 있을 경우 불가능합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "그룹 게시글 삭제 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "호스트만 가능합니다/댓글이 있을 경우 불가능합니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @DeleteMapping("/{postId}")
    ResponseEntity<Void> delete(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user
    );
}

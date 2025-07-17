package com.example.controller.swagger;

import com.example.dto.request.GroupRequestDto;
import com.example.dto.request.UpdateStatusDto;
import com.example.dto.response.GroupResponseDto;
import com.example.exception.ExceptionResponse;
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

@RequestMapping("/api/groups")
@Tag(name = "Group", description = "그룹 관련 API")
public interface GroupApi {

    @Operation(summary = "그룹 만들기", description = "마감일의 경우 현재 시간보다 미래여야 합니다")
    @ApiResponse(responseCode = "201", description = "그룹 생성 성공", content = @Content(mediaType = "application/json"))
    @PostMapping
    ResponseEntity<GroupResponseDto> create(
            @Valid @RequestBody GroupRequestDto dto,
            @AuthenticationPrincipal UserDetails user
    );

    @Operation(summary = "모든 그룹 조회")
    @ApiResponse(responseCode = "200", description = "모든 그룹 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping
    ResponseEntity<List<GroupResponseDto>> list(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "그룹 조회")
    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<GroupResponseDto> get(
            @PathVariable Long id
    );

    @Operation(summary = "상태가 오픈인 그룹만 조회")
    @ApiResponse(responseCode = "200", description = "오픈 그룹 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping("/open")
    ResponseEntity<List<GroupResponseDto>> listOpen(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "상태가 오픈이고 참여인원이 남은 그룹만 조회")
    @GetMapping("/available")
    @ApiResponse(responseCode = "200", description = "가입 가능 그룹 조회 성공", content = @Content(mediaType = "application/json"))
    ResponseEntity<List<GroupResponseDto>> listAvailable(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "그룹 내용 변경", description = "호스트만 가능합니다")

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 내용 변경 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "호스트만 가능합니다/잘못된 경로입니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PutMapping("/{groupId}")
    ResponseEntity<GroupResponseDto> update(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody GroupRequestDto dto
    );

    @Operation(summary = "그룹 상태 변경(오픈, 마감)", description = "호스트만 가능합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "그룹 상태 변경 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "호스트만 가능합니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PatchMapping("/{groupId}/status")
    ResponseEntity<Void> patchStatus(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody UpdateStatusDto dto
    );

    @Operation(summary = "그룹 삭제", description = "호스트만 가능합니다/참여자 or 게시글이 있을 경우 삭제가 불가능합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "그룹 삭제 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "참여자 or 게시글이 있을 경우 삭제가 불가능합니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    );
}

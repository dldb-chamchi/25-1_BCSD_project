package com.example.controller.swagger;

import com.example.dto.response.ParticipationResponseDto;
import com.example.exception.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/groups/{groupId}/participants")
@Tag(name = "Participant", description = "참여자 관련 API")
public interface ParticipationApi {

    @Operation(summary = "그룹 참여", description = "참여자가 아닌 멤버만 가능합니다")
    @ApiResponse(responseCode = "201", description = "그룹 참여 성공", content = @Content(mediaType = "application/json"))
    @PostMapping
    ResponseEntity<ParticipationResponseDto> join(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user
    );

    @Operation(summary = "참여 기록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여 기록 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "그룹이 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @GetMapping
    ResponseEntity<List<ParticipationResponseDto>> list(
            @PathVariable Long groupId,
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "그룹 떠나기", description = "그룹 참여자만 가능합니다")
    @ApiResponse(responseCode = "204", description = "그룹 떠나기 성공", content = @Content(mediaType = "application/json"))
    @DeleteMapping
    ResponseEntity<Void> leave(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user
    );
}

package com.example.controller.swagger;

import com.example.dto.request.MemberRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.dto.response.GroupResponseDto;
import com.example.dto.response.MemberResponseDto;
import com.example.dto.response.PostResponseDto;
import com.example.exception.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@Tag(name = "User", description = "사용자 관련 API")
public interface MemberApi {

    @Operation(summary = "멤버 가입")
    @ApiResponse(responseCode = "201", description = "가입 성공", content = @Content(mediaType = "application/json"))
    @PostMapping
    ResponseEntity<MemberResponseDto> register(@Valid @RequestBody MemberRequestDto dto);

    @Operation(summary = "멤버 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "멤버 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{id}")
    ResponseEntity<MemberResponseDto> get(@PathVariable Long id);

    @Operation(summary = "가입된 그룹 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입된 그룹 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{memberId}/groups")
    ResponseEntity<List<GroupResponseDto>> getJoinedGroups(@PathVariable Long memberId,
                                                           @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                           Pageable pageable);

    @Operation(summary = "멤버가 쓴 게시글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{memberId}/posts")
    ResponseEntity<List<PostResponseDto>> getMemberPosts(@PathVariable Long memberId,
                                                     @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                            Pageable pageable);

    @Operation(summary = "멤버가 쓴 댓글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{memberId}/comments")
    ResponseEntity<List<CommentResponseDto>> getMemberComments(@PathVariable Long memberId,
                                                                  @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                  Pageable pageable);

    @Operation(summary = "본인이 가입한 그룹 모두 조회")
    @ApiResponse(responseCode = "200", description = "그룹 모두 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping("/me/groups")
    ResponseEntity<List<GroupResponseDto>> myAllGroups(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "본인이 호스트인 그룹 모두 조회")
    @ApiResponse(responseCode = "200", description = "그룹 모두 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping("/me/groups/host")
    ResponseEntity<List<GroupResponseDto>> myHostGroups(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable);

    @Operation(summary = "본인이 쓴 게시글 조회")
    @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping("/me/posts")
    ResponseEntity<List<PostResponseDto>> myPosts(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "본인이 쓴 댓글 조회")
    @ApiResponse(responseCode = "200", description = "댓글 조회 성공", content = @Content(mediaType = "application/json"))
    @GetMapping("/me/comments")
    ResponseEntity<List<CommentResponseDto>> myComments(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable);

    @Operation(summary = "멤버 탈퇴")
    @ApiResponse(responseCode = "204", description = "멤버 탈퇴 성공", content = @Content(mediaType = "application/json"))
    @DeleteMapping
    ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails user,
                                HttpServletRequest request);
}

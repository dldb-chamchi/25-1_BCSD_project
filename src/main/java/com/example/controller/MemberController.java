package com.example.controller;

import com.example.dto.request.MemberRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.dto.response.GroupResponseDto;
import com.example.dto.response.MemberResponseDto;
import com.example.dto.response.PostResponseDto;
import com.example.model.Member;
import com.example.service.GroupPostService;
import com.example.service.MemberService;
import com.example.service.PostCommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
public class MemberController {
    private final MemberService memberService;
    private final GroupPostService postService;
    private final PostCommentService commentService;

    @PostMapping
    public ResponseEntity<MemberResponseDto> register(@Valid @RequestBody MemberRequestDto dto) {
        Member m = memberService.register(dto);
        return ResponseEntity
                .created(URI.create("/api/members/" + m.getId()))
                .body(MemberResponseDto.fromEntity(m));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> get(@PathVariable Long id) {
        Member m = memberService.get(id);
        return ResponseEntity.ok(MemberResponseDto.fromEntity(m));
    }

    @GetMapping("/{memberId}/groups")
    public ResponseEntity<List<GroupResponseDto>> getJoinedGroups(@PathVariable Long memberId) {
        List<GroupResponseDto> dtos = memberService.getJoinedGroups(memberId).stream()
                .map(GroupResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{memberId}/posts")
    public ResponseEntity<List<PostResponseDto>> getMyPosts(@PathVariable Long memberId,
                                                            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                            Pageable pageable) {
        memberService.get(memberId);
        List<PostResponseDto> dtos = postService.listByHost(memberId, pageable).stream()
                .map(PostResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{memberId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getMyComments(@PathVariable Long memberId,
                                                                  @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        memberService.get(memberId);
        List<CommentResponseDto> dtos = commentService.listByMember(memberId, pageable).stream()
                .map(CommentResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/me/groups")
    public ResponseEntity<List<GroupResponseDto>> myAllGroups(
            @AuthenticationPrincipal UserDetails user
    ) {
        Member m = memberService.getByEmail(user.getUsername());
        List<GroupResponseDto> dtos = memberService.getJoinedGroups(m.getId()).stream()
                .map(GroupResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/me/groups/host")
    public ResponseEntity<List<GroupResponseDto>> myHostGroups(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Member m = memberService.getByEmail(user.getUsername());
        List<GroupResponseDto> dtos = memberService.getHostGroups(m.getId(), pageable).stream()
                .map(GroupResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<List<PostResponseDto>> myPosts(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Member m = memberService.getByEmail(user.getUsername());
        List<PostResponseDto> dtos = postService.listByHost(m.getId(), pageable).stream()
                .map(PostResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/me/comments")
    public ResponseEntity<List<CommentResponseDto>> myComments(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Member m = memberService.getByEmail(user.getUsername());
        List<CommentResponseDto> dtos = commentService.listByMember(m.getId(), pageable).stream()
                .map(CommentResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

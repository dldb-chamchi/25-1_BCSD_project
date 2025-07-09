package com.example.controller;

import com.example.dto.request.MemberRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.dto.response.GroupResponseDto;
import com.example.dto.response.MemberResponseDto;
import com.example.dto.response.PostResponseDto;
import com.example.model.Member;
import com.example.model.PurchaseGroup;
import com.example.service.GroupPostService;
import com.example.service.MemberService;
import com.example.service.PostCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
public class MemberController {
    private final MemberService memberService;
    private final GroupPostService postService;
    private final PostCommentService commentService;

    @PostMapping
    public ResponseEntity<MemberResponseDto> register(@RequestBody MemberRequestDto dto) {
        Member m = memberService.register(dto);
        MemberResponseDto res = new MemberResponseDto(
                m.getId(), m.getEmail(), m.getName(), m.getCreatedAt()
        );
        return ResponseEntity
                .created(URI.create("/api/members/" + m.getId()))
                .body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> get(@PathVariable Long id) {
        Member m = memberService.get(id);
        return ResponseEntity.ok(new MemberResponseDto(
                m.getId(), m.getEmail(), m.getName(), m.getCreatedAt()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}/groups")
    @Operation(summary = "단일 사용자 조회", description = "ID로 사용자 정보를 조회합니다.")
    public ResponseEntity<List<GroupResponseDto>> getJoinedGroups(@PathVariable Long memberId) {
        List<PurchaseGroup> groups = memberService.getJoinedGroups(memberId);
        List<GroupResponseDto> dtos = groups.stream()
                .map(g -> new GroupResponseDto(
                        g.getId(),
                        g.getTitle(),
                        g.getDescription(),
                        g.getExpiresAt(),
                        g.getMaxMembers(),
                        g.getStatus(),
                        g.getParticipants().size()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{memberId}/posts")
    public ResponseEntity<List<PostResponseDto>> getMyPosts(@PathVariable Long memberId,
                                                            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                                            Pageable pageable) {
        var dtos = postService.listByHost(memberId, pageable).stream()
                .map(p -> new PostResponseDto(
                        p.getId(), p.getHostId(), p.getTitle(),
                        p.getContent(), p.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{memberId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getMyComments(@PathVariable Long memberId) {
        var dtos = commentService.listByMember(memberId).stream()
                .map(c -> new CommentResponseDto(
                        c.getId(), c.getMemberId(), c.getContent(), c.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}

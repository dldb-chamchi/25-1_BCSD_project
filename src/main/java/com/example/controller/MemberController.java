package com.example.controller;

import com.example.auth.AuthService;
import com.example.controller.swagger.MemberApi;
import com.example.dto.request.MemberRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.dto.response.GroupResponseDto;
import com.example.dto.response.MemberResponseDto;
import com.example.dto.response.PostResponseDto;
import com.example.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
public class MemberController implements MemberApi {
    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<MemberResponseDto> register(@Valid @RequestBody MemberRequestDto dto) {
        MemberResponseDto response = memberService.register(dto);
        return ResponseEntity
                .created(URI.create("/api/members/" + response.id()))
                .body(response);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> get(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.get(memberId));
    }

    @GetMapping("/{memberId}/groups")
    public ResponseEntity<List<GroupResponseDto>> getJoinedGroups(@PathVariable Long memberId,
                                                                  @PageableDefault(sort = "joinedAt", direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        return ResponseEntity.ok(memberService.getJoinedGroups(memberId, pageable).getContent());
    }

    @GetMapping("/{memberId}/posts")
    public ResponseEntity<List<PostResponseDto>> getMemberPosts(@PathVariable Long memberId,
                                                                @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                            Pageable pageable) {
        return ResponseEntity.ok(memberService.getMemberPosts(memberId, pageable).getContent());
    }

    @GetMapping("/{memberId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getMemberComments(@PathVariable Long memberId,
                                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        return ResponseEntity.ok(memberService.getMemberComments(memberId, pageable).getContent());
    }

    @GetMapping("/me/groups")
    public ResponseEntity<List<GroupResponseDto>> myAllGroups(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "joinedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        List<GroupResponseDto> response = memberService.getJoinedGroups(memberId, pageable).getContent();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/groups/host")
    public ResponseEntity<List<GroupResponseDto>> myHostGroups(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        return ResponseEntity.ok(memberService.getHostGroups(memberId, pageable).getContent());
    }

    @GetMapping("/me/posts")
    public ResponseEntity<List<PostResponseDto>> myPosts(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        return ResponseEntity.ok(memberService.getMemberPosts(memberId, pageable).getContent());
    }

    @GetMapping("/me/comments")
    public ResponseEntity<List<CommentResponseDto>> myComments(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        return ResponseEntity.ok(memberService.getMemberComments(memberId, pageable).getContent());
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails user,
                                       HttpServletRequest request) {

        Long memberId  = memberService.getByEmail(user.getUsername()).getId();
        memberService.delete(memberId);
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}

package com.example.domain.post.controller;

import com.example.domain.post.dto.PostRequestDto;
import com.example.domain.post.dto.PostResponseDto;
import com.example.domain.post.service.PostService;
import com.example.domain.member.service.MemberService;
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
@RequestMapping("/api/groups/{groupId}/posts")
@RequiredArgsConstructor
public class PostController implements PostApi {
    private final PostService postService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<PostResponseDto> create(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PostRequestDto dto
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        PostResponseDto response = postService.create(groupId, memberId, dto);
        return ResponseEntity
                .created(URI.create("/api/groups/" + groupId + "/posts/" + response.id()))
                .body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getById(
            @PathVariable Long groupId,
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(postService.getById(groupId, postId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> list(@PathVariable Long groupId,
                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                      Pageable pageable) {
        return ResponseEntity.ok( postService.list(groupId, pageable).getContent());
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> update(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PostRequestDto dto
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        PostResponseDto updated = postService.update(groupId, postId, memberId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        postService.delete(groupId, postId, memberId);
        return ResponseEntity.noContent().build();
    }
}

package com.example.controller;

import com.example.dto.request.PostRequestDto;
import com.example.dto.response.PostResponseDto;
import com.example.model.GroupPost;
import com.example.model.Member;
import com.example.service.GroupPostService;
import com.example.service.MemberService;
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
public class GroupPostController {
    private final GroupPostService postService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<PostResponseDto> create(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PostRequestDto dto
    ) {
        var m = memberService.getByEmail(user.getUsername());
        var p = postService.create(groupId, m.getId(), dto);
        return ResponseEntity
                .created(URI.create("/api/groups/" + groupId + "/posts/" + p.getId()))
                .body(PostResponseDto.fromEntity(p));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getById(
            @PathVariable Long groupId,
            @PathVariable Long postId
    ) {
        GroupPost p = postService.getById(groupId, postId);
        return ResponseEntity.ok(PostResponseDto.fromEntity(p));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> list(@PathVariable Long groupId,
                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                      Pageable pageable) {
        List<PostResponseDto> dtos = postService.list(groupId, pageable).stream()
                .map(PostResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> update(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PostRequestDto dto
    ) {
        Member host = memberService.getByEmail(user.getUsername());
        GroupPost updated = postService.update(groupId, postId, host.getId(), dto);
        return ResponseEntity.ok(PostResponseDto.fromEntity(updated));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user
    ) {
        Member m = memberService.getByEmail(user.getUsername());
        postService.delete(groupId, postId, m.getId());
        return ResponseEntity.noContent().build();
    }
}

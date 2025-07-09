package com.example.controller;

import com.example.dto.request.PostRequestDto;
import com.example.dto.response.PostResponseDto;
import com.example.service.GroupPostService;
import com.example.service.MemberService;
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
            @RequestBody PostRequestDto dto
    ) {
        var m = memberService.getByEmail(user.getUsername());
        var p = postService.create(groupId, m.getId(), dto);
        return ResponseEntity.created(URI.create("/api/groups/" + groupId + "/posts/" + p.getId())).
                body(new PostResponseDto(p.getId(), p.getHostId(), p.getTitle(), p.getContent(), p.getCreatedAt()));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> list(@PathVariable Long groupId,
                                                      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                                      Pageable pageable) {
        var dtos = postService.list(groupId, pageable).stream()
                .map(p -> new PostResponseDto(p.getId(), p.getHostId(),
                        p.getTitle(), p.getContent(), p.getCreatedAt())).toList();
        return ResponseEntity.ok(dtos);
    }
}

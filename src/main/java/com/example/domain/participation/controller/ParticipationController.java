package com.example.domain.participation.controller;

import com.example.domain.participation.dto.ParticipationResponseDto;
import com.example.domain.member.service.MemberService;
import com.example.domain.participation.service.ParticipationService;
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
@RequestMapping("/api/groups/{groupId}/participants")
@RequiredArgsConstructor
public class ParticipationController implements ParticipationApi {
    private final ParticipationService partService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ParticipationResponseDto> join(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        ParticipationResponseDto response = partService.join(groupId, memberId);
        return ResponseEntity
                .created(URI.create("/api/groups/" + groupId + "/participants/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationResponseDto>> list(@PathVariable Long groupId,
                                                               @PageableDefault(sort = "joinedAt", direction = Sort.Direction.DESC)
                                                               Pageable pageable) {
        return ResponseEntity.ok(partService.listByGroup(groupId, pageable).getContent());
    }

    @DeleteMapping
    public ResponseEntity<Void> leave(@PathVariable Long groupId,
                                      @AuthenticationPrincipal UserDetails user) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        partService.leave(groupId, memberId);
        return ResponseEntity.noContent().build();
    }
}

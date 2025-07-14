package com.example.controller;

import com.example.controller.swagger.ParticipationApi;
import com.example.dto.response.ParticipationResponseDto;
import com.example.service.MemberService;
import com.example.service.ParticipationService;
import lombok.RequiredArgsConstructor;
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
        var m = memberService.getByEmail(user.getUsername());
        var p = partService.join(groupId, m.getId());
        return ResponseEntity.created(
                        URI.create("/api/groups/" + groupId + "/participants/" + p.getId()))
                .body(ParticipationResponseDto.fromEntity(p));
    }

    @GetMapping
    public ResponseEntity<List<ParticipationResponseDto>> list(@PathVariable Long groupId) {
        List<ParticipationResponseDto> dtos = partService.listByGroup(groupId).stream()
                .map(ParticipationResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping
    public ResponseEntity<Void> leave(@PathVariable Long groupId,
                                      @AuthenticationPrincipal UserDetails user) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        partService.leave(groupId, memberId);
        return ResponseEntity.noContent().build();
    }
}

package com.example.controller;

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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups/{groupId}/participants")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationService partService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ParticipationResponseDto> join(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user
    ) {
        var m = memberService.getByEmail(user.getUsername());
        var p = partService.join(groupId, m.getId());
        var res = new ParticipationResponseDto(
                p.getId(), p.getMemberId(), p.getJoinedAt(), p.getPaymentStatus()
        );
        return ResponseEntity.created(
                URI.create("/api/groups/" + groupId + "/participants/" + p.getId())
        ).body(res);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationResponseDto>> list(@PathVariable Long groupId) {
        List<ParticipationResponseDto> dtos = partService.listByGroup(groupId).stream()
                .map(p -> new ParticipationResponseDto(
                        p.getId(), p.getMemberId(), p.getJoinedAt(), p.getPaymentStatus()
                )).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> leave(@PathVariable Long groupId, @PathVariable Long memberId) {
        partService.leave(groupId, memberId);
        return ResponseEntity.noContent().build();
    }
}

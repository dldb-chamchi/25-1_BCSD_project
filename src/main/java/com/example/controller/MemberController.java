package com.example.controller;

import com.example.dto.request.MemberRequestDto;
import com.example.dto.response.GroupResponseDto;
import com.example.dto.response.MemberResponseDto;
import com.example.model.Member;
import com.example.model.PurchaseGroup;
import com.example.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

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
}

package com.example.controller;

import com.example.dto.request.GroupRequestDto;
import com.example.dto.response.GroupResponseDto;
import com.example.model.Member;
import com.example.model.PurchaseGroup;
import com.example.service.MemberService;
import com.example.service.PurchaseGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class PurchaseGroupController {
    private final PurchaseGroupService groupService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<GroupResponseDto> create(
            @RequestBody GroupRequestDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        var m = memberService.getByEmail(user.getUsername());
        var g = groupService.create(dto, m.getId());
        return ResponseEntity
                .created(URI.create("/api/groups/" + g.getId()))
                .body(new GroupResponseDto(
                        g.getId(), g.getTitle(), g.getDescription(),
                        g.getExpiresAt(), g.getMaxMembers(), g.getStatus(), g.getParticipants().size()
                ));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> list() {
        List<GroupResponseDto> dtos = groupService.listAll().stream()
                .map(g -> new GroupResponseDto(
                        g.getId(), g.getTitle(), g.getDescription(),
                        g.getExpiresAt(), g.getMaxMembers(), g.getStatus(),
                        g.getParticipants().size()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> get(@PathVariable Long id) {
        PurchaseGroup g = groupService.get(id);
        GroupResponseDto res = new GroupResponseDto(
                g.getId(), g.getTitle(), g.getDescription(),
                g.getExpiresAt(), g.getMaxMembers(), g.getStatus(),
                g.getParticipants().size()
        );
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponseDto> update(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @RequestBody GroupRequestDto dto
    ) {
        Member m = memberService.getByEmail(user.getUsername());
        PurchaseGroup updated = groupService.update(groupId, m.getId(), dto);
        GroupResponseDto res = new GroupResponseDto(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getExpiresAt(),
                updated.getMaxMembers(),
                updated.getStatus(),
                updated.getParticipants().size()
        );
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{groupId}/status")
    public ResponseEntity<Void> patchStatus(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @RequestParam("status") String status
    ) {
        var m = memberService.getByEmail(user.getUsername());
        groupService.changeStatus(groupId, m.getId(), status.toUpperCase());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        var m = memberService.getByEmail(user.getUsername());
        groupService.delete(id, m.getId());
        return ResponseEntity.noContent().build();
    }
}

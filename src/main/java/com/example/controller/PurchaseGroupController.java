package com.example.controller;

import com.example.dto.request.GroupRequestDto;
import com.example.dto.response.GroupResponseDto;
import com.example.model.Member;
import com.example.model.PurchaseGroup;
import com.example.service.MemberService;
import com.example.service.PurchaseGroupService;
import jakarta.validation.Valid;
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
    public ResponseEntity<GroupResponseDto> create(@Valid
            @RequestBody GroupRequestDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        var m = memberService.getByEmail(user.getUsername());
        var g = groupService.create(dto, m.getId());
        return ResponseEntity
                .created(URI.create("/api/groups/" + g.getId()))
                .body(GroupResponseDto.fromEntity(g));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> list() {
        List<GroupResponseDto> dtos = groupService.listAll().stream()
                .map(GroupResponseDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(GroupResponseDto.fromEntity(groupService.get(id)));
    }

    @GetMapping("/open")
    public ResponseEntity<List<GroupResponseDto>> listOpen() {
        List<GroupResponseDto> dtos = groupService.listOpen().stream()
                .map(GroupResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/available")
    public ResponseEntity<List<GroupResponseDto>> listAvailable() {
        List<GroupResponseDto> dtos = groupService.listAvailable().stream()
                .map(GroupResponseDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponseDto> update(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody GroupRequestDto dto
    ) {
        Member m = memberService.getByEmail(user.getUsername());
        PurchaseGroup updated = groupService.update(groupId, m.getId(), dto);
        return ResponseEntity.ok(GroupResponseDto.fromEntity(updated));
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

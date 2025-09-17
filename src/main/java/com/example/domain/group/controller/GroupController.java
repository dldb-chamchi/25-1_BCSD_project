package com.example.domain.group.controller;

import com.example.domain.group.dto.GroupRequestDto;
import com.example.domain.group.dto.GroupStatusRequestDto;
import com.example.domain.group.dto.GroupResponseDto;
import com.example.domain.member.service.MemberService;
import com.example.domain.group.service.GroupService;
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
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController implements GroupApi {
    private final GroupService groupService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<GroupResponseDto> create(@Valid
            @RequestBody GroupRequestDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        GroupResponseDto response = groupService.create(dto, memberId);
        return ResponseEntity
                .created(URI.create("/api/groups/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> list(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(groupService.listAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.get(id));
    }

    @GetMapping("/open")
    public ResponseEntity<List<GroupResponseDto>> listOpen(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(groupService.listOpen(pageable).getContent());
    }

    @GetMapping("/available")
    public ResponseEntity<List<GroupResponseDto>> listAvailable(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(groupService.listAvailable(pageable).getContent());
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponseDto> update(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody GroupRequestDto dto
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        GroupResponseDto updated = groupService.update(groupId, memberId, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{groupId}/status")
    public ResponseEntity<Void> patchStatus(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody GroupStatusRequestDto dto
            ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        groupService.changeStatus(groupId, memberId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        Long memberId = memberService.getByEmail(user.getUsername()).getId();
        groupService.delete(id, memberId);
        return ResponseEntity.noContent().build();
    }
}

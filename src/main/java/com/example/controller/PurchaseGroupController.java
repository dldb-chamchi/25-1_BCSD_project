package com.example.controller;

import com.example.dto.request.GroupRequestDto;
import com.example.dto.response.GroupResponseDto;
import com.example.model.PurchaseGroup;
import com.example.service.PurchaseGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class PurchaseGroupController {
    private final PurchaseGroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponseDto> create(@RequestBody GroupRequestDto dto,
                                                   @RequestHeader("X-User-Id") Long hostId) {
        PurchaseGroup g = groupService.create(dto, hostId);
        GroupResponseDto res = new GroupResponseDto(
                g.getId(), g.getTitle(), g.getDescription(),
                g.getExpiresAt(), g.getMaxMembers(), g.getStatus(),
                g.getParticipants().size()
        );
        return ResponseEntity.created(URI.create("/api/groups/" + g.getId())).body(res);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("X-User-Id") Long hostId) {
        groupService.delete(id, hostId);
        return ResponseEntity.noContent().build();
    }
}

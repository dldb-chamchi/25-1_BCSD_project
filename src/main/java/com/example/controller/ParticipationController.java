package com.example.controller;

import com.example.dto.request.ParticipationRequestDto;
import com.example.dto.response.ParticipationResponseDto;
import com.example.model.Participation;
import com.example.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups/{groupId}/participants")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationService partService;

    @PostMapping
    public ResponseEntity<ParticipationResponseDto> join(@PathVariable Long groupId,
                                                         @RequestBody ParticipationRequestDto dto) {
        Participation p = partService.join(groupId, dto);
        ParticipationResponseDto res = new ParticipationResponseDto(
                p.getId(), p.getMemberId(), p.getJoinedAt(), p.getPaymentStatus()
        );
        return ResponseEntity.created(URI.create(
                "/api/groups/" + groupId + "/participants/" + p.getId()
        )).body(res);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationResponseDto>> list(@PathVariable Long groupId) {
        List<ParticipationResponseDto> dtos = partService.listByGroup(groupId).stream()
                .map(p -> new ParticipationResponseDto(
                        p.getId(), p.getMemberId(), p.getJoinedAt(), p.getPaymentStatus()
                )).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> leave(@PathVariable Long groupId, @PathVariable Long id) {
        Participation p = partService.listByGroup(groupId).stream()
                .filter(x -> x.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("참여 내역이 없습니다"));
        partService.leave(groupId, p.getMemberId());
        return ResponseEntity.noContent().build();
    }
}

package com.example.service;

import com.example.dto.request.GroupRequestDto;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.PurchaseGroup;
import com.example.repository.GroupPostRepository;
import com.example.repository.PurchaseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.model.Participation;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseGroupService {
    private final PurchaseGroupRepository groupRepo;
    private final GroupPostRepository postRepo;

    public PurchaseGroup create(GroupRequestDto dto, Long hostId) {
        PurchaseGroup group = PurchaseGroup.builder()
                .hostId(hostId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .expiresAt(dto.getExpiresAt())
                .maxMembers(dto.getMaxMembers())
                .createdAt(LocalDateTime.now())
                .status("OPEN")
                .build();

        Participation hostParticipation = Participation.builder()
                .group(group)
                .memberId(hostId)
                .joinedAt(LocalDateTime.now())
                .paymentStatus("PENDING")
                .build();
        group.addParticipant(hostParticipation);

        return groupRepo.save(group);
    }

    @Transactional(readOnly = true)
    public List<PurchaseGroup> listAll() {
        return groupRepo.findAll();
    }

    @Transactional(readOnly = true)
    public PurchaseGroup get(Long id) {
        return groupRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("그룹을 찾을 수 없습니다: " + id));
    }

    public void delete(Long id, Long hostId) {
        PurchaseGroup g = get(id);
        if (!g.getHostId().equals(hostId)) {
            throw new BadRequestException("호스트만 삭제 가능합니다");
        }
        groupRepo.delete(g);
    }
}

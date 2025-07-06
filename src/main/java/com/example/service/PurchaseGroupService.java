package com.example.service;

import com.example.dto.request.GroupRequestDto;
import com.example.model.PurchaseGroup;
import com.example.repository.PurchaseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseGroupService {
    private final PurchaseGroupRepository groupRepo;

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
        return groupRepo.save(group);
    }

    @Transactional(readOnly = true)
    public List<PurchaseGroup> listAll() {
        return groupRepo.findAll();
    }

    @Transactional(readOnly = true)
    public PurchaseGroup get(Long id) {
        return groupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다: " + id));
    }

    public void delete(Long id, Long hostId) {
        PurchaseGroup g = get(id);
        if (!g.getHostId().equals(hostId)) {
            throw new RuntimeException("호스트만 삭제 가능합니다");
        }
        groupRepo.delete(g);
    }
}

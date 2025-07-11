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
        PurchaseGroup group = dto.toEntity(hostId);

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

    @Transactional(readOnly = true)
    public List<PurchaseGroup> listOpen() {
        return groupRepo.findByStatus("OPEN");
    }

    @Transactional(readOnly = true)
    public List<PurchaseGroup> listAvailable() {
        return groupRepo.findAvailableByStatusAndMaxMembers("OPEN");
    }

    public PurchaseGroup update(Long groupId, Long hostId, GroupRequestDto dto) {
        PurchaseGroup g = get(groupId);
        if (!g.getHostId().equals(hostId)) {
            throw new BadRequestException("호스트만 수정할 수 있습니다");
        }
        g.update(dto.title(), dto.description(), dto.maxMembers(), dto.expiresAt());
        return g;
    }

    public void changeStatus(Long groupId, Long hostId, String newStatus) {
        PurchaseGroup g = get(groupId);
        if (!g.getHostId().equals(hostId)) {
            throw new BadRequestException("호스트만 상태를 변경할 수 있습니다");
        }
        g.updateStatus(newStatus);
    }

    public void delete(Long id, Long hostId) {
        PurchaseGroup g = get(id);
        if (!g.getHostId().equals(hostId)) {
            throw new BadRequestException("호스트만 삭제 가능합니다");
        }
        if (g.getParticipants().size() > 1) {
            throw new BadRequestException("참여자가 남아있는 그룹은 삭제할 수 없습니다");
        }
        if (postRepo.existsByGroupId(id)) {
            throw new BadRequestException("게시글이 존재하는 그룹은 삭제할 수 없습니다");
        }
        groupRepo.delete(g);
    }
}

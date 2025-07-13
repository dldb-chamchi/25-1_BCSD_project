package com.example.service;

import com.example.dto.request.GroupRequestDto;
import com.example.exception.ExceptionList;
import com.example.exception.errorCode.GroupErrorCode;
import com.example.model.PurchaseGroup;
import com.example.repository.GroupPostRepository;
import com.example.repository.PurchaseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.model.Participation;

import java.time.LocalDateTime;

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
    public Page<PurchaseGroup> listAll(Pageable pageable) {
        return groupRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public PurchaseGroup get(Long id) {
        return groupRepo.findById(id)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));
    }

    @Transactional(readOnly = true)
    public Page<PurchaseGroup> listOpen(Pageable pageable) {
        return groupRepo.findByStatus("OPEN", pageable);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseGroup> listAvailable(Pageable pageable) {
        return groupRepo.findAvailableByStatusAndMaxMembers("OPEN", pageable);
    }

    public PurchaseGroup update(Long groupId, Long hostId, GroupRequestDto dto) {
        PurchaseGroup g = get(groupId);
        if (!g.getHostId().equals(hostId)) {
            throw new ExceptionList(GroupErrorCode.HOST_ONLY_GROUP_UPDATE);
        }
        g.update(dto.title(), dto.description(), dto.maxMembers(), dto.expiresAt());
        return g;
    }

    public void changeStatus(Long groupId, Long hostId, String newStatus) {
        PurchaseGroup g = get(groupId);
        if (!g.getHostId().equals(hostId)) {
            throw new ExceptionList(GroupErrorCode.HOST_ONLY_GROUP_UPDATE);
        }
        g.updateStatus(newStatus);
    }

    public void delete(Long id, Long hostId) {
        PurchaseGroup g = get(id);
        if (!g.getHostId().equals(hostId)) {
            throw new ExceptionList(GroupErrorCode.HOST_ONLY_GROUP_DELETE);
        }
        if (g.getParticipants().size() > 1) {
            throw new ExceptionList(GroupErrorCode.NOT_DELETE_WITH_PARTICIPATION);
        }
        if (postRepo.existsByGroupId(id)) {
            throw new ExceptionList(GroupErrorCode.NOT_DELETE_WITH_POST);
        }
        groupRepo.delete(g);
    }
}

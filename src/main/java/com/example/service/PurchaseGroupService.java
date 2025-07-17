package com.example.service;

import com.example.dto.request.GroupRequestDto;
import com.example.dto.request.UpdateStatusDto;
import com.example.dto.response.GroupResponseDto;
import com.example.exception.ExceptionList;
import com.example.exception.errorCode.GroupErrorCode;
import com.example.model.PurchaseGroup;
import com.example.model.PurchaseGroupStatus;
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
@Transactional(readOnly = true)
public class PurchaseGroupService {
    private final PurchaseGroupRepository groupRepo;
    private final GroupPostRepository postRepo;

    @Transactional
    public GroupResponseDto create(GroupRequestDto dto, Long hostId) {
        PurchaseGroup group = dto.toEntity(hostId);

        Participation hostParticipation = Participation.builder()
                .group(group)
                .memberId(hostId)
                .joinedAt(LocalDateTime.now())
                .build();
        group.addParticipant(hostParticipation);

        return GroupResponseDto.fromEntity(groupRepo.save(group));
    }

    public Page<GroupResponseDto> listAll(Pageable pageable) {
        return groupRepo.findAll(pageable)
                .map(GroupResponseDto::fromEntity);
    }

    public GroupResponseDto get(Long id) {
        PurchaseGroup group = groupRepo.findById(id)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));
        return GroupResponseDto.fromEntity(group);
    }

    public Page<GroupResponseDto> listOpen(Pageable pageable) {
        return groupRepo.findByStatus(PurchaseGroupStatus.OPEN, pageable)
                .map(GroupResponseDto::fromEntity);
    }

    public Page<GroupResponseDto> listAvailable(Pageable pageable) {
        return groupRepo.findAvailableByStatusAndMaxMembers(PurchaseGroupStatus.OPEN, pageable)
                .map(GroupResponseDto::fromEntity);
    }

    @Transactional
    public GroupResponseDto update(Long groupId, Long hostId, GroupRequestDto dto) {
        PurchaseGroup group = groupRepo.findById(groupId).
                orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

        if (!group.getHostId().equals(hostId)) {
            throw new ExceptionList(GroupErrorCode.HOST_ONLY_GROUP_UPDATE);
        }

        group.update(dto.title(), dto.description(), dto.maxMembers(), dto.expiresAt());
        return GroupResponseDto.fromEntity(group);
    }

    @Transactional
    public void changeStatus(Long groupId, Long hostId, UpdateStatusDto dto) {
        PurchaseGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

        if (!group.getHostId().equals(hostId)) {
            throw new ExceptionList(GroupErrorCode.HOST_ONLY_GROUP_UPDATE);
        }

        PurchaseGroupStatus newStatus = dto.status();

        if (newStatus == PurchaseGroupStatus.OPEN
                && group.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ExceptionList(GroupErrorCode.NOT_VALID_STATUS);
        }
        group.updateStatus(newStatus);
    }

    @Transactional
    public void delete(Long id, Long hostId) {
        PurchaseGroup g = groupRepo.findById(id)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

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

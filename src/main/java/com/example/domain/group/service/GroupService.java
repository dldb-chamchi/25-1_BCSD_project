package com.example.domain.group.service;

import com.example.domain.group.dto.GroupRequestDto;
import com.example.domain.group.dto.GroupStatusRequestDto;
import com.example.domain.group.dto.GroupResponseDto;
import com.example.global.exception.ExceptionList;
import com.example.global.exception.errorCode.GroupErrorCode;
import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupStatus;
import com.example.domain.post.repository.PostRepository;
import com.example.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.domain.participation.model.Participation;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepo;
    private final PostRepository postRepo;

    @Transactional
    public GroupResponseDto create(GroupRequestDto dto, Long hostId) {
        Group group = dto.toEntity(hostId);

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
        Group group = groupRepo.findById(id)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));
        return GroupResponseDto.fromEntity(group);
    }

    public Page<GroupResponseDto> listOpen(Pageable pageable) {
        return groupRepo.findByStatus(GroupStatus.OPEN, pageable)
                .map(GroupResponseDto::fromEntity);
    }

    public Page<GroupResponseDto> listAvailable(Pageable pageable) {
        return groupRepo.findAvailableByStatusAndMaxMembers(GroupStatus.OPEN, pageable)
                .map(GroupResponseDto::fromEntity);
    }

    @Transactional
    public GroupResponseDto update(Long groupId, Long hostId, GroupRequestDto dto) {
        Group group = groupRepo.findById(groupId).
                orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

        if (!group.getHostId().equals(hostId)) {
            throw new ExceptionList(GroupErrorCode.HOST_ONLY_GROUP_UPDATE);
        }

        group.update(dto.title(), dto.description(), dto.maxMember(), dto.expiresAt());
        return GroupResponseDto.fromEntity(group);
    }

    @Transactional
    public void changeStatus(Long groupId, Long hostId, GroupStatusRequestDto dto) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

        if (!group.getHostId().equals(hostId)) {
            throw new ExceptionList(GroupErrorCode.HOST_ONLY_GROUP_UPDATE);
        }

        GroupStatus newStatus = dto.status();

        if (newStatus == GroupStatus.OPEN
                && group.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ExceptionList(GroupErrorCode.NOT_VALID_STATUS);
        }
        group.updateStatus(newStatus);
    }

    @Transactional
    public void delete(Long id, Long hostId) {
        Group g = groupRepo.findById(id)
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

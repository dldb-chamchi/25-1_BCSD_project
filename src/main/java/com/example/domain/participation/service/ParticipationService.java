package com.example.domain.participation.service;

import com.example.domain.participation.dto.ParticipationResponseDto;
import com.example.global.exception.ExceptionList;
import com.example.global.exception.errorCode.GroupErrorCode;
import com.example.global.exception.errorCode.ParticipationErrorCode;
import com.example.domain.participation.model.Participation;
import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupStatus;
import com.example.domain.participation.repository.ParticipationRepository;
import com.example.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationService {
    private final ParticipationRepository partRepo;
    private final GroupRepository groupRepo;

    @Transactional
    public ParticipationResponseDto join(Long groupId, Long memberId) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

        if (group.getStatus() == GroupStatus.CLOSED) {
            throw new ExceptionList(ParticipationErrorCode.NOT_PARTICIPATE_GROUP_CLOSED);
        }
        if (group.getParticipants().size() >= group.getMaxMembers()) {
            throw new ExceptionList(ParticipationErrorCode.LIMIT_MAX_MEMBER);
        }
        if (partRepo.existsByGroupIdAndMemberId(groupId, memberId)) {
            throw new ExceptionList(ParticipationErrorCode.ALREADY_MEMBER);
        }

        Participation participation = Participation.builder()
                .group(group)
                .memberId(memberId)
                .joinedAt(LocalDateTime.now())
                .build();
        group.addParticipant(participation);
        return ParticipationResponseDto.fromEntity(partRepo.save(participation));
    }

    public Page<ParticipationResponseDto> listByGroup(Long groupId, Pageable pageable) {
        return partRepo.findByGroupId(groupId, pageable)
                .map(ParticipationResponseDto::fromEntity);
    }

    @Transactional
    public void leave(Long groupId, Long memberId) {
        Participation participation = partRepo.findByGroupId(groupId).stream()
                .filter(x -> x.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ExceptionList(ParticipationErrorCode.NOT_FOUND_PARTICIPATION));

        Group group = participation.getGroup();
        if (group.getHostId().equals(memberId)) {
            throw new ExceptionList(ParticipationErrorCode.NOT_LEAVE_HOST);
        }

        group.removeParticipant(participation);
        partRepo.delete(participation);
    }
}

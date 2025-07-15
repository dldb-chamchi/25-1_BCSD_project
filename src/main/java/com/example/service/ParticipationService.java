package com.example.service;

import com.example.dto.response.ParticipationResponseDto;
import com.example.exception.*;
import com.example.exception.errorCode.GroupErrorCode;
import com.example.exception.errorCode.ParticipationErrorCode;
import com.example.model.Participation;
import com.example.model.PurchaseGroup;
import com.example.model.PurchaseGroupStatus;
import com.example.repository.ParticipationRepository;
import com.example.repository.PurchaseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationService {
    private final ParticipationRepository partRepo;
    private final PurchaseGroupRepository groupRepo;

    public ParticipationResponseDto join(Long groupId, Long memberId) {
        PurchaseGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

        if (group.getStatus() == PurchaseGroupStatus.CLOSED) {
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

    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> listByGroup(Long groupId) {
        return partRepo.findByGroupId(groupId)
                .stream()
                .map(ParticipationResponseDto::fromEntity)
                .toList();
    }

    public void leave(Long groupId, Long memberId) {
        Participation participation = partRepo.findByGroupId(groupId).stream()
                .filter(x -> x.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ExceptionList(ParticipationErrorCode.NOT_FOUND_PARTICIPATION));

        PurchaseGroup group = participation.getGroup();
        if (group.getHostId().equals(memberId)) {
            throw new ExceptionList(ParticipationErrorCode.NOT_LEAVE_HOST);
        }

        group.removeParticipant(participation);
        partRepo.delete(participation);
    }
}

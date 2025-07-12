package com.example.service;

import com.example.exception.*;
import com.example.model.Participation;
import com.example.model.PurchaseGroup;
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

    public Participation join(Long groupId, Long memberId) {
        var g = groupRepo.findById(groupId)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));
        if ("CLOSED".equals(g.getStatus())) {
            throw new ExceptionList(ParticipationErrorCode.NOT_PARTICIPATE_GROUP_CLOSED);
        }
        if (g.getParticipants().size() >= g.getMaxMembers()) {
            throw new ExceptionList(ParticipationErrorCode.LIMIT_MAX_MEMBER);
        }
        if (partRepo.existsByGroupIdAndMemberId(groupId, memberId)) {
            throw new ExceptionList(ParticipationErrorCode.ALREADY_MEMBER);
        }
        var p = Participation.builder()
                .group(g)
                .memberId(memberId)
                .joinedAt(LocalDateTime.now())
                .paymentStatus("PENDING")
                .build();
        g.addParticipant(p);
        return partRepo.save(p);
    }

    @Transactional(readOnly = true)
    public List<Participation> listByGroup(Long groupId) {
        return partRepo.findByGroupId(groupId);
    }

    public void leave(Long groupId, Long memberId) {
        Participation p = partRepo.findByGroupId(groupId).stream()
                .filter(x -> x.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ExceptionList(ParticipationErrorCode.NOT_FOUND_PARTICIPATION));
        PurchaseGroup g = p.getGroup();
        g.removeParticipant(p);
        partRepo.delete(p);
    }
}

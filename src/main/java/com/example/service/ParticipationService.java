package com.example.service;

import com.example.dto.request.ParticipationRequestDto;
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

    public Participation join(Long groupId, ParticipationRequestDto dto) {
        PurchaseGroup g = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다: " + groupId));
        if (g.getParticipants().size() >= g.getMaxMembers()) {
            throw new RuntimeException("모집 인원이 가득 찼습니다");
        }
        Participation p = Participation.builder()
                .group(g)
                .memberId(dto.getMemberId())
                .joinedAt(LocalDateTime.now())
                .paymentStatus("PENDING")
                .build();
        g.addParticipant(p);
        return partRepo.save(p);
    }

    public void leave(Long groupId, Long memberId) {
        Participation p = partRepo.findByGroupId(groupId).stream()
                .filter(x -> x.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("참여 내역이 없습니다"));
        PurchaseGroup g = p.getGroup();
        g.removeParticipant(p);
        partRepo.delete(p);
    }

    @Transactional(readOnly = true)
    public List<Participation> listByGroup(Long groupId) {
        return partRepo.findByGroupId(groupId);
    }
}


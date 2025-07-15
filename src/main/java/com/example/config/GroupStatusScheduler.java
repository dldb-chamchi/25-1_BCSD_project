package com.example.config;

import com.example.model.PurchaseGroup;
import com.example.model.PurchaseGroupStatus;
import com.example.repository.PurchaseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupStatusScheduler {

    private final PurchaseGroupRepository groupRepo;

    //매시간, 기한 마감된 OPEN 그룹을 자동으로 CLOSED로 전환
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void closeExpiredGroups() {
        List<PurchaseGroup> expired = groupRepo
                .findByStatusAndExpiresAtBefore(PurchaseGroupStatus.OPEN, LocalDateTime.now());

        expired.forEach(group ->
                group.updateStatus(PurchaseGroupStatus.CLOSED));
        groupRepo.saveAll(expired);
    }
}

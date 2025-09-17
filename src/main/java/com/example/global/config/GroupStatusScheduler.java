package com.example.global.config;

import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupStatus;
import com.example.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupStatusScheduler {

    private final GroupRepository groupRepo;

    //매시간, 기한 마감된 OPEN 그룹을 자동으로 CLOSED로 전환
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void closeExpiredGroups() {
        List<Group> expired = groupRepo
                .findByStatusAndExpiresAtBefore(GroupStatus.OPEN, LocalDateTime.now());

        expired.forEach(group ->
                group.updateStatus(GroupStatus.CLOSED));
        groupRepo.saveAll(expired);
    }
}

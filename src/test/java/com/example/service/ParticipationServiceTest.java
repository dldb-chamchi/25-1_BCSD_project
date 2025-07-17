package com.example.service;

import com.example.dto.response.ParticipationResponseDto;
import com.example.exception.ExceptionList;
import com.example.exception.errorCode.GroupErrorCode;
import com.example.exception.errorCode.ParticipationErrorCode;
import com.example.model.Participation;
import com.example.model.PurchaseGroup;
import com.example.model.PurchaseGroupStatus;
import com.example.repository.ParticipationRepository;
import com.example.repository.PurchaseGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    @Mock
    private ParticipationRepository partRepo;
    @Mock
    private PurchaseGroupRepository groupRepo;
    @InjectMocks
    private ParticipationService participationService;

    private Long groupId;
    private Long hostId;
    private Long memberId;
    private PurchaseGroup group;

    @BeforeEach
    void setUp() {
        groupId = 100L;
        hostId = 200L;
        memberId = 300L;
        group = PurchaseGroup.builder()
                .id(groupId)
                .hostId(hostId)
                .status(PurchaseGroupStatus.OPEN)
                .maxMembers(2)
                .participants(new java.util.ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("join: 성공 - 정상 참여")
    void joinSuccess() {
        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group));
        when(partRepo.existsByGroupIdAndMemberId(groupId, memberId)).thenReturn(false);
        when(partRepo.save(any(Participation.class))).thenAnswer(inv -> {
            Participation orig = inv.getArgument(0);
            return Participation.builder()
                    .id(1L)
                    .group(orig.getGroup())
                    .memberId(orig.getMemberId())
                    .joinedAt(orig.getJoinedAt())
                    .build();
        });

        ParticipationResponseDto result = participationService.join(groupId, memberId);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.memberId()).isEqualTo(memberId);
        assertThat(result.joinedAt()).isNotNull();
    }

    @Test
    @DisplayName("join: 실패 - 그룹 없음 NOT_FOUND_GROUP")
    void joinGroupNotFound() {
        when(groupRepo.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participationService.join(groupId, memberId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList)ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.NOT_FOUND_GROUP));
    }

    @Test
    @DisplayName("join: 실패 - 그룹 마감 시 NOT_PARTICIPATE_GROUP_CLOSED")
    void joinClosedGroup() {
        group.updateStatus(PurchaseGroupStatus.CLOSED);
        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> participationService.join(groupId, memberId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList)ex).getErrorCode())
                        .isEqualTo(ParticipationErrorCode.NOT_PARTICIPATE_GROUP_CLOSED));
    }

    @Test
    @DisplayName("join: 실패 - 인원 초과 LIMIT_MAX_MEMBER")
    void joinLimitExceeded() {
        group.getParticipants().add(
                Participation.builder().memberId(111L).joinedAt(LocalDateTime.now()).build()
        );
        group.getParticipants().add(
                Participation.builder().memberId(222L).joinedAt(LocalDateTime.now()).build()
        );
        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> participationService.join(groupId, memberId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList)ex).getErrorCode())
                        .isEqualTo(ParticipationErrorCode.LIMIT_MAX_MEMBER));
    }

    @Test
    @DisplayName("join: 실패 - 이미 참여 ALREADY_MEMBER")
    void joinAlreadyMember() {
        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group));
        when(partRepo.existsByGroupIdAndMemberId(groupId, memberId)).thenReturn(true);

        assertThatThrownBy(() -> participationService.join(groupId, memberId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList)ex).getErrorCode())
                        .isEqualTo(ParticipationErrorCode.ALREADY_MEMBER));
    }

    @Test
    @DisplayName("leave: 성공 - 정상 탈퇴")
    void leaveSuccess() {
        Participation p = Participation.builder()
                .id(1L)
                .group(group)
                .memberId(memberId)
                .build();
        when(partRepo.findByGroupId(groupId)).thenReturn(List.of(p));

        participationService.leave(groupId, memberId);

        verify(partRepo).delete(p);
    }

    @Test
    @DisplayName("leave: 실패 - 참여 내역 없음 NOT_FOUND_PARTICIPATION")
    void leaveNotFoundThrows() {
        when(partRepo.findByGroupId(groupId)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> participationService.leave(groupId, memberId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList)ex).getErrorCode())
                        .isEqualTo(ParticipationErrorCode.NOT_FOUND_PARTICIPATION));
    }

    @Test
    @DisplayName("leave: 실패 - 호스트는 그룹에서 나갈 수 없음")
    void leaveHostThrows() {
        Participation p = Participation.builder()
                .id(2L)
                .group(group)
                .memberId(hostId)
                .joinedAt(LocalDateTime.now())
                .build();
        when(partRepo.findByGroupId(groupId)).thenReturn(List.of(p));

        assertThatThrownBy(() -> participationService.leave(groupId, hostId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(ex.getMessage())
                        .isEqualTo("호스트는 그룹에서 나갈 수 없습니다"));
    }
}

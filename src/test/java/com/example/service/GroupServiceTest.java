package com.example.service;

import com.example.domain.group.dto.GroupRequestDto;
import com.example.domain.group.service.GroupService;
import com.example.domain.group.dto.GroupStatusRequestDto;
import com.example.domain.group.dto.GroupResponseDto;
import com.example.global.exception.ExceptionList;
import com.example.global.exception.errorCode.GroupErrorCode;
import com.example.domain.participation.model.Participation;
import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupStatus;
import com.example.domain.post.repository.PostRepository;
import com.example.domain.group.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.example.domain.group.model.GroupStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepo;
    @Mock
    private PostRepository postRepo;
    @InjectMocks
    private GroupService groupService;

    private GroupRequestDto dto;
    private Long hostId;
    private Group unsaved;

    @BeforeEach
    void setUp() {
        dto = new GroupRequestDto(
                "Title",
                "Description",
                LocalDateTime.now().plusDays(1),
                5
        );
        hostId = 100L;
        unsaved = dto.toEntity(hostId);
    }

    @Test
    @DisplayName("create: 성공 - 호스트 포함한 신규 그룹 생성")
    void createSuccess() {
        Group saved = Group.builder()
                .id(1L)
                .hostId(hostId)
                .title(dto.title())
                .description(dto.description())
                .expiresAt(dto.expiresAt())
                .maxMembers(dto.maxMember())
                .createdAt(unsaved.getCreatedAt())
                .status(OPEN)
                .participants(Collections.singletonList(
                        Participation.builder()
                                .id(10L)
                                .group(unsaved)
                                .memberId(hostId)
                                .joinedAt(LocalDateTime.now())
                                .build()
                ))
                .build();
        when(groupRepo.save(any(Group.class))).thenReturn(saved);

        GroupResponseDto result = groupService.create(dto, hostId);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo(dto.title());
        assertThat(result.description()).isEqualTo(dto.description());
        assertThat(result.expiresAt()).isEqualTo(dto.expiresAt());
        assertThat(result.maxMember()).isEqualTo(dto.maxMember());
        assertThat(result.status()).isEqualTo(OPEN);
        assertThat(result.participantCount()).isEqualTo(1);
        verify(groupRepo).save(any(Group.class));
    }

    @Test
    @DisplayName("get: 실패 - 존재하지 않는 그룹 조회시 NOT_FOUND_GROUP")
    void getNotFoundThrows() {
        when(groupRepo.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.get(5L))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.NOT_FOUND_GROUP));
    }

    @Test
    @DisplayName("update: 실패 - 호스트가 아닌 경우 HOST_ONLY_GROUP_UPDATE")
    void updateByNonHostThrows() {
        Group g =
                Group.builder()
                .id(2L)
                .hostId(hostId)
                .build();
        when(groupRepo.findById(2L)).thenReturn(Optional.of(g));

        assertThatThrownBy(() -> groupService.update(2L, hostId + 1, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.HOST_ONLY_GROUP_UPDATE));
    }

    @Test
    @DisplayName("changeStatus: 실패 - 호스트가 아닌 경우 HOST_ONLY_GROUP_UPDATE")
    void changeStatusByNonHostThrows() {
        Group g = Group.builder()
                .id(3L)
                .hostId(hostId)
                .status(OPEN)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .maxMembers(5)
                .participants(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();
        when(groupRepo.findById(3L)).thenReturn(Optional.of(g));

        GroupStatusRequestDto dto = new GroupStatusRequestDto(GroupStatus.CLOSED);
        assertThatThrownBy(() -> groupService.changeStatus(3L, hostId + 1, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.HOST_ONLY_GROUP_UPDATE));
    }

    @Test
    @DisplayName("delete: 실패 - 참여자가 남아있을 때 NOT_DELETE_WITH_PARTICIPATION")
    void deleteWithParticipantsThrows() {
        Group g = unsaved;
        g.getParticipants().add(
                Participation.builder().id(1L).group(g).memberId(hostId).joinedAt(LocalDateTime.now()).build()
        );
        g.getParticipants().add(
                Participation.builder().id(2L).group(g).memberId(hostId + 1).joinedAt(LocalDateTime.now()).build()
        );
        when(groupRepo.findById(4L)).thenReturn(Optional.of(g));

        assertThatThrownBy(() -> groupService.delete(4L, hostId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.NOT_DELETE_WITH_PARTICIPATION));
    }

    @Test
    @DisplayName("delete: 실패 - 게시글 존재 시 NOT_DELETE_WITH_POST")
    void deleteWithPostsThrows() {
        Group g = unsaved;
        when(groupRepo.findById(6L)).thenReturn(Optional.of(g));
        when(postRepo.existsByGroupId(6L)).thenReturn(true);

        assertThatThrownBy(() -> groupService.delete(6L, hostId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.NOT_DELETE_WITH_POST));
    }

    @Test
    @DisplayName("changeStatus: 실패 - 만료된 그룹은 OPEN으로 다시 열 수 없음")
    void changeStatusExpiredCannotReopen() {
        Group expired = Group.builder()
                .id(7L)
                .hostId(hostId)
                .status(GroupStatus.CLOSED)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .maxMembers(10)
                .participants(Collections.emptyList())
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();
        when(groupRepo.findById(7L)).thenReturn(Optional.of(expired));

        GroupStatusRequestDto dto = new GroupStatusRequestDto(OPEN);
        assertThatThrownBy(() -> groupService.changeStatus(7L, hostId, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.NOT_VALID_STATUS));
    }
}

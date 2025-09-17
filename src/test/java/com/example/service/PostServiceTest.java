package com.example.service;

import com.example.domain.post.dto.PostRequestDto;
import com.example.domain.post.dto.PostResponseDto;
import com.example.domain.post.service.PostService;
import com.example.global.exception.ExceptionList;
import com.example.global.exception.errorCode.GroupErrorCode;
import com.example.global.exception.errorCode.PathErrorCode;
import com.example.global.exception.errorCode.PostErrorCode;
import com.example.domain.post.model.Post;
import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupStatus;
import com.example.domain.post.repository.PostRepository;
import com.example.domain.comment.repository.CommentRepository;
import com.example.domain.group.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepo;
    @Mock
    private GroupRepository groupRepo;
    @Mock
    private CommentRepository commentRepo;
    @InjectMocks
    private PostService postService;

    private Long groupId;
    private Long hostId;
    private PostRequestDto dto;
    private Group group;

    @BeforeEach
    void setUp() {
        groupId = 10L;
        hostId = 20L;
        dto = new PostRequestDto("Title", "Content");
        group = Group.builder()
                .id(groupId)
                .hostId(hostId)
                .title("Group Title")
                .description("Description")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .maxMembers(5)
                .createdAt(LocalDateTime.now())
                .status(GroupStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("create: 성공 - 게시글 생성 후 DTO 반환")
    void createSuccess() {
        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group));
        when(postRepo.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        PostResponseDto result = postService.create(groupId, hostId, dto);

        assertThat(result.hostId()).isEqualTo(hostId);
        assertThat(result.title()).isEqualTo(dto.title());
        verify(postRepo).save(any(Post.class));
    }

    @Test
    @DisplayName("create: 실패 - 그룹 없음")
    void createGroupNotFoundThrows() {
        when(groupRepo.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.create(groupId, hostId, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.NOT_FOUND_GROUP));
    }

    @Test
    @DisplayName("create: 실패 - 호스트가 아닐 때")
    void createByNonHostThrows() {
        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> postService.create(groupId, hostId + 1, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(PostErrorCode.HOST_ONLY_POST_UPLOAD));
    }

    @Test
    @DisplayName("getById: 실패 - 그룹 없음")
    void getByIdGroupNotFoundThrows() {
        when(groupRepo.existsById(groupId)).thenReturn(false);

        assertThatThrownBy(() -> postService.getById(groupId, 42L))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(GroupErrorCode.NOT_FOUND_GROUP));
    }

    @Test
    @DisplayName("getById: 실패 - 게시글 없음")
    void getByIdNotFoundThrows() {
        when(groupRepo.existsById(groupId)).thenReturn(true);
        when(postRepo.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getById(groupId, 42L))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(PostErrorCode.NOT_FOUND_POST));
    }

    @Test
    @DisplayName("getById: 실패 - 그룹 경로 불일치")
    void getByIdInvalidGroupThrows() {
        when(groupRepo.existsById(groupId)).thenReturn(true);
        Group other = Group.builder().id(groupId + 1).build();
        Post post = Post.builder().id(50L).group(other).hostId(hostId).build();
        when(postRepo.findById(50L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.getById(groupId, 50L))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(PathErrorCode.NOT_VALID_PATH));
    }

    @Test
    @DisplayName("update: 실패 - 호스트가 아닐 때")
    void updateByNonHostThrows() {
        Post post = Post.builder().id(2L).group(group).hostId(hostId).build();
        when(postRepo.findById(2L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.update(groupId, 2L, hostId + 1, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(PostErrorCode.HOST_ONLY_POST_UPDATE));
    }

    @Test
    @DisplayName("delete: 실패 - 댓글이 있을 때")
    void deleteWithCommentsThrows() {
        Post post = Post.builder().id(3L).group(group).hostId(hostId).build();
        when(postRepo.findById(3L)).thenReturn(Optional.of(post));
        when(commentRepo.countByPostId(3L)).thenReturn(1L);

        assertThatThrownBy(() -> postService.delete(groupId, 3L, hostId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(PostErrorCode.NOT_DELETE_WITH_COMMENT));
    }

    @Test
    @DisplayName("delete: 성공 - 댓글 없을 때 삭제")
    void deleteSuccess() {
        Post post = Post.builder().id(4L).group(group).hostId(hostId).build();
        when(postRepo.findById(4L)).thenReturn(Optional.of(post));
        when(commentRepo.countByPostId(4L)).thenReturn(0L);

        postService.delete(groupId, 4L, hostId);

        verify(postRepo).delete(post);
    }
}

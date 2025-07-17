package com.example.service;

import com.example.dto.request.CommentRequestDto;
import com.example.dto.response.CommentResponseDto;
import com.example.exception.ExceptionList;
import com.example.exception.errorCode.CommentErrorCode;
import com.example.exception.errorCode.PathErrorCode;
import com.example.exception.errorCode.PostErrorCode;
import com.example.model.GroupPost;
import com.example.model.PostComment;
import com.example.model.PurchaseGroup;
import com.example.repository.GroupPostRepository;
import com.example.repository.ParticipationRepository;
import com.example.repository.PostCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommentServiceTest {

    @Mock private PostCommentRepository commentRepo;
    @Mock private GroupPostRepository postRepo;
    @Mock private ParticipationRepository partRepo;
    @InjectMocks private PostCommentService commentService;

    private Long postId;
    private Long memberId;
    private PurchaseGroup group;
    private GroupPost post;
    private CommentRequestDto dto;

    @BeforeEach
    void setUp() {
        postId = 1L;
        memberId = 2L;
        dto = new CommentRequestDto("Hello");
        group = PurchaseGroup.builder().id(10L).build();
        post = GroupPost.builder().id(postId).group(group).build();
    }

    @Test
    @DisplayName("create: 성공 - 그룹 참여자 댓글 작성")
    void createSuccess() {
        when(postRepo.findById(postId)).thenReturn(Optional.of(post));
        when(partRepo.existsByGroupIdAndMemberId(group.getId(), memberId)).thenReturn(true);
        when(commentRepo.save(any(PostComment.class))).thenAnswer(inv -> {
            PostComment orig = inv.getArgument(0);
            // 빌더를 사용해 새 인스턴스를 생성하여 id, createdAt 필드를 설정
            return PostComment.builder()
                    .id(1L)
                    .post(orig.getPost())
                    .memberId(orig.getMemberId())
                    .content(orig.getContent())
                    .createdAt(LocalDateTime.now())
                    .build();
        });

        CommentResponseDto result = commentService.create(postId, memberId, dto);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.memberId()).isEqualTo(memberId);
        assertThat(result.content()).isEqualTo("Hello");
        assertThat(result.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("create: 실패 - 게시글 없음 NOT_FOUND_POST")
    void createNotFoundPostThrows() {
        when(postRepo.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(postId, memberId, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(PostErrorCode.NOT_FOUND_POST));
    }

    @Test
    @DisplayName("update: 실패 - 댓글 없음 NOT_FOUND_COMMENT")
    void updateNotFoundThrows() {
        when(commentRepo.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.update(group.getId(), postId, 5L, memberId, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(CommentErrorCode.NOT_FOUND_COMMENT));
    }

    @Test
    @DisplayName("update: 실패 - 경로 불일치 NOT_VALID_PATH")
    void updateInvalidPathThrows() {
        PostComment comment = PostComment.builder()
                .id(1L)
                .post(post)
                .memberId(memberId)
                .content("x")
                .build();
        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.update(group.getId() + 1, postId, 1L, memberId, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(PathErrorCode.NOT_VALID_PATH));
    }

    @Test
    @DisplayName("update: 실패 - 작성자 아니면 ONLY_WRITER_MEMBER_UPDATE")
    void updateByNonWriterThrows() {
        PostComment comment = PostComment.builder()
                .id(2L)
                .post(post)
                .memberId(memberId)
                .content("x")
                .build();
        when(commentRepo.findById(2L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.update(group.getId(), postId, 2L, memberId + 1, dto))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(CommentErrorCode.ONLY_WRITER_MEMBER_UPDATE));
    }

    @Test
    @DisplayName("delete: 실패 - 댓글 없음 NOT_FOUND_COMMENT")
    void deleteNotFoundThrows() {
        when(commentRepo.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.delete(group.getId(), postId, 3L, memberId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(CommentErrorCode.NOT_FOUND_COMMENT));
    }

    @Test
    @DisplayName("delete: 실패 - 경로 불일치 NOT_VALID_PATH")
    void deleteInvalidPathThrows() {
        PostComment comment = PostComment.builder()
                .id(4L)
                .post(post)
                .memberId(memberId)
                .content("x")
                .build();
        when(commentRepo.findById(4L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.delete(group.getId() + 1, postId, 4L, memberId))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(PathErrorCode.NOT_VALID_PATH));
    }

    @Test
    @DisplayName("delete: 실패 - 작성자 아니면 ONLY_WRITER_MEMBER_DELETE")
    void deleteByNonWriterThrows() {
        PostComment comment = PostComment.builder()
                .id(5L)
                .post(post)
                .memberId(memberId)
                .content("x")
                .build();
        when(commentRepo.findById(5L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.delete(group.getId(), postId, 5L, memberId + 1))
                .isInstanceOf(ExceptionList.class)
                .satisfies(ex -> assertThat(((ExceptionList) ex).getErrorCode())
                        .isEqualTo(CommentErrorCode.ONLY_WRITER_MEMBER_DELETE));
    }

    @Test
    @DisplayName("delete: 성공 - 댓글 삭제")
    void deleteSuccess() {
        PostComment comment = PostComment.builder()
                .id(6L)
                .post(post)
                .memberId(memberId)
                .content("x")
                .build();
        when(commentRepo.findById(6L)).thenReturn(Optional.of(comment));

        commentService.delete(group.getId(), postId, 6L, memberId);

        verify(commentRepo).delete(comment);
    }
}

package com.example.service;

import com.example.dto.request.PostRequestDto;
import com.example.dto.response.PostResponseDto;
import com.example.exception.ExceptionList;
import com.example.exception.errorCode.GroupErrorCode;
import com.example.exception.errorCode.PathErrorCode;
import com.example.exception.errorCode.PostErrorCode;
import com.example.model.GroupPost;
import com.example.model.PurchaseGroup;
import com.example.repository.GroupPostRepository;
import com.example.repository.PostCommentRepository;
import com.example.repository.PurchaseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupPostService {
    private final GroupPostRepository postRepo;
    private final PurchaseGroupRepository groupRepo;
    private final PostCommentRepository commentRepo;

    public PostResponseDto create(Long groupId, Long hostId, PostRequestDto dto) {
        PurchaseGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

        if (!group.getHostId().equals(hostId))
            throw new ExceptionList(PostErrorCode.HOST_ONLY_POST_UPLOAD);

        GroupPost post = dto.toEntity(group, hostId);
        return PostResponseDto.fromEntity(postRepo.save(post));
    }

    @Transactional(readOnly = true)
    public PostResponseDto getById(Long groupId, Long postId) {
        if (!groupRepo.existsById(groupId)) {
            throw new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP);
        }

        GroupPost post = postRepo.findById(postId)
                .orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new ExceptionList(PathErrorCode.NOT_VALID_PATH);
        }
        return PostResponseDto.fromEntity(post);
    }

    @Transactional(readOnly=true)
    public Page<PostResponseDto> list(Long groupId, Pageable pageable) {
        if (!groupRepo.existsById(groupId)) {
            throw new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP);
        }
        return postRepo.findByGroupId(groupId, pageable)
                .map(PostResponseDto::fromEntity);
    }

    public PostResponseDto update(Long groupId, Long postId, Long hostId, PostRequestDto dto) {
        GroupPost post = postRepo.findById(postId).orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new ExceptionList(PathErrorCode.NOT_VALID_PATH);
        }
        if (!post.getHostId().equals(hostId)) {
            throw new ExceptionList(PostErrorCode.HOST_ONLY_POST_UPDATE);
        }

        post.update(dto.title(), dto.content());
        return PostResponseDto.fromEntity(post);
    }

    public void delete(Long groupId, Long postId, Long hostId) {
        GroupPost post = postRepo.findById(postId)
                .orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new ExceptionList(PathErrorCode.NOT_VALID_PATH);
        }
        if (!post.getHostId().equals(hostId)) {
            throw new ExceptionList(PostErrorCode.HOST_ONLY_POST_DELETE);
        }

        long commentCount = commentRepo.countByPostId(postId);
        if (commentCount > 0) {
            throw new ExceptionList(PostErrorCode.NOT_DELETE_WITH_COMMENT);
        }
        postRepo.delete(post);
    }
}

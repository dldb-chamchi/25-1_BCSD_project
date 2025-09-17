package com.example.domain.post.service;

import com.example.domain.post.dto.PostRequestDto;
import com.example.domain.post.dto.PostResponseDto;
import com.example.global.exception.ExceptionList;
import com.example.global.exception.errorCode.GroupErrorCode;
import com.example.global.exception.errorCode.PathErrorCode;
import com.example.global.exception.errorCode.PostErrorCode;
import com.example.domain.post.model.Post;
import com.example.domain.group.model.Group;
import com.example.domain.post.repository.PostRepository;
import com.example.domain.comment.repository.CommentRepository;
import com.example.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepo;
    private final GroupRepository groupRepo;
    private final CommentRepository commentRepo;

    @Transactional
    public PostResponseDto create(Long groupId, Long hostId, PostRequestDto dto) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP));

        if (!group.getHostId().equals(hostId))
            throw new ExceptionList(PostErrorCode.HOST_ONLY_POST_UPLOAD);

        Post post = dto.toEntity(group, hostId);
        return PostResponseDto.fromEntity(postRepo.save(post));
    }

    public PostResponseDto getById(Long groupId, Long postId) {
        if (!groupRepo.existsById(groupId)) {
            throw new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP);
        }

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new ExceptionList(PathErrorCode.NOT_VALID_PATH);
        }
        return PostResponseDto.fromEntity(post);
    }

    public Page<PostResponseDto> list(Long groupId, Pageable pageable) {
        if (!groupRepo.existsById(groupId)) {
            throw new ExceptionList(GroupErrorCode.NOT_FOUND_GROUP);
        }
        return postRepo.findByGroupId(groupId, pageable)
                .map(PostResponseDto::fromEntity);
    }

    @Transactional
    public PostResponseDto update(Long groupId, Long postId, Long hostId, PostRequestDto dto) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new ExceptionList(PathErrorCode.NOT_VALID_PATH);
        }
        if (!post.getHostId().equals(hostId)) {
            throw new ExceptionList(PostErrorCode.HOST_ONLY_POST_UPDATE);
        }

        post.update(dto.title(), dto.content());
        return PostResponseDto.fromEntity(post);
    }

    @Transactional
    public void delete(Long groupId, Long postId, Long hostId) {
        Post post = postRepo.findById(postId)
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

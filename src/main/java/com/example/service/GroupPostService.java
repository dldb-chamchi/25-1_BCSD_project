package com.example.service;

import com.example.dto.request.PostRequestDto;
import com.example.exception.BadRequestException;
import com.example.exception.ExceptionList;
import com.example.exception.PostErrorCode;
import com.example.model.GroupPost;
import com.example.model.PurchaseGroup;
import com.example.repository.GroupPostRepository;
import com.example.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupPostService {
    private final GroupPostRepository postRepo;
    private final PurchaseGroupService groupService;
    private final PostCommentRepository commentRepo;

    public GroupPost create(Long groupId, Long hostId, PostRequestDto dto) {
        PurchaseGroup g = groupService.get(groupId);

        if (!g.getHostId().equals(hostId))
            throw new ExceptionList(PostErrorCode.HOST_ONLY_POST_UPLOAD);

        GroupPost p = dto.toEntity(g, hostId);
        return postRepo.save(p);
    }

    @Transactional(readOnly = true)
    public GroupPost getById(Long groupId, Long postId) {
        groupService.get(groupId);
        GroupPost post = postRepo.findById(postId)
                .orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 그룹 경로입니다");
        }
        return post;
    }

    @Transactional(readOnly=true)
    public List<GroupPost> list(Long groupId, Pageable pageable) {
        groupService.get(groupId);
        return postRepo.findByGroupId(groupId, pageable);
    }

    @Transactional(readOnly=true)
    public List<GroupPost> listByHost(Long hostId, Pageable pageable) {
        return postRepo.findByHostId(hostId, pageable);
    }

    public GroupPost update(Long groupId, Long postId, Long hostId, PostRequestDto dto) {
        var post = postRepo.findById(postId).orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));
        if (!post.getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 그룹 경로입니다");
        }
        if (!post.getHostId().equals(hostId)) {
            throw new ExceptionList(PostErrorCode.HOST_ONLY_POST_UPDATE);
        }
        post.update(dto.title(), dto.content());
        return post;
    }

    public void delete(Long groupId, Long postId, Long hostId) {
        GroupPost post = postRepo.findById(postId)
                .orElseThrow(() -> new ExceptionList(PostErrorCode.NOT_FOUND_POST));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 그룹 경로입니다");
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

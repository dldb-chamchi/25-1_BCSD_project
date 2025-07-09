package com.example.service;

import com.example.dto.request.PostRequestDto;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.GroupPost;
import com.example.model.PurchaseGroup;
import com.example.repository.GroupPostRepository;
import com.example.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
            throw new BadRequestException("호스트만 게시글을 작성할 수 있습니다");
        GroupPost p = GroupPost.builder()
                .group(g)
                .hostId(hostId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
        return postRepo.save(p);
    }

    public GroupPost update(Long groupId, Long postId, Long hostId, PostRequestDto dto) {
        var post = postRepo.findById(postId).orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        if (!post.getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 그룹 경로입니다");
        }
        if (!post.getHostId().equals(hostId)) {
            throw new AccessDeniedException("호스트만 게시글을 수정할 수 있습니다");
        }
        post.update(dto.getTitle(), dto.getContent());
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

    public void delete(Long groupId, Long postId, Long hostId) {
        GroupPost post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (!post.getGroup().getId().equals(groupId)) {
            throw new BadRequestException("잘못된 그룹 경로입니다");
        }
        if (!post.getHostId().equals(hostId)) {
            throw new AccessDeniedException("호스트만 삭제할 수 있습니다");
        }
        long commentCount = commentRepo.countByPostId(postId);
        if (commentCount > 0) {
            throw new BadRequestException("이미 댓글이 달린 게시글은 삭제할 수 없습니다");
        }
        postRepo.delete(post);
    }
}

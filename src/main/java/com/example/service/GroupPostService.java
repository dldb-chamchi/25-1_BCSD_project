package com.example.service;

import com.example.dto.request.PostRequestDto;
import com.example.model.GroupPost;
import com.example.model.PurchaseGroup;
import com.example.repository.GroupPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupPostService {
    private final GroupPostRepository postRepo;
    private final PurchaseGroupService groupService;

    public GroupPost create(Long groupId, Long hostId, PostRequestDto dto) {
        PurchaseGroup g = groupService.get(groupId);
        if (!g.getHostId().equals(hostId))
            throw new RuntimeException("호스트만 게시글을 작성할 수 있습니다");
        GroupPost p = GroupPost.builder()
                .group(g)
                .hostId(hostId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
        return postRepo.save(p);
    }

    @Transactional(readOnly=true)
    public List<GroupPost> list(Long groupId) {
        return postRepo.findByGroupId(groupId);
    }

    @Transactional(readOnly=true)
    public List<GroupPost> listByHost(Long hostId) {
        return postRepo.findByHostId(hostId);
    }
}

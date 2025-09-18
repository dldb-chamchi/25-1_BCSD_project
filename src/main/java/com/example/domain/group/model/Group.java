package com.example.domain.group.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.domain.participation.model.Participation;

@Entity
@Table(name = "`group`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "max_member", nullable = false)
    private Integer maxMember;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupStatus status;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)

    @Builder.Default
    private List<Participation> participants = new ArrayList<>();

    public void update(String newTitle, String newDescription, Integer newMaxMember, LocalDateTime newExpiresAt) {
            this.title = newTitle;
            this.description = newDescription;
            this.maxMember = newMaxMember;
            this.expiresAt = newExpiresAt;
    }

    public void updateStatus(GroupStatus newStatus) {
        this.status = newStatus;
    }

    public void addParticipant(Participation p) {
        participants.add(p);
    }

    public void removeParticipant(Participation p) {
        participants.remove(p);
    }
}

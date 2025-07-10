package com.example.model;

import com.example.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PurchaseGroup {
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

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)

    @Builder.Default
    private List<Participation> participants = new ArrayList<>();

    public void update(String newTitle, String newDescription, Integer newMaxMembers, LocalDateTime newExpiresAt) {
            this.title = newTitle;
            this.description = newDescription;
            this.maxMembers = newMaxMembers;
            this.expiresAt = newExpiresAt;
    }

    public void updateStatus(String newStatus) {
        if (!List.of("OPEN","CLOSED").contains(newStatus)) {
            throw new BadRequestException("유효한 상태가 아닙니다");
        }
        this.status = newStatus;
    }

    public void addParticipant(Participation p) {
        participants.add(p);
    }

    public void removeParticipant(Participation p) {
        participants.remove(p);
    }
}

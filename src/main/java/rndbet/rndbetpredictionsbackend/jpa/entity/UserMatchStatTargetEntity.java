package rndbet.rndbetpredictionsbackend.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_match_stat_targets")
@Getter
@Setter
@NoArgsConstructor
public class UserMatchStatTargetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "match_id", nullable = false)
    private Integer matchId;

    @Column(nullable = false, length = 8)
    private String scope;

    @Column(nullable = false, length = 32)
    private String stat;

    @Column(nullable = false)
    private Integer threshold;

    @Column(nullable = false, length = 16)
    private String state;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "fulfilled_at")
    private OffsetDateTime fulfilledAt;

    @Column(name = "fulfilled_match_minute")
    private Integer fulfilledMatchMinute;

    @Column(name = "failed_at")
    private OffsetDateTime failedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (state == null) {
            state = "PENDING";
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}

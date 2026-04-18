package rndbet.rndbetpredictionsbackend.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "match_events")
@Getter
@Setter
@NoArgsConstructor
public class MatchEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "match_id", nullable = false)
    private Integer matchId;

    @Column(name = "team_id")
    private Integer teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    private Integer minute;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "extra_data")
    private String extraDataJson;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}

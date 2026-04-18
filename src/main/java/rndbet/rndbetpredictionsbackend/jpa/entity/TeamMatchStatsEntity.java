package rndbet.rndbetpredictionsbackend.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "team_match_stats")
@Getter
@Setter
@NoArgsConstructor
public class TeamMatchStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "match_id", nullable = false)
    private Integer matchId;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Column(name = "is_home")
    private Boolean isHome;

    private Integer goals;

    private BigDecimal possession;

    private Integer shots;

    @Column(name = "shots_on_target")
    private Integer shotsOnTarget;

    private Integer saves;

    @Column(name = "yellow_cards")
    private Integer yellowCards;

    @Column(name = "red_cards")
    private Integer redCards;

    private Integer corners;

    private Integer fouls;

    private Integer offsides;
}

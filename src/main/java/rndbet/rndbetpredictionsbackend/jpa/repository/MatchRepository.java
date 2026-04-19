package rndbet.rndbetpredictionsbackend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEntity;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<MatchEntity, Integer> {

    @Query(
            """
                    SELECT COUNT(m) FROM MatchEntity m
                    WHERE m.id = :matchId AND m.season.id = :seasonId AND m.round = :round
                      AND m.season.competitionId = :competitionId
                    """)
    long countInSeasonRoundContext(
            @Param("matchId") int matchId,
            @Param("seasonId") int seasonId,
            @Param("round") int round,
            @Param("competitionId") int competitionId);

    @Query(
            """
                    SELECT m FROM MatchEntity m
                    JOIN FETCH m.homeTeam JOIN FETCH m.awayTeam JOIN FETCH m.season s
                    WHERE m.id = :matchId AND m.season.id = :seasonId AND m.round = :round
                      AND s.competitionId = :competitionId
                    """)
    Optional<MatchEntity> findForDetailHeader(
            @Param("matchId") int matchId,
            @Param("seasonId") int seasonId,
            @Param("round") int round,
            @Param("competitionId") int competitionId);

    @Query(
            """
                    SELECT m FROM MatchEntity m
                    JOIN FETCH m.homeTeam JOIN FETCH m.awayTeam
                    WHERE m.season.id = :seasonId AND m.round = :round
                    ORDER BY m.date ASC NULLS LAST, m.id ASC
                    """)
    List<MatchEntity> findBySeasonAndRoundWithTeams(
            @Param("seasonId") int seasonId, @Param("round") int round);

    @Query(
            """
                    SELECT m FROM MatchEntity m
                    JOIN FETCH m.homeTeam JOIN FETCH m.awayTeam
                    WHERE m.season.id = :seasonId
                      AND m.homeScore IS NOT NULL AND m.awayScore IS NOT NULL
                    ORDER BY m.date ASC NULLS LAST, m.id ASC
                    """)
    List<MatchEntity> findFinishedBySeasonWithTeams(@Param("seasonId") int seasonId);

    List<MatchEntity> findBySeason_Id(int seasonId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
                    UPDATE MatchEntity m SET m.liveTrackEnqueued = true, m.liveTrackEnqueuedAt = :ts
                    WHERE m.id IN :ids
                    """)
    int markLiveTrackEnqueued(@Param("ids") Collection<Integer> ids, @Param("ts") OffsetDateTime ts);
}

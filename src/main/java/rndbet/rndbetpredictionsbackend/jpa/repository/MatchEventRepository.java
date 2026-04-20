package rndbet.rndbetpredictionsbackend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEventEntity;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEventEntity, Integer> {

    @Query(
            """
                    SELECT me FROM MatchEventEntity me LEFT JOIN FETCH me.player
                    WHERE me.matchId = :matchId
                    ORDER BY me.minute ASC NULLS LAST, me.id ASC
                    """)
    List<MatchEventEntity> findAllForMatchWithPlayer(@Param("matchId") Integer matchId);

    @Query(
            value =
                    """
                            SELECT EXISTS (
                                SELECT 1 FROM match_events me
                                WHERE me.match_id = :matchId
                                  AND me.event_type = :eventType
                                  AND me.minute IS NOT DISTINCT FROM :minute
                                  AND me.team_id IS NOT DISTINCT FROM :teamId
                                  AND LOWER(TRIM(COALESCE(me.extra_data::jsonb->>'player_name', '')))
                                      = LOWER(TRIM(COALESCE(:playerName, '')))
                            )
                            """,
            nativeQuery = true)
    boolean existsDuplicateLiveEvent(
            @Param("matchId") int matchId,
            @Param("minute") Integer minute,
            @Param("eventType") String eventType,
            @Param("teamId") Integer teamId,
            @Param("playerName") String playerName);
}

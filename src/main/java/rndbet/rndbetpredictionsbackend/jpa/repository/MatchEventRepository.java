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
}

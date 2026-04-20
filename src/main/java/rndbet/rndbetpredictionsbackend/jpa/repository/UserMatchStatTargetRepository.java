package rndbet.rndbetpredictionsbackend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rndbet.rndbetpredictionsbackend.jpa.entity.UserMatchStatTargetEntity;

import java.util.List;
import java.util.Optional;

public interface UserMatchStatTargetRepository extends JpaRepository<UserMatchStatTargetEntity, Long> {

    List<UserMatchStatTargetEntity> findByUserIdAndMatchIdOrderByIdAsc(long userId, int matchId);

    List<UserMatchStatTargetEntity> findByMatchIdAndStateOrderByIdAsc(int matchId, String state);

    Optional<UserMatchStatTargetEntity> findByIdAndUserId(long id, long userId);

    @Modifying(clearAutomatically = true)
    @Query(
            """
                    DELETE FROM UserMatchStatTargetEntity t
                    WHERE t.id = :id AND t.userId = :userId AND t.matchId = :matchId
                    """)
    int deleteByIdAndUserIdAndMatchId(
            @Param("id") long id, @Param("userId") long userId, @Param("matchId") int matchId);
}

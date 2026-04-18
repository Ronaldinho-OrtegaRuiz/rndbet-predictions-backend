package rndbet.rndbetpredictionsbackend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rndbet.rndbetpredictionsbackend.jpa.entity.TeamMatchStatsEntity;

import java.util.Collection;
import java.util.List;

public interface TeamMatchStatsRepository extends JpaRepository<TeamMatchStatsEntity, Integer> {

    List<TeamMatchStatsEntity> findByMatchId(Integer matchId);

    List<TeamMatchStatsEntity> findByMatchIdIn(Collection<Integer> matchIds);
}

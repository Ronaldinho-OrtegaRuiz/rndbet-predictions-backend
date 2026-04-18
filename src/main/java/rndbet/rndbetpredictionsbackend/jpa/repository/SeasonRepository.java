package rndbet.rndbetpredictionsbackend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rndbet.rndbetpredictionsbackend.jpa.entity.SeasonEntity;

public interface SeasonRepository extends JpaRepository<SeasonEntity, Integer> {

    boolean existsByIdAndCompetitionId(Integer id, Integer competitionId);
}

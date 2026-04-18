package rndbet.rndbetpredictionsbackend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rndbet.rndbetpredictionsbackend.jpa.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
}

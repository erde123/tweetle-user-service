package nl.fontys.tweetleuserservice.persistence.repository;

import nl.fontys.tweetleuserservice.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByAuth0Id(String auth0Id);
    Optional<UserEntity> findByUsername(String username);
}

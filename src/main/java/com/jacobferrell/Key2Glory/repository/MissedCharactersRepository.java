package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.MissedCharacters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissedCharactersRepository extends JpaRepository<MissedCharacters, Long> {
    //@Query("SELECT m FROM MissedCharacters m WHERE m.username = :username")
    Optional<MissedCharacters> findByUsername(String username);
}

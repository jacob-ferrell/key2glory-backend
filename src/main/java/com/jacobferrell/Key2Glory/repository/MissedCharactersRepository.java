package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.MissedCharacters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissedCharactersRepository extends JpaRepository<MissedCharacters, Long> {
    // findFirst: concurrent first completions could have created more than one row per user
    Optional<MissedCharacters> findFirstByUsernameOrderByIdAsc(String username);
}

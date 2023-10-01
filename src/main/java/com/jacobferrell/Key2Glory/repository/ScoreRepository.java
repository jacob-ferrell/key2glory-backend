package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.Score;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    @Query("SELECT s FROM Score s WHERE s.typingTest.id = :id AND s.username = :username")
    List<Score> findByTestAndUser(Long id, String username);

    @Query("SELECT s FROM Score s WHERE s.typingTest.id = :id ORDER BY s.wpm DESC")
    List<Score> findByTestOrderByWpmDesc(Long id);

    @Query("SELECT s FROM Score s " +
            "WHERE s.typingTest.id = :id " +
            "ORDER BY s.wpm DESC")
    Optional<List<Score>> findHighScores(Long id, Pageable pageable);
}

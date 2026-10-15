package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.dto.LeaderboardEntryDTO;
import com.jacobferrell.Key2Glory.model.Score;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    @Query("SELECT s FROM Score s WHERE s.typingTest.id = :id AND s.username = :username ORDER BY s.id DESC")
    List<Score> findByTestAndUser(Long id, String username);

    List<Score> findByUsernameOrderByIdDesc(String username, Pageable pageable);

    long countByUsername(String username);

    Optional<Score> findFirstByTypingTestIdOrderByOverallScoreDesc(Long typingTestId);

    Optional<Score> findFirstByTypingTestIdAndUsernameOrderByOverallScoreDesc(Long typingTestId, String username);

    /** Each user's best score on a test, best first. Exact ties for a user's best can produce duplicates. */
    @Query("SELECT s FROM Score s WHERE s.typingTest.id = :id AND s.username IS NOT NULL AND s.overallScore = " +
            "(SELECT MAX(s2.overallScore) FROM Score s2 WHERE s2.typingTest.id = :id AND s2.username = s.username) " +
            "ORDER BY s.overallScore DESC, s.id ASC")
    List<Score> findBestScoresPerUser(Long id, Pageable pageable);

    @Query("SELECT new com.jacobferrell.Key2Glory.dto.LeaderboardEntryDTO(" +
            "s.username, AVG(s.wpm), MAX(s.wpm), AVG(s.accuracy), COUNT(s)) " +
            "FROM Score s WHERE s.username IS NOT NULL " +
            "GROUP BY s.username HAVING COUNT(s) >= :minTests " +
            "ORDER BY AVG(s.wpm) DESC")
    List<LeaderboardEntryDTO> findLeaderboard(long minTests, Pageable pageable);

    @Query("SELECT AVG(s.wpm) FROM Score s WHERE s.username = :username")
    Double findAverageWpmByUsername(String username);

    @Query("SELECT MAX(s.wpm) FROM Score s WHERE s.username = :username")
    Double findMaxWpmByUsername(String username);

    @Query("SELECT AVG(s.accuracy) FROM Score s WHERE s.username = :username")
    Double findAverageAccuracyByUsername(String username);
}

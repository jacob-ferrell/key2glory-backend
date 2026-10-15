package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.TestRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRatingRepository extends JpaRepository<TestRating, Long> {
    Optional<TestRating> findFirstByTypingTestIdAndUsername(Long typingTestId, String username);

    @Query("SELECT AVG(r.rating) FROM TestRating r WHERE r.typingTest.id = :id")
    Double findAverageRating(Long id);
}

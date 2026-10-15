package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.model.TypingTestType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypingTestRepository extends JpaRepository<TypingTest, Long>, JpaSpecificationExecutor<TypingTest> {
    long countByCreatedBy(String username);

    // Atomic so concurrent completions by different users don't lose increments
    @Modifying
    @Query("UPDATE TypingTest t SET t.scoresCount = COALESCE(t.scoresCount, 0) + 1 WHERE t.id = :id")
    void incrementScoresCount(Long id);

    boolean existsByText(String text);

    @Query("SELECT COUNT(t) FROM TypingTest t WHERE t.type = :type AND t.id <> :excludeId")
    long countByTypeExcluding(TypingTestType type, Long excludeId);

    @Query("SELECT t FROM TypingTest t WHERE t.type = :type AND t.id <> :excludeId ORDER BY t.id")
    List<TypingTest> findByTypeExcluding(TypingTestType type, Long excludeId, Pageable pageable);
}

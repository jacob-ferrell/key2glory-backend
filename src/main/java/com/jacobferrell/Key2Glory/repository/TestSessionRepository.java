package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.TestSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
    Optional<TestSession> findFirstByTypingTestIdAndUsernameOrderByIdDesc(Long typingTestId, String username);

    // Row lock so two simultaneous completion requests can't both score the same run
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TestSession> findFirstForUpdateByTypingTestIdAndUsernameOrderByIdDesc(Long typingTestId, String username);
}

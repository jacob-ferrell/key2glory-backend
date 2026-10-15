package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
    Optional<TestSession> findFirstByTypingTestIdAndUsernameOrderByIdDesc(Long typingTestId, String username);
}

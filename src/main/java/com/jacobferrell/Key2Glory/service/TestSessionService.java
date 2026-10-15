package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.dto.TestSessionDTO;
import com.jacobferrell.Key2Glory.model.*;
import com.jacobferrell.Key2Glory.repository.MissedCharactersRepository;
import com.jacobferrell.Key2Glory.repository.ScoreRepository;
import com.jacobferrell.Key2Glory.repository.TestSessionRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TestSessionService {
    /**
     * How far the client-reported duration may fall short of what the server observed between
     * the start and complete requests. Covers differences in request latency; anything shorter
     * is treated as a tampered start/end time and the server-observed duration is used instead.
     */
    static final long DURATION_TOLERANCE_MS = 3000;

    @Autowired
    private TypingTestRepository repository;
    @Autowired
    private TestSessionRepository sessionRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    MissedCharactersRepository missedCharactersRepository;

    public ResponseEntity<?> createTestSession(Long id, Jwt jwt, TestSession newSession) {
        String username = jwt.getClaim("username");
        TypingTest test = findTest(id);
        Long startTime = newSession.getStartTime() == null ? System.currentTimeMillis() : newSession.getStartTime();
        var existingSession = sessionRepository.findFirstByTypingTestIdAndUsernameOrderByIdDesc(id, username);
        if (existingSession.isPresent()) {
            var session = existingSession.get();
            session.restartSession(startTime);
            return ResponseEntity.ok().body(new TestSessionDTO(sessionRepository.save(session)));
        }
        var session = new TestSession();
        session.setTypingTest(test);
        session.setUsername(username);
        session.restartSession(startTime);
        sessionRepository.save(session);
        return ResponseEntity.created(URI.create("/api/private/typing-test/" + test.getId() + "/session")).body(new TestSessionDTO(session));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getTestSession(Long id, Jwt jwt) {
        return ResponseEntity.ok().body(new TestSessionDTO(findSession(id, jwt.getClaim("username"))));
    }

    public ResponseEntity<?> completeSession(Long id, Jwt jwt, TestSession session) {
        String username = jwt.getClaim("username");
        var existingSession = sessionRepository.findFirstForUpdateByTypingTestIdAndUsernameOrderByIdDesc(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No session found for this test"));
        if (existingSession.isCompleted()) {
            // Prevents the same run from being submitted (and scored) more than once
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This test session has already been completed");
        }
        if (session.getEndTime() == null || session.getEndTime() <= existingSession.getStartTime()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after the session's start time");
        }
        List<Character> missedCharacters = session.getMissedCharacters() == null ? new ArrayList<>() : session.getMissedCharacters();
        existingSession.setEndTime(session.getEndTime());
        existingSession.setMissedCharacters(missedCharacters);
        updateUserMissedCharacters(missedCharacters, username);
        var score = new Score(existingSession, getValidatedDuration(existingSession, System.currentTimeMillis()));
        scoreRepository.save(score);
        repository.incrementScoresCount(existingSession.getTypingTest().getId());
        return ResponseEntity.ok().body(new ScoreDTO(score));
    }

    static long getValidatedDuration(TestSession session, long serverNow) {
        long clientDuration = session.getEndTime() - session.getStartTime();
        if (session.getServerStartTime() == null) {
            // Session started before server-side timing was recorded
            return clientDuration;
        }
        long serverDuration = serverNow - session.getServerStartTime();
        return Math.max(clientDuration, serverDuration - DURATION_TOLERANCE_MS);
    }

    private void updateUserMissedCharacters(List<Character> missedCharacters, String username) {
        MissedCharacters userMissedCharacters = missedCharactersRepository.findFirstByUsernameOrderByIdAsc(username)
                .orElseGet(() -> new MissedCharacters(username, List.of()));
        userMissedCharacters.addMissedCharacters(missedCharacters);
        missedCharactersRepository.save(userMissedCharacters);
    }

    private TypingTest findTest(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Typing test not found"));
    }

    private TestSession findSession(Long id, String username) {
        return sessionRepository.findFirstByTypingTestIdAndUsernameOrderByIdDesc(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No session found for this test"));
    }
}

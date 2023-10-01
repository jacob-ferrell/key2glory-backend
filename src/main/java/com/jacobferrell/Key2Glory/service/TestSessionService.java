package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.dto.TestSessionDTO;
import com.jacobferrell.Key2Glory.model.ErrorMessage;
import com.jacobferrell.Key2Glory.model.Score;
import com.jacobferrell.Key2Glory.model.TestSession;
import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.repository.ScoreRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import com.jacobferrell.Key2Glory.util.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;
import java.util.List;

@Service
public class TestSessionService {
    @Autowired
    private TypingTestRepository repository;
    @Autowired
    private ScoreRepository scoreRepository;

    public ResponseEntity<?> createTestSession(Long id, Jwt jwt, TestSession newSession) {
        if (jwt == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Must be logged in to create a test session"));
        }
        String username = jwt.getClaim("username");
        TypingTest test = repository.findById(id).orElseThrow();
        List<TestSession> sessions = test.getSessions();
        TestSession existingSession = sessions.stream().filter(session -> session.getUsername().equals(username)).findFirst().orElse(null);
        if (existingSession != null) {
            return restartSession(test, existingSession, newSession.getStartTime());
        }
        System.out.println(newSession.getStartTime() + "!!!!!!!\n");
        newSession.setTypingTest(test);
        newSession.setUsername(username);
        sessions.add(newSession);
        test.setSessions(sessions);
        repository.save(test);
        return ResponseEntity.created(URI.create("/api/private/typing-test/" + test.getId() + "/session")).body(new TestSessionDTO(newSession));
    }

    public ResponseEntity<?> getTestSession(Long id, Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Must be logged in to view test sessions"));
        }
        String username = jwt.getClaim("username");
        TypingTest test = repository.findById(id).orElseThrow();
        TestSession session = test.getSessions().stream().filter(s -> s.getUsername().equals(username)).findFirst().orElseThrow();
        return ResponseEntity.ok().body(new TestSessionDTO(session));
    }

    public ResponseEntity<TestSessionDTO> restartSession(TypingTest test, TestSession session, Long startTime) {
        session.setStartTime(startTime);
        repository.save(test);
        return ResponseEntity.ok().body(new TestSessionDTO(session));
    }

    public ResponseEntity<?> completeSession(Long id, Jwt jwt, TestSession session) {
        if (jwt == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Must be logged in to complete a test session"));
        }
        String username = jwt.getClaim("username");
        TypingTest test = repository.findById(id).orElseThrow();
        TestSession existingSession = test.getSessions().stream().filter(s -> s.getUsername().equals(username)).findFirst().orElseThrow();
        existingSession.setEndTime(session.getEndTime());
        existingSession.setErrors(session.getErrors());
        var score = new Score(existingSession);
        var scores = test.getScores();
        scores.add(score);
        scoreRepository.save(score);
        repository.save(test);
        return ResponseEntity.ok().body(new ScoreDTO(score));
    }
}

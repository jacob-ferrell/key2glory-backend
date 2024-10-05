package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.dto.TestSessionDTO;
import com.jacobferrell.Key2Glory.model.*;
import com.jacobferrell.Key2Glory.repository.MissedCharactersRepository;
import com.jacobferrell.Key2Glory.repository.ScoreRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class TestSessionService {
    @Autowired
    private TypingTestRepository repository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    MissedCharactersRepository missedCharactersRepository;

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
        session.restartSession(startTime);
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
        var test = repository.findById(id).orElseThrow();
        var existingSession = test.getSessions().stream().filter(s -> s.getUsername().equals(username)).findFirst().orElseThrow();
        updateExistingSession(existingSession, session);
        updateUserMissedCharacters(session.getMissedCharacters(), username);
        return ResponseEntity.ok().body(updateScores(test, existingSession));
    }
    private void updateUserMissedCharacters(List<Character> missedCharacters, String username) {
        MissedCharacters userMissedCharacters = missedCharactersRepository.findByUsername(username).orElse(null);
        if (userMissedCharacters == null) {
            var newMissedCharacters = new MissedCharacters(username, missedCharacters);
            missedCharactersRepository.save(newMissedCharacters);
            return;
        }
        userMissedCharacters.addMissedCharacters(missedCharacters);
        missedCharactersRepository.save(userMissedCharacters);
    }
    private void updateExistingSession(TestSession existingSession, TestSession newSession) {
        existingSession.setEndTime(newSession.getEndTime());
        existingSession.setMissedCharacters(newSession.getMissedCharacters());
    }
    private ScoreDTO updateScores(TypingTest test, TestSession existingSession) {
        var score = new Score(existingSession);
        var scores = test.getScores();
        scores.add(score);
        test.setScoresCount((long) scores.size());
        scoreRepository.save(score);
        repository.save(test);
        return new ScoreDTO(score);
    }

}

package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.dto.TypingTestDTO;
import com.jacobferrell.Key2Glory.model.*;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

@Service
public class TypingTestService {
    @Autowired
    private TypingTestRepository repository;


    public TypingTestDTO getTest(Long id, Jwt jwt) {
        var test = repository.findById(id).orElseThrow();
        var testDTO = new TypingTestDTO(test);
        if (jwt != null) {
            String username = jwt.getClaim("username");
            List<Score> scores = test.getScores();
            testDTO.setCurrentUserRating(getUserRating(test, username));
            testDTO.setCurrentUserHighScore(getUserHighScore(scores, username));
            testDTO.setHighScore(getHighScore(scores));
        }
        return testDTO;
    }
    public String getText(Long id) { return repository.findById(id).orElseThrow().getText(); }

    public ResponseEntity<?> createTypingTest(Jwt jwt, TypingTest typingTest) {
        if (!typingTest.textLengthValid()) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Text must within 30 - 3000 characters"));
        }
        String username = jwt.getClaim("username");
        typingTest.setCreatedBy(username);
        repository.save(typingTest);
        return ResponseEntity
                .created(URI.create("/api/public/typing-test/" + typingTest.getId()))
                .body(new TypingTestDTO(typingTest));
    }
    private ScoreDTO getUserHighScore(List<Score> scores, String username) {
        return scores.stream()
                .filter(s -> s.getUsername().equals(username))
                .max(Comparator.comparingDouble(Score::getOverallScore))
                .map(ScoreDTO::new)
                .orElse(null);
    }
    private ScoreDTO getHighScore(List<Score> scores) {
        return scores.stream()
                .max(Comparator.comparingDouble(Score::getOverallScore))
                .map(ScoreDTO::new)
                .orElse(null);
    }

    private Double getUserRating(TypingTest test, String username) {
        return test.getRatings().stream()
                .filter(rating -> rating.getUsername().equals(username))
                .findFirst()
                .map(TestRating::getRating)
                .orElse(null);
    }
    public ResponseEntity<?> updateTypingTest(Long id, TypingTest typingTest, Jwt jwt) {
        TypingTest existingTest = repository.findById(id).orElseThrow();
        String username = jwt.getClaim("username");
        //check current user is creator of test
        if (!existingTest.getCreatedBy().equals(username)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorMessage.from("Current user " + username + " is not the creator of this test"));
        }
        //if test has existing scores, text cannot be changed
        if (!existingTest.getScores().isEmpty() && !existingTest.getText().equals(typingTest.getText())) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Cannot update text of a test that has been taken as this would invalidate user scores"));
        }
        //check updated text is valid length
        if (!typingTest.textLengthValid()) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Text must within 30 - 3000 characters"));
        }
        existingTest.setText(typingTest.getText());
        if (typingTest.getType() != null) {
            existingTest.setType(typingTest.getType());
        }
        repository.save(existingTest);
        return ResponseEntity.ok().body(new TypingTestDTO(existingTest));
    }

}

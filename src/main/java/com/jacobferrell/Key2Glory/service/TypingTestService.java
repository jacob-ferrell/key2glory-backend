package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.TypingTestDTO;
import com.jacobferrell.Key2Glory.model.*;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.net.URI;
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
            testDTO.setCurrentUserRating(getUserRating(test, username));

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

    private Double getUserRating(TypingTest test, String username) {
        return test.getRatings().stream()
                .filter(rating -> rating.getUsername().equals(username))
                .findFirst()
                .map(TestRating::getRating)
                .orElse(null);
    }

}

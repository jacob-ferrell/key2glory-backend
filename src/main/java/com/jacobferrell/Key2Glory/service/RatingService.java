package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.TestRatingDTO;
import com.jacobferrell.Key2Glory.model.ErrorMessage;
import com.jacobferrell.Key2Glory.model.TestRating;
import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.repository.TestRatingRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@Service
public class RatingService {
    @Autowired
    private TypingTestRepository repository;

    @Autowired
    private TestRatingRepository ratingRepository;

    public Double getRating(Long id) {
        return findTest(id).getRating();
    }

    @Transactional
    public ResponseEntity<?> addRating(Jwt jwt, Long id, TestRating rating) {
        double ratingValue = rating.getRating();
        if (ratingValue < 1 || ratingValue > 5) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Rating must be between 1 and 5"));
        }
        TypingTest test = findTest(id);
        String username = jwt.getClaim("username");
        var existingUserRating = ratingRepository.findFirstByTypingTestIdAndUsername(id, username).orElse(null);
        boolean created = existingUserRating == null;
        TestRating saved;
        if (created) {
            rating.setTypingTest(test);
            rating.setUsername(username);
            saved = ratingRepository.save(rating);
        } else {
            existingUserRating.setRating(ratingValue);
            saved = ratingRepository.save(existingUserRating);
        }
        // Recompute from the source of truth so updated ratings are reflected in the average
        ratingRepository.flush();
        test.setRating(ratingRepository.findAverageRating(id));
        repository.save(test);
        var body = new TestRatingDTO(saved);
        if (!created) {
            return ResponseEntity.ok().body(body);
        }
        return ResponseEntity
                .created(URI.create("/api/public/typing-test/" + test.getId() + "/rating"))
                .body(body);
    }

    private TypingTest findTest(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Typing test not found"));
    }
}

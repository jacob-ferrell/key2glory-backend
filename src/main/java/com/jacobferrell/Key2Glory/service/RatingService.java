package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.TestRatingDTO;
import com.jacobferrell.Key2Glory.model.ErrorMessage;
import com.jacobferrell.Key2Glory.model.TestRating;
import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class RatingService {
    @Autowired
    private TypingTestRepository repository;

    @Autowired
    private UserService userService;

    public Double getRating(Long id) {
        TypingTest test = repository.findById(id).orElseThrow();
        return test.getRating();
    }

    public ResponseEntity<?> addRating(Jwt jwt, Long id, TestRating rating) {
        TypingTest test = repository.findById(id).orElseThrow();
        String username = jwt.getClaim("username");
        var existingUserRating = getExistingUserRating(test, username);
        if (existingUserRating != null) {
            existingUserRating.setRating(rating.getRating());
            repository.save(test);
            return ResponseEntity.ok().body(new TestRatingDTO(existingUserRating));
        }
        double ratingValue = rating.getRating();
        if (ratingValue < 1 || ratingValue > 5) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Rating must be between 1 and 5"));
        }
        rating.setTypingTest(test);
        rating.setUsername(username);
        List<TestRating> ratings = test.getRatings();
        test.setRating(getNewAverageRating(test, rating.getRating()));
        ratings.add(rating);
        test.setRatings(ratings);
        repository.save(test);
        return ResponseEntity
                .created(URI.create("/api/public/typing-test/" + test.getId() + "/rating"))
                .body(new TestRatingDTO(rating));
    }

    private TestRating getExistingUserRating(TypingTest test, String username) {
        return test
                .getRatings()
                .stream()
                .filter(rating -> rating.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public double getNewAverageRating(TypingTest test, double rating) {
        var ratings = test.getRatings();
        if (ratings.isEmpty()) {
            return rating;
        }
        return (test.getRating() * test.getRatings().size() + rating) / (test.getRatings().size() + 1);
    }



}

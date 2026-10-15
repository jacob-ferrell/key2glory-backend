package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.dto.TypingTestDTO;
import com.jacobferrell.Key2Glory.dto.TypingTestRequest;
import com.jacobferrell.Key2Glory.dto.TypingTestSortAndFilterDTO;
import com.jacobferrell.Key2Glory.model.*;
import com.jacobferrell.Key2Glory.repository.FavoritesRepository;
import com.jacobferrell.Key2Glory.repository.ScoreRepository;
import com.jacobferrell.Key2Glory.repository.TestRatingRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TypingTestService {
    private static final String LENGTH_ERROR =
            "Text must be within " + TypingTest.MIN_LENGTH + " - " + TypingTest.MAX_LENGTH + " characters";

    @Autowired
    private TypingTestRepository repository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private TestRatingRepository ratingRepository;
    @Autowired
    private FavoritesRepository favoritesRepository;

    @Transactional(readOnly = true)
    public TypingTestDTO getTest(Long id, Jwt jwt) {
        return toDetailedDTO(findTest(id), jwt);
    }

    @Transactional(readOnly = true)
    public TypingTestDTO getRandomTest(TypingTestType type, Long excludeId, Jwt jwt) {
        long exclude = excludeId == null ? -1 : excludeId;
        long count = repository.countByTypeExcluding(type, exclude);
        if (count == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No typing tests of type " + type);
        }
        int offset = (int) ThreadLocalRandom.current().nextLong(count);
        var test = repository.findByTypeExcluding(type, exclude, PageRequest.of(offset, 1)).get(0);
        return toDetailedDTO(test, jwt);
    }

    public String getText(Long id) { return findTest(id).getText(); }

    private TypingTest findTest(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Typing test not found"));
    }

    private TypingTestDTO toDetailedDTO(TypingTest test, Jwt jwt) {
        var testDTO = new TypingTestDTO(test);
        testDTO.setHighScore(scoreRepository.findFirstByTypingTestIdOrderByOverallScoreDesc(test.getId())
                .map(ScoreDTO::new)
                .orElse(null));
        if (jwt != null) {
            String username = jwt.getClaim("username");
            testDTO.setCurrentUserRating(ratingRepository.findFirstByTypingTestIdAndUsername(test.getId(), username)
                    .map(TestRating::getRating)
                    .orElse(null));
            testDTO.setCurrentUserHighScore(scoreRepository
                    .findFirstByTypingTestIdAndUsernameOrderByOverallScoreDesc(test.getId(), username)
                    .map(ScoreDTO::new)
                    .orElse(null));
            testDTO.setFavorited(favoritesRepository.findFirstByUsernameOrderByIdAsc(username)
                    .map(favorites -> favorites.contains(test.getId()))
                    .orElse(false));
        }
        return testDTO;
    }

    public ResponseEntity<?> createTypingTest(Jwt jwt, TypingTestRequest request) {
        var typingTest = new TypingTest(
                request.text(),
                jwt.getClaim("username"),
                request.type() == null ? TypingTestType.GENERAL : request.type());
        if (!typingTest.textLengthValid()) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from(LENGTH_ERROR));
        }
        repository.save(typingTest);
        return ResponseEntity
                .created(URI.create("/api/public/typing-test/" + typingTest.getId()))
                .body(new TypingTestDTO(typingTest));
    }

    @Transactional(readOnly = true)
    public Page<TypingTestDTO> getTests(TypingTestSortAndFilterDTO params, String username) {
        Set<Long> favoriteIds = username == null ? Set.of() : favoritesRepository.findFirstByUsernameOrderByIdAsc(username)
                .map(favorites -> favorites.getFavorites().stream().map(TypingTest::getId).collect(Collectors.toSet()))
                .orElse(Set.of());
        return repository.findAll(params.createSpecification(), params.getPageRequest()).map(test -> {
            var dto = new TypingTestDTO(test);
            if (username != null) {
                dto.setFavorited(favoriteIds.contains(test.getId()));
            }
            return dto;
        });
    }

    @Transactional
    public ResponseEntity<?> updateTypingTest(Long id, TypingTestRequest request, Jwt jwt) {
        TypingTest existingTest = findTest(id);
        String username = jwt.getClaim("username");
        //check current user is creator of test
        if (!existingTest.getCreatedBy().equals(username)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorMessage.from("Only the creator of a test can update it"));
        }
        String newText = TypingTest.cleanText(request.text());
        //if test has existing scores, text cannot be changed
        if (existingTest.getScoresCount() > 0 && !existingTest.getText().equals(newText)) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorMessage.from("Cannot update text of a test that has been taken as this would invalidate user scores"));
        }
        existingTest.setText(newText);
        //check updated text is valid length
        if (!existingTest.textLengthValid()) {
            // Throw so the transaction rolls back the in-memory change
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, LENGTH_ERROR);
        }
        if (request.type() != null) {
            existingTest.setType(request.type());
        }
        repository.save(existingTest);
        return ResponseEntity.ok().body(new TypingTestDTO(existingTest));
    }

}

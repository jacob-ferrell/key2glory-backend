package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.model.Score;
import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.repository.ScoreRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class ScoreService {
    @Autowired
    private TypingTestRepository typingTestRepository;
    @Autowired
    private ScoreRepository repository;
    @Autowired
    private UserService userService;
    public List<Score> getUserScores(Long id, Jwt jwt) {
        String username = jwt.getClaim("username");
        return repository.findByTestAndUser(id, username);
    }
    public ScoreDTO getScore(Long id) {
        var score = repository.findById(id).orElseThrow();
        return new ScoreDTO(score);
    }
    public List<ScoreDTO> getScoresByTest(Long id) {
        var scores = repository.findByTestOrderByWpmDesc(id);
        return convertScoresToDTOs(scores);
    }

    private List<ScoreDTO> convertScoresToDTOs (List<Score> scores) {
        return scores.stream().map(ScoreDTO::new).toList();
    }

    public ResponseEntity<?> addScore(Long id, Score score, Jwt jwt) {
        TypingTest typingTest = typingTestRepository.findById(id).orElseThrow();
        String username = jwt.getClaim("username");
        score.setUsername(username);
        score.setTypingTest(typingTest);
        repository.save(score);
        List<Score> prevScores = typingTest.getScores();
        prevScores.add(score);
        typingTest.setScores(prevScores);
        typingTestRepository.save(typingTest);
        return ResponseEntity
                .created(URI.create("/api/private/score/" + score.getId()))
                .body(new ScoreDTO(score));
    }
}

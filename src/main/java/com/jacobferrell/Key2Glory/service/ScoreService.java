package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.LeaderboardEntryDTO;
import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.model.Score;
import com.jacobferrell.Key2Glory.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ScoreService {
    public static final int MAX_LIMIT = 100;
    // Averages over one or two runs aren't meaningful enough to rank
    public static final long LEADERBOARD_MIN_TESTS = 3;

    @Autowired
    private ScoreRepository repository;

    public List<ScoreDTO> getUserScores(Long id, Jwt jwt) {
        String username = jwt.getClaim("username");
        return convertScoresToDTOs(repository.findByTestAndUser(id, username));
    }
    public ScoreDTO getScore(Long id) {
        var score = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Score not found"));
        return new ScoreDTO(score);
    }

    /** Leaderboard for a single test: each user's best run, highest overall score first. */
    public List<ScoreDTO> getTestLeaderboard(Long id, int limit) {
        int size = clampLimit(limit);
        Set<String> seen = new HashSet<>();
        return repository.findBestScoresPerUser(id, PageRequest.of(0, size * 2)).stream()
                .filter(score -> seen.add(score.getUsername()))
                .limit(size)
                .map(ScoreDTO::new)
                .toList();
    }

    public List<LeaderboardEntryDTO> getLeaderboard(int limit) {
        return repository.findLeaderboard(LEADERBOARD_MIN_TESTS, PageRequest.of(0, clampLimit(limit)));
    }

    public List<ScoreDTO> getRecentScores(Jwt jwt, int limit) {
        String username = jwt.getClaim("username");
        return convertScoresToDTOs(repository.findByUsernameOrderByIdDesc(username, PageRequest.of(0, clampLimit(limit))));
    }

    private static int clampLimit(int limit) {
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }

    private List<ScoreDTO> convertScoresToDTOs (List<Score> scores) {
        return scores.stream().map(ScoreDTO::new).toList();
    }
}

package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.dto.LeaderboardEntryDTO;
import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Scores are only ever created by completing a test session (see TestSessionController),
 * so the server is the one calculating them.
 */
@RestController
@RequestMapping(path= "api")
public class ScoreController {
    @Autowired
    private ScoreService service;
    @GetMapping("/public/typing-test/{id}/scores")
    public ResponseEntity<List<ScoreDTO>> getScores(
            @PathVariable Long id,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok().body(service.getTestLeaderboard(id, limit));
    }
    @GetMapping("/public/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard(
            @RequestParam(name = "limit", defaultValue = "25") int limit
    ) {
        return ResponseEntity.ok().body(service.getLeaderboard(limit));
    }
    @GetMapping("/private/typing-test/{id}/scores")
    public ResponseEntity<List<ScoreDTO>> getUserScores(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(service.getUserScores(id, jwt));
    }
    @GetMapping("/private/scores/recent")
    public ResponseEntity<List<ScoreDTO>> getRecentScores(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "limit", defaultValue = "30") int limit
    ) {
        return ResponseEntity.ok().body(service.getRecentScores(jwt, limit));
    }
    @GetMapping("/private/score/{id}")
    public ResponseEntity<ScoreDTO> getScore(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.getScore(id));
    }
}

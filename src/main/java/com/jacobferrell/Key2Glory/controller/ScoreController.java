package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.dto.ScoreDTO;
import com.jacobferrell.Key2Glory.model.Score;
import com.jacobferrell.Key2Glory.service.ScoreService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path= "api")
@CrossOrigin(origins = "http://localhost:5174", allowedHeaders = "*")
public class ScoreController {
    @Autowired
    private ScoreService service;
    @GetMapping("/public/typing-test/{id}/scores")
    public ResponseEntity<List<ScoreDTO>> getScores(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.getScoresByTest(id));
    }
    @GetMapping("/private/typing-test/{id}/scores")
    public ResponseEntity<List<Score>> getUserScores(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(service.getUserScores(id, jwt));
    }
    @GetMapping("/private/score/{id}")
    public ResponseEntity<ScoreDTO> getScore(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.getScore(id));
    }
    @PostMapping("/private/typing-test/{id}/scores")
    public ResponseEntity<?> addScore(@PathVariable Long id, @RequestBody Score score, @AuthenticationPrincipal Jwt jwt) {
        return service.addScore(id, score, jwt);
    }
}

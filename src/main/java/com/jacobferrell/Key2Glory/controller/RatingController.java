package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.dto.TestRatingDTO;
import com.jacobferrell.Key2Glory.model.TestRating;
import com.jacobferrell.Key2Glory.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path= "api")
@CrossOrigin(origins = "http://localhost:5174", allowedHeaders = "*")
public class RatingController {
    @Autowired
    private RatingService service;

    @GetMapping("/public/typing-test/{id}/rating")
    public ResponseEntity<Double> getRating(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.getRating(id));
    }

    @PostMapping("/private/typing-test/{id}/rating")
    public ResponseEntity<?> addRating(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id, @RequestBody TestRating rating) {
        return service.addRating(jwt, id, rating);
    }


}

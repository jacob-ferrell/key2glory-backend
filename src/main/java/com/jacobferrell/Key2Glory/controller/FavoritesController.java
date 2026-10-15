package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.dto.TypingTestDTO;
import com.jacobferrell.Key2Glory.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class FavoritesController {
    @Autowired
    private FavoritesService favoritesService;
    @GetMapping("/private/favorites")
    public ResponseEntity<List<TypingTestDTO>> getFavorites(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(favoritesService.getFavorites(jwt));
    }

    @PostMapping("/private/favorites/{testId}")
    public ResponseEntity<List<TypingTestDTO>> addFavorite(@AuthenticationPrincipal Jwt jwt, @PathVariable Long testId) {
        return ResponseEntity.ok().body(favoritesService.addToFavorites(jwt, testId));
    }
    @DeleteMapping("/private/favorites/{testId}")
    public ResponseEntity<List<TypingTestDTO>> removeFavorite(@AuthenticationPrincipal Jwt jwt, @PathVariable Long testId) {
        return ResponseEntity.ok().body(favoritesService.removeFromFavorites(jwt, testId));
    }
}

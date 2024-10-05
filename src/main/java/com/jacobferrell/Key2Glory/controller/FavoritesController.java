package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FavoritesController {
    @Autowired
    private FavoritesService favoritesService;
    @GetMapping("/private/favorites")
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal Jwt jwt) {
        return favoritesService.getFavorites(jwt);
    }

    @PostMapping("/private/favorites/{testId}")
    public ResponseEntity<?> addFavorite(@AuthenticationPrincipal Jwt jwt, @PathVariable Long testId) {
        return favoritesService.addToFavorites(jwt, testId);
    }
    @PutMapping("/private/favorites/{testId}")
    public ResponseEntity<?> removeFavorite(@AuthenticationPrincipal Jwt jwt, @PathVariable Long testId) {
        return favoritesService.removeFromFavorites(jwt, testId);
    }
}

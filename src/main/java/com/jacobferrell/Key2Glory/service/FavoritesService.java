package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.FavoritesDTO;
import com.jacobferrell.Key2Glory.model.Favorites;
import com.jacobferrell.Key2Glory.repository.FavoritesRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class FavoritesService {
    @Autowired
    private FavoritesRepository repository;
    @Autowired
    private TypingTestRepository typingTestRepository;
    public ResponseEntity<?> getFavorites(Jwt jwt) {
        String username = jwt.getClaim("username");
        var favorites = repository.findByUsername(username).orElse(null);
        if (favorites == null) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
        return ResponseEntity.ok().body(favorites.toDTO());
    }

    public ResponseEntity<?> addToFavorites(Jwt jwt, Long testId) {
        String username = jwt.getClaim("username");
        var testToAdd = typingTestRepository.findById(testId).orElseThrow();
        var favorites = repository.findByUsername(username).orElse(null);
        if (favorites == null) {
            favorites = new Favorites(username);
        }
        favorites.addFavorite(testToAdd);
        repository.save(favorites);
        return ResponseEntity.ok().body(favorites.toDTO());
    }

    public ResponseEntity<?> removeFromFavorites(Jwt jwt, Long testId) {
        String username = jwt.getClaim("username");
        var testToRemove = typingTestRepository.findById(testId).orElseThrow();
        var favorites = repository.findByUsername(username).orElse(null);
        if (favorites == null) {
            return ResponseEntity.ok().body(new FavoritesDTO());
        }
        favorites.removeFavorite(testToRemove);
        repository.save(favorites);
        return ResponseEntity.ok().body(favorites.toDTO());
    }
}

package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.dto.TypingTestDTO;
import com.jacobferrell.Key2Glory.model.Favorites;
import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.repository.FavoritesRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class FavoritesService {
    @Autowired
    private FavoritesRepository repository;
    @Autowired
    private TypingTestRepository typingTestRepository;

    @Transactional(readOnly = true)
    public List<TypingTestDTO> getFavorites(Jwt jwt) {
        String username = jwt.getClaim("username");
        return repository.findByUsername(username)
                .map(this::toDTOs)
                .orElse(List.of());
    }

    public List<TypingTestDTO> addToFavorites(Jwt jwt, Long testId) {
        String username = jwt.getClaim("username");
        var testToAdd = typingTestRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Typing test not found"));
        var favorites = repository.findByUsername(username).orElseGet(() -> new Favorites(username));
        favorites.addFavorite(testToAdd);
        repository.save(favorites);
        return toDTOs(favorites);
    }

    public List<TypingTestDTO> removeFromFavorites(Jwt jwt, Long testId) {
        String username = jwt.getClaim("username");
        return repository.findByUsername(username)
                .map(favorites -> {
                    favorites.removeFavorite(testId);
                    return toDTOs(repository.save(favorites));
                })
                .orElse(List.of());
    }

    private List<TypingTestDTO> toDTOs(Favorites favorites) {
        return favorites.getFavorites().stream()
                .sorted(Comparator.comparing(TypingTest::getId))
                .map(test -> {
                    var dto = new TypingTestDTO(test);
                    dto.setFavorited(true);
                    return dto;
                })
                .toList();
    }
}

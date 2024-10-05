package com.jacobferrell.Key2Glory.dto;

import com.jacobferrell.Key2Glory.model.Favorites;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoritesDTO {
    private Long id;
    private String username;
    private Set<TypingTestDTO> typingTests = new HashSet<>();
    public FavoritesDTO() { }
    public FavoritesDTO(Favorites favorites) {
        this.id = favorites.getId();
        this.username = favorites.getUsername();
        this.typingTests = favorites.getFavorites().stream().map(TypingTestDTO::new).collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<TypingTestDTO> getTypingTests() {
        return typingTests;
    }
}

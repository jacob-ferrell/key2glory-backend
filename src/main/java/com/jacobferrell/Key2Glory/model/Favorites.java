package com.jacobferrell.Key2Glory.model;

import com.jacobferrell.Key2Glory.dto.FavoritesDTO;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Favorites {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name="username")
    private String username;
    @OneToMany(mappedBy="favorites", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<TypingTest> favorites = new HashSet<>();
    public Favorites() {}

    public Favorites(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<TypingTest> getFavorites() {
        return favorites;
    }

    public void addFavorite(TypingTest typingTest) {
        this.favorites.add(typingTest);
    }

    public FavoritesDTO toDTO() {
        return new FavoritesDTO(this);
    }

    public void removeFavorite(TypingTest typingTest) {
        this.favorites.remove(typingTest);
    }
}

package com.jacobferrell.Key2Glory.model;

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
    // Many-to-many: the same test can be in any number of users' favorites
    @ManyToMany
    @JoinTable(
            name = "favorites_typing_tests",
            joinColumns = @JoinColumn(name = "favorites_id"),
            inverseJoinColumns = @JoinColumn(name = "typing_test_id"))
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
        if (!contains(typingTest.getId())) {
            this.favorites.add(typingTest);
        }
    }

    public void removeFavorite(Long typingTestId) {
        this.favorites.removeIf(test -> test.getId().equals(typingTestId));
    }

    public boolean contains(Long typingTestId) {
        return favorites.stream().anyMatch(test -> test.getId().equals(typingTestId));
    }
}

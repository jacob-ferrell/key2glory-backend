package com.jacobferrell.Key2Glory.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="missed_characters")
public class MissedCharacters {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    @Column(name="username", nullable=false)
    @JsonIgnore
    private String username;
    @ElementCollection
    @MapKeyColumn(name="character")
    @Column(name="count")
    @CollectionTable(name="missed_characters_counts", joinColumns=@JoinColumn(name="missed_characters_id"))
    private final Map<Character, Integer> characterCounts = new HashMap<>();
    public MissedCharacters() {
    }
    public MissedCharacters(String username, List<Character> charactersList) {
        this.username = username;
        addMissedCharacters(charactersList);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<Character, Integer> getCharacterCounts() {
        return characterCounts;
    }

    public void addMissedCharacters(List<Character> characters) {
        for (Character character : characters) {
            addCharacter(character);
        }

    }
    private void addCharacter(Character character) {
        Integer count = characterCounts.get(character);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        characterCounts.put(character, count);
    }
}

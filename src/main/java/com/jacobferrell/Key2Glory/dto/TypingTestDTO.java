package com.jacobferrell.Key2Glory.dto;

import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.model.TypingTestType;

import java.sql.Timestamp;

public class TypingTestDTO {
    private final Long id;
    private final String text;
    private final int ratings;
    private final Double rating;
    private final String createdBy;
    private final TypingTestType type;
    private final Timestamp createdAt;
    private Double currentUserRating;
    private ScoreDTO highScore;
    private ScoreDTO currentUserHighScore;
    private Boolean favorited;
    private final Long scoresCount;
    private final Long wordsCount;
    public TypingTestDTO(TypingTest test) {
        this.id = test.getId();
        this.text = test.getText();
        this.ratings = test.getRatingsCount();
        this.rating = test.getRating();
        this.createdBy = test.getCreatedBy();
        this.wordsCount = test.getWordsCount();
        this.type = test.getType();
        this.scoresCount = test.getScoresCount();
        this.createdAt = test.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getRatings() {
        return ratings;
    }

    public Double getRating() {
        return rating;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public ScoreDTO getHighScore() {
        return highScore;
    }

    public Double getCurrentUserRating() {
        return currentUserRating;
    }

    public void setCurrentUserRating(Double currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    public void setHighScore(ScoreDTO highScore) {
        this.highScore = highScore;
    }

    public TypingTestType getType() {
        return type;
    }

    public Long getScoresCount() {
        return scoresCount;
    }

    public Long getWordsCount() {
        return wordsCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public ScoreDTO getCurrentUserHighScore() {
        return currentUserHighScore;
    }

    public void setCurrentUserHighScore(ScoreDTO currentUserHighScore) {
        this.currentUserHighScore = currentUserHighScore;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }
}

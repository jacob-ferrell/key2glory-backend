package com.jacobferrell.Key2Glory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.model.TypingTestType;
import com.jacobferrell.Key2Glory.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

public class TypingTestDTO {
    private final Long id;
    private final String text;
    private final int ratings;
    private final Double rating;
    private final String createdBy;
    private final TypingTestType type;
    private Double currentUserRating;
    private ScoreDTO highScore;
    private ScoreDTO currentUserHighScore;
    private Long scoresCount;
    private Long wordsCount;
    public TypingTestDTO(TypingTest test) {
        this.id = test.getId();
        this.text = test.getText();
        this.ratings = test.getRatings().size();
        this.rating = test.getRating();
        this.createdBy = test.getCreatedBy();
        this.wordsCount = test.getWordsCount();
        this.type = test.getType();
        this.scoresCount = test.getScoresCount();
        //this.highScore = new ScoreDTO(repository.findHighScores(test.getId(), PageRequest.of(0, 1)).orElseThrow().get(0));
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

    public ScoreDTO getCurrentUserHighScore() {
        return currentUserHighScore;
    }

    public void setCurrentUserHighScore(ScoreDTO currentUserHighScore) {
        this.currentUserHighScore = currentUserHighScore;
    }
}

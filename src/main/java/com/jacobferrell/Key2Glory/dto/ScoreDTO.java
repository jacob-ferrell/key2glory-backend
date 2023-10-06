package com.jacobferrell.Key2Glory.dto;

import com.jacobferrell.Key2Glory.model.Score;

import java.util.List;

public class ScoreDTO {
    private Long id;
    private double wpm;
    private double accuracy;
    private double time;
    private double overallScore;
    private List<Character> missedCharacters;
    private String username;
    private Long typingTest;
    private String createdAt;

    public ScoreDTO(Score score) {
        this.id = score.getId();
        this.wpm = score.getWpm();
        this.accuracy = score.getAccuracy();
        this.time = score.getTime();
        this.username = score.getUsername();
        this.typingTest = score.getTypingTest().getId();
        this.createdAt = score.getCreatedAt();
        this.overallScore = score.getOverallScore();
        this.missedCharacters = score.getMissedCharacters();
    }

    public ScoreDTO() { }

    public double getWpm() {
        return wpm;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getTime() {
        return time;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public List<Character> getMissedCharacters() {
        return missedCharacters;
    }

    public String getUsername() {
        return username;
    }

    public Long getTypingTest() {
        return typingTest;
    }
}

package com.jacobferrell.Key2Glory.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="score")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username")
    private String username;

    @ManyToOne
    @JoinColumn(name="typing_test_id", nullable=false)
    private TypingTest typingTest;

    @Column(nullable = false, name="wpm")
    private double wpm;

    @Column(nullable = false, name="time")
    private double time;

    @Column(nullable = false, name="accuracy")
    private double accuracy;
    @Column(name="overall_score")
    private double overallScore;

    @CreationTimestamp
    @Column(nullable = false, name="created_at")
    private String createdAt;
    @Column(name="missed_characters")
    private List<Character> missedCharacters;
    public Score() {

    }

    /**
     * @param durationMs time taken to complete the test, already validated by the caller
     */
    public Score(TestSession session, long durationMs) {
        this.typingTest = session.getTypingTest();
        this.username = session.getUsername();
        this.time = durationMs;
        this.missedCharacters = session.getMissedCharacters() == null ? new ArrayList<>() : session.getMissedCharacters();
        this.wpm = calcWPM();
        this.accuracy = calcAccuracy();
        this.overallScore = calcOverallScore();
    }
    private double calcWPM() {
        double timeInMinutes = Math.max(time, 1) / 1000 / 60;
        return round(typingTest.getWordsCount() / timeInMinutes);
    }
    private double calcAccuracy() {
        long charactersCount = typingTest.getText().length();
        // A character can be missed more than once, so don't let accuracy go negative
        long correctKeyPresses = Math.max(0, charactersCount - missedCharacters.size());
        return round((double) correctKeyPresses / charactersCount * 100);
    }
    private double calcOverallScore() {
        return round(wpm * (accuracy / 100));
    }
    private static double round(double value) {
        return Math.round(value * 100) / 100.0;
    }


    public Long getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUsername() {
        return username;
    }

    public double getWpm() {
        return wpm;
    }

    public double getTime() {
        return time;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public List<Character> getMissedCharacters() {
        return missedCharacters;
    }

    public TypingTest getTypingTest() {
        return typingTest;
    }
}

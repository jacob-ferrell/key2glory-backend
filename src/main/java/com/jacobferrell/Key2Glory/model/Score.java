package com.jacobferrell.Key2Glory.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.text.DecimalFormat;
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

    public Score(double wpm, double time, double accuracy) {
        this.typingTest = null;
        this.wpm = wpm;
        this.time = time;
        this.accuracy = accuracy;
    }

    public Score(TestSession session) {
        DecimalFormat df = new DecimalFormat("0.00");
        this.typingTest = session.getTypingTest();
        this.username = session.getUsername();
        this.time = session.getEndTime() - session.getStartTime();
        this.missedCharacters = session.getMissedCharacters();
        this.wpm = calcWPM(df);
        this.accuracy = calcAccuracy(df);
        this.overallScore = calcOverallScore(df);
    }
    private Double calcWPM(DecimalFormat df) {
        double timeInSeconds = time / 1000;
        double timeInMinutes = timeInSeconds / 60;
        return Double.parseDouble(df.format((double) typingTest.getWordsCount() / timeInMinutes));
    }
    private Double calcAccuracy(DecimalFormat df) {
        long charactersCount = typingTest.getText().length();
        long correctKeyPresses = charactersCount - missedCharacters.size();
        return Double.parseDouble(df.format((double) correctKeyPresses / charactersCount * 100));
    }
    private Double calcOverallScore(DecimalFormat df) {
        return Double.parseDouble(df.format(wpm * (accuracy / 100)));
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

    public void setUsername(String nickname) {
        this.username = nickname;
    }
    public void setTypingTest(TypingTest typingTest) {
        this.typingTest = typingTest;
    }

    public TypingTest getTypingTest() {
        return typingTest;
    }
}

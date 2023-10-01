package com.jacobferrell.Key2Glory.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.text.DecimalFormat;

@Entity
@Table(name="scores")
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

    @CreationTimestamp
    @Column(nullable = false, name="created_at")
    private String createdAt;
    private Long errorsCount;
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
        this.errorsCount = session.getErrors();
        this.wpm = calcWPM(df);
        this.accuracy = calcAccuracy(df);
    }
    private Double calcWPM(DecimalFormat df) {
        return Double.parseDouble(df.format((double) typingTest.getWordsCount() / 5 / (time / 1000 / 60)));
    }
    private Double calcAccuracy(DecimalFormat df) {
        return Double.parseDouble(df.format(((typingTest.getText().length() - errorsCount) / typingTest.getText().length()) * 100));
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

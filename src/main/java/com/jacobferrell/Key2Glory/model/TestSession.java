package com.jacobferrell.Key2Glory.model;

import com.jacobferrell.Key2Glory.util.DateTime;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="test_session")
public class TestSession {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name="username")
    private String username;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="typing_test_id", nullable=false)
    private TypingTest typingTest;

    @Column(name="start_time")
    private Long startTime;
    @Column(name="end_time")
    private Long endTime;
    @Column(name="missed_characters")
    private List<Character> missedCharacters;

    public TestSession(TypingTest typingTest, String username) {
        this.typingTest = typingTest;
        this.username = username;
        this.startTime = new DateTime().toMillis();
    }
    public TestSession(Long endTime, List<Character> missedCharacters) {
        this.startTime = 0L;
        this.endTime = endTime;
        this.missedCharacters = missedCharacters;
    }
    public TestSession(Long startTime) {
        this.startTime = startTime;
    }

    public TestSession() {

    }

    public Long getId() {
        return id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public String getUsername() {
        return username;
    }

    public TypingTest getTypingTest() {
        return typingTest;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<Character> getMissedCharacters() {
        return missedCharacters;
    }

    public void setMissedCharacters(List<Character> missedCharacters) {
        this.missedCharacters = missedCharacters;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTypingTest(TypingTest typingTest) {
        this.typingTest = typingTest;
    }
    public void restartSession(Long startTime) {
        this.startTime = startTime;
        this.endTime = null;
        this.missedCharacters = new ArrayList<>();
    }
}

package com.jacobferrell.Key2Glory.model;

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

    // Client-reported times (ms since epoch), used for precise timing
    @Column(name="start_time")
    private Long startTime;
    @Column(name="end_time")
    private Long endTime;
    // Server clock when the session started, used to sanity check the client-reported duration
    @Column(name="server_start_time")
    private Long serverStartTime;
    @Column(name="missed_characters")
    private List<Character> missedCharacters;

    public TestSession() {

    }

    public Long getId() {
        return id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public Long getServerStartTime() {
        return serverStartTime;
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

    public boolean isCompleted() {
        return endTime != null;
    }

    public void restartSession(Long startTime) {
        this.startTime = startTime;
        this.serverStartTime = System.currentTimeMillis();
        this.endTime = null;
        this.missedCharacters = new ArrayList<>();
    }
}

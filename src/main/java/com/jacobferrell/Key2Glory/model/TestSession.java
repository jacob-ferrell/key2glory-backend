package com.jacobferrell.Key2Glory.model;

import com.jacobferrell.Key2Glory.util.DateTime;
import jakarta.persistence.*;

@Entity
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
    @Column(name="errors")
    private Long errors;

    public TestSession(TypingTest typingTest, String username) {
        this.typingTest = typingTest;
        this.username = username;
        this.startTime = new DateTime().toMillis();
    }
    public TestSession(Long endTime, Long errors) {
        this.startTime = 0L;
        this.endTime = endTime;
        this.errors = errors;
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

    public Long getErrors() {
        return errors;
    }

    public void setErrors(Long errors) {
        this.errors = errors;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTypingTest(TypingTest typingTest) {
        this.typingTest = typingTest;
    }
}

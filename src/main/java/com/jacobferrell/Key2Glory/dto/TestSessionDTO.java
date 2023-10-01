package com.jacobferrell.Key2Glory.dto;

import com.jacobferrell.Key2Glory.model.TestSession;

public class TestSessionDTO {
    private Long typingTest;
    private String username;
    private Long startTime;
    private Long endTime;
    private Long errors;
    public TestSessionDTO() {

    }
    public TestSessionDTO(TestSession session) {
        this.typingTest = session.getTypingTest().getId();
        this.username = session.getUsername();
        this.startTime = session.getStartTime();
        this.endTime = session.getEndTime();
        this.errors = session.getErrors();
    }

    public Long getTypingTest() {
        return typingTest;
    }

    public String getUsername() {
        return username;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }
    public Long getErrors() {
        return errors;
    }
}

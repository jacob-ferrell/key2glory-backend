package com.jacobferrell.Key2Glory.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jacobferrell.Key2Glory.model.TestSession;

import java.util.List;

public class TestSessionDTO {
    private Long typingTest;
    @JsonIgnore
    private String username;
    private Long startTime;
    private Long endTime;
    private List<Character> missedCharacters;
    public TestSessionDTO() {

    }
    public TestSessionDTO(TestSession session) {
        this.typingTest = session.getTypingTest().getId();
        this.username = session.getUsername();
        this.startTime = session.getStartTime();
        this.endTime = session.getEndTime();
        this.missedCharacters = session.getMissedCharacters();
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

    public List<Character> getMissedCharcters() {
        return missedCharacters;
    }
}

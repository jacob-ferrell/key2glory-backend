package com.jacobferrell.Key2Glory.dto;

import com.jacobferrell.Key2Glory.model.TestRating;
public class TestRatingDTO {
    private Long typingTest;
    private double rating;
    private String username;
    public TestRatingDTO(TestRating testRating) {
        this.typingTest = testRating.getTypingTest().getId();
        this.rating = testRating.getRating();
        this.username = testRating.getUsername();
    }
    public TestRatingDTO() { }

    public Long getTypingTest() {
        return typingTest;
    }

    public double getRating() {
        return rating;
    }

    public String getUsername() {
        return username;
    }
}

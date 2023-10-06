package com.jacobferrell.Key2Glory.model;

import jakarta.persistence.*;

@Entity
@Table(name="test_rating")
public class TestRating {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="typing_test_id", nullable=false)
    private TypingTest typingTest;

    @Column(nullable = false, name="rating")
    private double rating;

    @Column(nullable = false, name="username")
    private String username;

    public TestRating() {

    }

    public TestRating(double rating) {
        this.rating = rating;
    }
    public Long getId() {
        return id;
    }

    public TypingTest getTypingTest() {
        return typingTest;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTypingTest(TypingTest typingTest) {
        this.typingTest = typingTest;
    }


}

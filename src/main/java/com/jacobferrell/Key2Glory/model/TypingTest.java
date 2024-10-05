package com.jacobferrell.Key2Glory.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="typing_test")
public class TypingTest {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, name="text", length = 2000)
    private String text;
    @Column(name="created_by")
    private String createdBy;

    @Column(name="rating")
    private Double rating;
    @Column(name="type")
    private TypingTestType type;
    @CreationTimestamp
    @Column(name="created_at")
    private java.sql.Timestamp createdAt;
    @Column(name="length")
    private Long length;

    @OneToMany(mappedBy="typingTest", cascade = CascadeType.ALL)
    private List<Score> scores = new ArrayList<>();
    @Column(name="scores_count")
    private Long scoresCount;

    @OneToMany(mappedBy="typingTest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestRating> ratings = new ArrayList<>();

    @OneToMany(mappedBy="typingTest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestSession> sessions = new ArrayList<>();
    private Long wordsCount;
    @ManyToOne
    @JoinColumn(name="favorites_id", nullable=true)
    private Favorites favorites;

    public TypingTest(String text) {
        this.text = text;
        cleanText();
        this.createdBy = "Key2Glory";
        this.wordsCount = calcWordsCount(text);
        this.type = TypingTestType.CUSTOM;
        this.length = (long) text.length();
    }
    public TypingTest(String text, TypingTestType type) {
        this.text = text;
        cleanText();
        this.createdBy = "Key2Glory";
        this.wordsCount = calcWordsCount(text);
        this.type = type;
        this.length = (long) text.length();
    }
    public TypingTest(String text, String createdBy, TypingTestType type) {
        this.text = text;
        cleanText();
        this.createdBy = createdBy;
        this.wordsCount = calcWordsCount(text);
        this.type = type;
        this.length = (long) text.length();
    }
    public TypingTest(String text, String createdBy) {
        this.text = text;
        cleanText();
        this.createdBy = createdBy;
        this.wordsCount = calcWordsCount(text);
        this.length = (long) text.length();
    }
    private Long calcWordsCount(String text) {
        return (long) text.split(" ").length;
    }
    public TypingTest() {
        this.text = null;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
        this.length = (long) text.length();
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<TestRating> getRatings() {
        return ratings;
    }

    public void setRatings(List<TestRating> ratings) {
        this.ratings = ratings;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public Long getScoresCount() {
        return scoresCount;
    }

    public void setScoresCount(Long scoresCount) {
        this.scoresCount = scoresCount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setCreatedBy(String username) {
        this.createdBy = username;
    }

    public List<TestSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<TestSession> sessions) {
        this.sessions = sessions;
    }

    public Long getWordsCount() {
        return wordsCount;
    }

    public TypingTestType getType() {
        return type;
    }

    public void setType(TypingTestType type) {
        this.type = type;
    }
    public void cleanText() {
        setText(this.text.replaceAll("\\n", "").replaceAll("\\s{2,}", " "));
    }

    public boolean textLengthValid() {
        assert text != null;
        int length = text.length();
        return length <= 3_000 && length >= 30;
    }
}

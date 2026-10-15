package com.jacobferrell.Key2Glory.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="typing_test")
public class TypingTest {
    public static final int MIN_LENGTH = 30;
    public static final int MAX_LENGTH = 2000;

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, name="text", length = MAX_LENGTH)
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
    private Long scoresCount = 0L;

    @OneToMany(mappedBy="typingTest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestRating> ratings = new ArrayList<>();

    // Computed in the same select so listing tests doesn't lazy-load every rating
    @Formula("(select count(*) from test_rating r where r.typing_test_id = id)")
    private int ratingsCount;

    @OneToMany(mappedBy="typingTest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestSession> sessions = new ArrayList<>();
    private Long wordsCount;

    public TypingTest(String text) {
        this(text, "Key2Glory", TypingTestType.CUSTOM);
    }
    public TypingTest(String text, TypingTestType type) {
        this(text, "Key2Glory", type);
    }
    public TypingTest(String text, String createdBy, TypingTestType type) {
        setText(text);
        this.createdBy = createdBy;
        this.type = type;
    }
    public TypingTest() {
        this.text = null;
    }

    /**
     * Collapses whitespace and swaps typographic punctuation for the plain keyboard
     * equivalents, so every character in a test can actually be typed.
     */
    public static String cleanText(String text) {
        if (text == null) {
            return null;
        }
        return normalizeCharacters(text).replaceAll("\\s+", " ").trim();
    }

    /** Length-preserving replacements only, so it is safe to apply to tests that already have scores. */
    public static String normalizeCharacters(String text) {
        return text
                .replaceAll("[\\u2018\\u2019\\u201A\\u201B\\u2032]", "'")
                .replaceAll("[\\u201C\\u201D\\u201E\\u201F\\u2033]", "\"")
                .replaceAll("[\\u2010-\\u2015\\u2212]", "-")
                .replaceAll("[\\u00A0\\u2007\\u202F]", " ");
    }

    private static Long calcWordsCount(String text) {
        return (long) text.split(" ").length;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = cleanText(text);
        if (this.text != null) {
            this.length = (long) this.text.length();
            this.wordsCount = calcWordsCount(this.text);
        }
    }

    /** Applies {@link #normalizeCharacters} and fills in counts missing from older rows. Returns true if anything changed. */
    public boolean normalizeStoredText() {
        String normalized = normalizeCharacters(text);
        boolean changed = !normalized.equals(text) || length == null || wordsCount == null;
        this.text = normalized;
        this.length = (long) text.length();
        this.wordsCount = calcWordsCount(text);
        return changed;
    }

    public List<Score> getScores() {
        return scores;
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

    public int getRatingsCount() {
        return ratingsCount;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public Long getScoresCount() {
        return scoresCount == null ? 0L : scoresCount;
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

    public Long getWordsCount() {
        return wordsCount;
    }

    public Long getLength() {
        return length;
    }

    public TypingTestType getType() {
        return type;
    }

    public void setType(TypingTestType type) {
        this.type = type;
    }

    public boolean textLengthValid() {
        if (text == null) {
            return false;
        }
        int length = text.length();
        return length <= MAX_LENGTH && length >= MIN_LENGTH;
    }
}

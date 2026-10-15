package com.jacobferrell.Key2Glory.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreTest {
    private static final String TEXT = "one two three four five six seven eight nine ten"; // 10 words, 48 chars

    private static TestSession session(List<Character> missed) {
        var session = new TestSession();
        session.setTypingTest(new TypingTest(TEXT, TypingTestType.GENERAL));
        session.setUsername("alice");
        session.setMissedCharacters(missed);
        return session;
    }

    @Test
    void calculatesWpmAccuracyAndOverallScore() {
        var score = new Score(session(List.of('a', 'b', 'c')), 6_000); // 10 words in 0.1 minutes
        assertEquals(100.0, score.getWpm());
        assertEquals(93.75, score.getAccuracy());
        assertEquals(93.75, score.getOverallScore());
        assertEquals(6_000, score.getTime());
    }

    @Test
    void accuracyNeverGoesNegative() {
        var score = new Score(session(Collections.nCopies(TEXT.length() * 2, 'x')), 60_000);
        assertEquals(0.0, score.getAccuracy());
        assertEquals(0.0, score.getOverallScore());
    }

    @Test
    void handlesNullMissedCharacters() {
        var score = new Score(session(null), 60_000);
        assertEquals(100.0, score.getAccuracy());
    }
}

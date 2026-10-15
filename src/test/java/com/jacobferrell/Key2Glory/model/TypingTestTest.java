package com.jacobferrell.Key2Glory.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypingTestTest {

    @Test
    void cleanTextCollapsesWhitespaceAndNewlines() {
        assertEquals("one two three", TypingTest.cleanText("  one\ntwo \t  three \n"));
    }

    @Test
    void cleanTextReplacesTypographicPunctuation() {
        assertEquals("we'll say \"hi\" - ok", TypingTest.cleanText("we’ll say “hi” – ok"));
    }

    @Test
    void settingTextComputesCounts() {
        // Request bodies are deserialized through the no-arg constructor + setters
        var test = new TypingTest();
        test.setText("a b  c\nd");
        assertEquals("a b c d", test.getText());
        assertEquals(4L, test.getWordsCount());
        assertEquals(7L, test.getLength());
    }

    @Test
    void normalizeStoredTextOnlyMakesLengthPreservingChanges() {
        var test = new TypingTest("let’s go somewhere fun today with friends", TypingTestType.GENERAL);
        assertFalse(test.normalizeStoredText());
        assertEquals("let's go somewhere fun today with friends", test.getText());
    }

    @Test
    void textLengthValidation() {
        assertFalse(new TypingTest("too short", TypingTestType.GENERAL).textLengthValid());
        assertTrue(new TypingTest("x".repeat(TypingTest.MIN_LENGTH), TypingTestType.GENERAL).textLengthValid());
        assertFalse(new TypingTest("x".repeat(TypingTest.MAX_LENGTH + 1), TypingTestType.GENERAL).textLengthValid());
        assertFalse(new TypingTest().textLengthValid());
    }
}

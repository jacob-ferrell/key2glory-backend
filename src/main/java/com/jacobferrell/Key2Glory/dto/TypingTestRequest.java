package com.jacobferrell.Key2Glory.dto;

import com.jacobferrell.Key2Glory.model.TypingTestType;

/** Request body for creating or updating a typing test; only these fields are client-controlled. */
public record TypingTestRequest(String text, TypingTestType type) { }

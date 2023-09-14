package com.jacobferrell.Key2Glory.model;

public class ErrorMessage {

    private final String message;

    // Constructor
    public ErrorMessage(String message) {
        this.message = message;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Factory method to create ErrorMessage
    public static ErrorMessage from(String message) {
        return new ErrorMessage(message);
    }

    // You can also override toString() if needed
    @Override
    public String toString() {
        return "ErrorMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}


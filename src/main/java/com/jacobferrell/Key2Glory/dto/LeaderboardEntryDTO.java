package com.jacobferrell.Key2Glory.dto;

public class LeaderboardEntryDTO {
    private final String username;
    private final double averageWpm;
    private final double bestWpm;
    private final double averageAccuracy;
    private final long testsCompleted;

    // Instantiated from a JPQL constructor expression, so the aggregate types are boxed
    public LeaderboardEntryDTO(String username, Double averageWpm, Double bestWpm, Double averageAccuracy, Long testsCompleted) {
        this.username = username;
        this.averageWpm = round(averageWpm);
        this.bestWpm = round(bestWpm);
        this.averageAccuracy = round(averageAccuracy);
        this.testsCompleted = testsCompleted == null ? 0 : testsCompleted;
    }

    private static double round(Double value) {
        return value == null ? 0 : Math.round(value * 100) / 100.0;
    }

    public String getUsername() {
        return username;
    }

    public double getAverageWpm() {
        return averageWpm;
    }

    public double getBestWpm() {
        return bestWpm;
    }

    public double getAverageAccuracy() {
        return averageAccuracy;
    }

    public long getTestsCompleted() {
        return testsCompleted;
    }
}

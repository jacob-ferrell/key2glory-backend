package com.jacobferrell.Key2Glory.model;

public class GeneralStats {
    private double averageWPM;
    private double highestWPM;
    private double averageAccuracy;
    private int testsCompleted;
    private int testsContributed;

    public GeneralStats(double averageWPM, double highestWPM, double averageAccuracy, int testsCompleted, int testsContributed) {
        this.averageWPM = averageWPM;
        this.highestWPM = highestWPM;
        this.averageAccuracy = averageAccuracy;
        this.testsCompleted = testsCompleted;
        this.testsContributed = testsContributed;
    }

    public double getAverageWPM() {
        return averageWPM;
    }

    public void setAverageWPM(double averageWPM) {
        this.averageWPM = averageWPM;
    }

    public double getHighestWPM() {
        return highestWPM;
    }

    public void setHighestWPM(double highestWPM) {
        this.highestWPM = highestWPM;
    }

    public double getAverageAccuracy() {
        return averageAccuracy;
    }

    public void setAverageAccuracy(double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    public int getTestsCompleted() {
        return testsCompleted;
    }

    public void setTestsCompleted(int testsCompleted) {
        this.testsCompleted = testsCompleted;
    }

    public int getTestsContributed() {
        return testsContributed;
    }

    public void setTestsContributed(int testsContributed) {
        this.testsContributed = testsContributed;
    }
}

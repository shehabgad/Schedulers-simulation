package com.company;

public interface Scheduler {
    void runScheduler();

    int calculateWeight(Process process);
    double calculateAvgWeight();

    int calculateTurnAroundTime(Process process);
    double calculateAvgTurnAroundTime();

    void printOutput();
}

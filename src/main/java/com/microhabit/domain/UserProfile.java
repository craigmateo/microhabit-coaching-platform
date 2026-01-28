package com.microhabit.domain;

public class UserProfile {
    private final int userId;
    private final String fitnessLevel;      // BEGINNER/INTERMEDIATE/ADVANCED
    private final String timeAvailability;  // 5_MIN/10_MIN/15_MIN/20_MIN

    public UserProfile(int userId, String fitnessLevel, String timeAvailability) {
        this.userId = userId;
        this.fitnessLevel = fitnessLevel;
        this.timeAvailability = timeAvailability;
    }

    public int getUserId() { return userId; }
    public String getFitnessLevel() { return fitnessLevel; }
    public String getTimeAvailability() { return timeAvailability; }
}

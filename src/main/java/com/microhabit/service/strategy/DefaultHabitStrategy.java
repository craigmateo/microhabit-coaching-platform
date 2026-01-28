package com.microhabit.service.strategy;

public class DefaultHabitStrategy implements HabitGenerationStrategy {

    @Override
    public String pickDifficulty(String fitnessLevel) {
        // fitness_level already matches enum strings in DB
        return (fitnessLevel == null || fitnessLevel.isBlank()) ? "BEGINNER" : fitnessLevel;
    }

    @Override
    public int pickMinutes(String timeAvailability) {
        if (timeAvailability == null) return 10;
        switch (timeAvailability) {
            case "5_MIN": return 5;
            case "10_MIN": return 10;
            case "15_MIN": return 15;
            case "20_MIN": return 20;
            default: return 10;
        }
    }
}

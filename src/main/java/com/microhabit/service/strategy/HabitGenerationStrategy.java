package com.microhabit.service.strategy;

public interface HabitGenerationStrategy {
    String pickDifficulty(String fitnessLevel);
    int pickMinutes(String timeAvailability);
}

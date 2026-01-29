package com.microhabit.domain;

public class MicroHabit {
    private final int habitId;
    private final String title;
    private final String description;
    private final String difficulty;
    private final int minutes;

    private MicroHabit(Builder b) {
        this.habitId = b.habitId;
        this.title = b.title;
        this.description = b.description;
        this.difficulty = b.difficulty;
        this.minutes = b.minutes;
    }

    public int getHabitId() { return habitId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public int getMinutes() { return minutes; }

    public static class Builder {
        private int habitId;
        private String title;
        private String description;
        private String difficulty;
        private int minutes;

        public Builder habitId(int habitId) { this.habitId = habitId; return this; } 
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder minutes(int minutes) { this.minutes = minutes; return this; }

        public MicroHabit build() { return new MicroHabit(this); }
    }
}

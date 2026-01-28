package com.microhabit.domain;

public class MicroHabit {

    private final int id;
    private final String title;
    private final String description;
    private final int minutes;

    private MicroHabit(Builder b) {
        this.id = b.id;
        this.title = b.title;
        this.description = b.description;
        this.minutes = b.minutes;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public static class Builder {
        private int id;
        private String title;
        private String description;
        private int minutes;

        public Builder id(int id) { this.id = id; return this; }
        public Builder title(String t) { this.title = t; return this; }
        public Builder description(String d) { this.description = d; return this; }
        public Builder minutes(int m) { this.minutes = m; return this; }

        public MicroHabit build() {
            return new MicroHabit(this);
        }
    }
}

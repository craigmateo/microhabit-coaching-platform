-- Micro-Habit Gym Coach - Project 3 schema.sql (MySQL)
-- Creates: users, micro_habits, daily_assignments, streaks, habit_completions
-- Includes: completed_at column (fixes "Unknown column 'completed_at'")

CREATE DATABASE IF NOT EXISTS microhabit
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE microhabit;

-- Drop in FK-safe order (optional, but helps reset)
DROP TABLE IF EXISTS habit_completions;
DROP TABLE IF EXISTS daily_assignments;
DROP TABLE IF EXISTS streaks;
DROP TABLE IF EXISTS micro_habits;
DROP TABLE IF EXISTS users;

-- 1) USERS
CREATE TABLE users (
  user_id           INT AUTO_INCREMENT PRIMARY KEY,
  full_name         VARCHAR(100) NOT NULL,
  email             VARCHAR(150) NOT NULL UNIQUE,
  password_hash     VARCHAR(255) NOT NULL,
  password_salt     VARCHAR(255) NOT NULL,

  -- Profile fields (optional for now)
  fitness_level     ENUM('BEGINNER','INTERMEDIATE','ADVANCED') DEFAULT 'BEGINNER',
  time_availability ENUM('5_MIN','10_MIN','15_MIN','20_MIN') DEFAULT '10_MIN',

  created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2) MICRO_HABITS
CREATE TABLE micro_habits (
  habit_id     INT AUTO_INCREMENT PRIMARY KEY,
  title        VARCHAR(120) NOT NULL,
  description  VARCHAR(500) NOT NULL,
  difficulty   ENUM('BEGINNER','INTERMEDIATE','ADVANCED') NOT NULL,
  minutes      INT NOT NULL,
  is_active    BOOLEAN NOT NULL DEFAULT TRUE,

  CONSTRAINT chk_minutes CHECK (minutes BETWEEN 1 AND 60)
);

-- 3) DAILY_ASSIGNMENTS
CREATE TABLE daily_assignments (
  assignment_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id       INT NOT NULL,
  habit_id      INT NOT NULL,

  assign_date   DATE NOT NULL,
  status        ENUM('ASSIGNED','COMPLETED') NOT NULL DEFAULT 'ASSIGNED',
  completed_at  DATETIME NULL,

  CONSTRAINT uq_user_date UNIQUE (user_id, assign_date),

  CONSTRAINT fk_da_user FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE,

  CONSTRAINT fk_da_habit FOREIGN KEY (habit_id)
    REFERENCES micro_habits(habit_id)
    ON DELETE RESTRICT
);

CREATE INDEX idx_da_user  ON daily_assignments(user_id);
CREATE INDEX idx_da_date  ON daily_assignments(assign_date);
CREATE INDEX idx_da_habit ON daily_assignments(habit_id);

-- 4) STREAKS (optional persistence; your app may compute dynamically)
CREATE TABLE streaks (
  user_id             INT PRIMARY KEY,
  current_streak      INT NOT NULL DEFAULT 0,
  longest_streak      INT NOT NULL DEFAULT 0,
  last_completed_date DATE NULL,

  CONSTRAINT fk_streak_user FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE
);

-- 5) HABIT_COMPLETIONS (simple audit / streak computation)
CREATE TABLE habit_completions (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  user_id         INT NOT NULL,
  completion_date DATE NOT NULL,

  CONSTRAINT uq_completion UNIQUE (user_id, completion_date),

  CONSTRAINT fk_hc_user FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE
);

CREATE INDEX idx_hc_user ON habit_completions(user_id);
CREATE INDEX idx_hc_date ON habit_completions(completion_date);

-- Seed habits
INSERT INTO micro_habits (title, description, difficulty, minutes, is_active) VALUES
('5-minute Walk', 'Walk at an easy pace for 5 minutes.', 'BEGINNER', 5, TRUE),
('10 Push-ups', 'Do 10 push-ups with good form.', 'BEGINNER', 5, TRUE),
('Stretch Routine', 'Stretch full body for 10 minutes.', 'BEGINNER', 10, TRUE),
('Plank 30 sec', 'Hold a plank for 30 seconds.', 'INTERMEDIATE', 5, TRUE),
('Bodyweight Squats', 'Do 20 bodyweight squats.', 'INTERMEDIATE', 10, TRUE),
('Jog 10 minutes', 'Jog lightly for 10 minutes.', 'ADVANCED', 10, TRUE);

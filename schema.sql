CREATE DATABASE IF NOT EXISTS microhabit;
USE microhabit;

-- USERS
CREATE TABLE IF NOT EXISTS users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  password_salt VARCHAR(255) NOT NULL,
  fitness_level ENUM('BEGINNER','INTERMEDIATE','ADVANCED') DEFAULT 'BEGINNER',
  time_availability ENUM('5_MIN','10_MIN','15_MIN','20_MIN') DEFAULT '10_MIN',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- MICRO HABITS (templates)
CREATE TABLE IF NOT EXISTS micro_habits (
  habit_id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  description VARCHAR(500) NOT NULL,
  difficulty ENUM('BEGINNER','INTERMEDIATE','ADVANCED') NOT NULL,
  minutes INT NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- DAILY ASSIGNMENT (one habit per user per day)
CREATE TABLE IF NOT EXISTS daily_assignments (
  assignment_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  habit_id INT NOT NULL,
  assign_date DATE NOT NULL,
  status ENUM('ASSIGNED','COMPLETED') NOT NULL DEFAULT 'ASSIGNED',
  CONSTRAINT uq_user_day UNIQUE (user_id, assign_date),
  CONSTRAINT fk_da_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_da_habit FOREIGN KEY (habit_id) REFERENCES micro_habits(habit_id) ON DELETE RESTRICT
);

-- COMPLETIONS (audit log)
CREATE TABLE IF NOT EXISTS habit_completions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  completion_date DATE NOT NULL,
  CONSTRAINT uq_completion UNIQUE(user_id, completion_date),
  CONSTRAINT fk_completion_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Optional streak cache (not mandatory)
CREATE TABLE IF NOT EXISTS streaks (
  user_id INT PRIMARY KEY,
  current_streak INT NOT NULL DEFAULT 0,
  longest_streak INT NOT NULL DEFAULT 0,
  last_completed_date DATE NULL,
  CONSTRAINT fk_streak_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Seed habits
INSERT INTO micro_habits (title, description, difficulty, minutes, is_active) VALUES
('5-minute Walk', 'Walk at an easy pace for 5 minutes.', 'BEGINNER', 5, TRUE),
('10 Push-ups', 'Do 10 push-ups with good form.', 'BEGINNER', 5, TRUE),
('Stretch Routine', 'Stretch full body for 10 minutes.', 'BEGINNER', 10, TRUE),
('Plank 30 sec', 'Hold a plank for 30 seconds.', 'INTERMEDIATE', 5, TRUE),
('Bodyweight Squats', 'Do 20 bodyweight squats.', 'INTERMEDIATE', 10, TRUE),
('Jog 10 minutes', 'Jog lightly for 10 minutes.', 'ADVANCED', 10, TRUE);

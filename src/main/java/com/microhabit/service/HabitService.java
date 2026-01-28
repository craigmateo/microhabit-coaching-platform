package com.microhabit.service;

import com.microhabit.dao.DaoFactory;
import com.microhabit.dao.HabitDAO;
import com.microhabit.domain.MicroHabit;
import com.microhabit.service.strategy.DefaultHabitStrategy;
import com.microhabit.service.strategy.HabitGenerationStrategy;

public class HabitService {

    private final HabitDAO habitDao = DaoFactory.habitDao();
    private final HabitGenerationStrategy strategy = new DefaultHabitStrategy();

    public MicroHabit generateForProfile(String fitnessLevel, String timeAvailability) throws Exception {
        String difficulty = strategy.pickDifficulty(fitnessLevel);
        int maxMinutes = strategy.pickMinutes(timeAvailability);

        MicroHabit habit = habitDao.getRandomHabit(difficulty, maxMinutes);

        // Fallback so dashboard never breaks if table is empty or filters are too strict
        if (habit == null) {
            habit = habitDao.getRandomHabit("BEGINNER", 60);
        }
        return habit;
    }
}

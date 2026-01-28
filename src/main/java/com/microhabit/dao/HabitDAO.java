package com.microhabit.dao;

import com.microhabit.domain.MicroHabit;

public interface HabitDAO {
    MicroHabit getRandomHabit(String difficulty, int maxMinutes) throws Exception;
}

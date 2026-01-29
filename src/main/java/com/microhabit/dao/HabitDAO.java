package com.microhabit.dao;

import com.microhabit.domain.MicroHabit;
import java.sql.SQLException;

public interface HabitDAO {
    MicroHabit getRandomActiveHabit(String difficulty, int maxMinutes) throws SQLException;
}

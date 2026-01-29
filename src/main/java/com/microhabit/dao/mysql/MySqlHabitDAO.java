package com.microhabit.dao.mysql;

import com.microhabit.dao.HabitDAO;
import com.microhabit.db.DbUtil;
import com.microhabit.domain.MicroHabit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MySqlHabitDAO implements HabitDAO {

    @Override
    public MicroHabit getRandomActiveHabit(String difficulty, int maxMinutes) throws SQLException {

        String sql =
            "SELECT habit_id, title, description, difficulty, minutes " +
            "FROM micro_habits " +
            "WHERE is_active=TRUE AND difficulty=? AND minutes<=?";

        List<MicroHabit> options = new ArrayList<>();

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, difficulty);
            ps.setInt(2, maxMinutes);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    options.add(new MicroHabit.Builder()
                            .habitId(rs.getInt("habit_id"))       
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .difficulty(rs.getString("difficulty"))
                            .minutes(rs.getInt("minutes"))
                            .build());
                }
            }
        }

        if (options.isEmpty()) return null;
        return options.get(new Random().nextInt(options.size()));
    }
}

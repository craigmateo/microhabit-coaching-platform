package com.microhabit.dao.mysql;

import com.microhabit.dao.HabitDAO;
import com.microhabit.db.DbUtil;
import com.microhabit.domain.MicroHabit;

import java.sql.*;

public class MySqlHabitDAO implements HabitDAO {

    @Override
    public MicroHabit getRandomHabit(String difficulty, int maxMinutes) throws Exception {
        // Random row selection using ORDER BY RAND() is fine for small MVP tables.
        // (For large tables you'd do a different approach.)
        String sql =
            "SELECT habit_id, title, description, difficulty, minutes " +
            "FROM micro_habits " +
            "WHERE is_active=TRUE AND difficulty=? AND minutes<=? " +
            "ORDER BY RAND() LIMIT 1";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, difficulty);
            ps.setInt(2, maxMinutes);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new MicroHabit.Builder()
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .minutes(rs.getInt("minutes"))
                    .build();
            }
        }
    }
}

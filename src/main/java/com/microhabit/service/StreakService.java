package com.microhabit.service;

import com.microhabit.db.DbUtil;

import java.sql.*;
import java.time.LocalDate;

public class StreakService {

    public int currentStreak(int userId) throws Exception {
        // Count consecutive days ending today or yesterday depending on completion.
        // Simple approach: walk backwards day-by-day until missing.
        LocalDate d = LocalDate.now();
        int streak = 0;

        while (hasCompletion(userId, d)) {
            streak++;
            d = d.minusDays(1);
        }
        return streak;
    }

    private boolean hasCompletion(int userId, LocalDate date) throws Exception {
        String sql = "SELECT 1 FROM habit_completions WHERE user_id=? AND completion_date=?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}

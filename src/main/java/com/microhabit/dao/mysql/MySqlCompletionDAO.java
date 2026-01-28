package com.microhabit.dao.mysql;

import com.microhabit.dao.CompletionDAO;
import com.microhabit.db.DbUtil;

import java.sql.*;
import java.time.LocalDate;

public class MySqlCompletionDAO implements CompletionDAO {

    @Override
    public boolean hasCompleted(int userId, LocalDate date) throws Exception {
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

    @Override
    public void markCompleted(int userId, LocalDate date) throws Exception {
        String sql = "INSERT INTO habit_completions(user_id, completion_date) VALUES(?, ?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        }
    }
}

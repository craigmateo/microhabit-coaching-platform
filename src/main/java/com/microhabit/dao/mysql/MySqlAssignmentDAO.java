package com.microhabit.dao.mysql;

import com.microhabit.dao.AssignmentDAO;
import com.microhabit.db.DbUtil;
import com.microhabit.domain.MicroHabit;

import java.sql.*;
import java.time.LocalDate;

public class MySqlAssignmentDAO implements AssignmentDAO {

    @Override
    public boolean hasAssignment(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT 1 FROM daily_assignments WHERE user_id=? AND assign_date=?";
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
    public MicroHabit getAssignmentHabit(int userId, LocalDate date) throws SQLException {
        String sql =
            "SELECT h.habit_id, h.title, h.description, h.difficulty, h.minutes, a.status " +
            "FROM daily_assignments a " +
            "JOIN micro_habits h ON h.habit_id=a.habit_id " +
            "WHERE a.user_id=? AND a.assign_date=?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new MicroHabit.Builder()
                        .habitId(rs.getInt("habit_id"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .difficulty(rs.getString("difficulty"))
                        .minutes(rs.getInt("minutes"))
                        .build();
            }
        }
    }

    @Override
    public void createAssignment(int userId, int habitId, LocalDate date) throws SQLException {
        String sql =
            "INSERT INTO daily_assignments(user_id, habit_id, assign_date, status) " +
            "VALUES(?, ?, ?, 'ASSIGNED')";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, habitId);
            ps.setDate(3, Date.valueOf(date));
            ps.executeUpdate();
        }
    }

    @Override
    public boolean isCompleted(int userId, LocalDate date) throws SQLException {
        String sql =
            "SELECT status FROM daily_assignments WHERE user_id=? AND assign_date=?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return "COMPLETED".equalsIgnoreCase(rs.getString("status"));
            }
        }
    }

    @Override
    public void markCompleted(int userId, LocalDate date) throws SQLException {
        String sql =
            "UPDATE daily_assignments " +
            "SET status='COMPLETED', completed_at=NOW() " +
            "WHERE user_id=? AND assign_date=?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        }
    }
}

package com.microhabit.dao.mysql;

import com.microhabit.dao.UserDAO;
import com.microhabit.db.DbUtil;

import java.sql.*;

public class MySqlUserDAO implements UserDAO {

    @Override
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email=?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int createUser(String fullName, String email, String passwordHash, String salt) throws SQLException {
        String sql = "INSERT INTO users(full_name, email, password_hash, password_salt) VALUES(?,?,?,?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, salt);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                throw new SQLException("No generated key returned for user insert");
            }
        }
    }

    @Override
    public UserRow findByEmailAndPassword(String email, String passwordHash) throws SQLException {
        String sql = "SELECT user_id, full_name FROM users WHERE email=? AND password_hash=?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new UserRow(rs.getInt("user_id"), rs.getString("full_name"));
            }
        }
    }
    @Override
public ProfileRow getProfile(int userId) throws SQLException {
    String sql = "SELECT fitness_level, time_availability FROM users WHERE user_id=?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return new ProfileRow("BEGINNER", "10_MIN");
            return new ProfileRow(rs.getString("fitness_level"), rs.getString("time_availability"));
        }
    }
}

@Override
public void updateProfile(int userId, String fitnessLevel, String timeAvailability) throws SQLException {
    String sql = "UPDATE users SET fitness_level=?, time_availability=? WHERE user_id=?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, fitnessLevel);
        ps.setString(2, timeAvailability);
        ps.setInt(3, userId);
        ps.executeUpdate();
    }
}

}

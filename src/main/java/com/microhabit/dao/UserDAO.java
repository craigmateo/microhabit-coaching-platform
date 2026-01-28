package com.microhabit.dao;

import java.sql.SQLException;

public interface UserDAO {
    boolean emailExists(String email) throws SQLException;
    int createUser(String fullName, String email, String passwordHash, String salt) throws SQLException;

    UserRow findByEmailAndPassword(String email, String passwordHash) throws SQLException;

    // PR0003 – Profile fields used for habit generation
    ProfileRow getProfile(int userId) throws SQLException;
    void updateProfile(int userId, String fitnessLevel, String timeAvailability) throws SQLException;

    class UserRow {
        public final int userId;
        public final String fullName;
        public UserRow(int userId, String fullName) {
            this.userId = userId;
            this.fullName = fullName;
        }
    }

    class ProfileRow {
        public final String fitnessLevel;      // BEGINNER/INTERMEDIATE/ADVANCED
        public final String timeAvailability;  // 5_MIN/10_MIN/15_MIN/20_MIN

        public ProfileRow(String fitnessLevel, String timeAvailability) {
            this.fitnessLevel = fitnessLevel;
            this.timeAvailability = timeAvailability;
        }
    }
}

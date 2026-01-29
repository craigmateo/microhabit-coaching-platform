package com.microhabit.dao;

import com.microhabit.domain.MicroHabit;
import java.sql.SQLException;
import java.time.LocalDate;

public interface AssignmentDAO {
    boolean hasAssignment(int userId, LocalDate date) throws SQLException;
    MicroHabit getAssignmentHabit(int userId, LocalDate date) throws SQLException;
    void createAssignment(int userId, int habitId, LocalDate date) throws SQLException;

    boolean isCompleted(int userId, LocalDate date) throws SQLException;
    void markCompleted(int userId, LocalDate date) throws SQLException;
}

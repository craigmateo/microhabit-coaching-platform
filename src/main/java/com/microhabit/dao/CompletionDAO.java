package com.microhabit.dao;

import java.time.LocalDate;

public interface CompletionDAO {
    boolean hasCompleted(int userId, LocalDate date) throws Exception;
    void markCompleted(int userId, LocalDate date) throws Exception;
}

package com.microhabit.dao;

import com.microhabit.dao.mysql.*;

public final class DaoFactory {

    private DaoFactory() {}

    public static UserDAO userDao() {
        return new MySqlUserDAO();
    }

    public static HabitDAO habitDao() {
        return new MySqlHabitDAO();
    }

    public static CompletionDAO completionDao() {
        return new MySqlCompletionDAO();
    }
}

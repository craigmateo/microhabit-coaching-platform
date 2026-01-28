package com.microhabit.db;

import javax.servlet.ServletContext;
import java.sql.*;

public final class DbUtil {
    private static String url;
    private static String user;
    private static String pass;
    private static boolean initialized = false;

    private DbUtil() {}

    public static synchronized void init(ServletContext ctx) {
        if (initialized) return;

        url = ctx.getInitParameter("DB_URL");
        user = ctx.getInitParameter("DB_USER");
        pass = ctx.getInitParameter("DB_PASS");

        if (url == null || user == null || pass == null) {
            throw new IllegalStateException("DB params missing in web.xml (DB_URL/DB_USER/DB_PASS)");
        }

        // Load driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found. Ensure mysql-connector-j is in WEB-INF/lib.", e);
        }

        // IMPORTANT: mark initialized BEFORE any metadata test that opens a connection
        initialized = true;

        // Optional: metadata logging (won't crash app if DB is unavailable)
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            DatabaseMetaData meta = c.getMetaData();
            System.out.println("DB: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("Driver: " + meta.getDriverName() + " " + meta.getDriverVersion());
            System.out.println("Supports transactions: " + meta.supportsTransactions());
        } catch (SQLException e) {
            System.out.println("DB metadata check failed (app can still run): " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) throw new IllegalStateException("DbUtil not initialized");
        return DriverManager.getConnection(url, user, pass);
    }
}

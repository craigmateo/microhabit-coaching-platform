package com.microhabit.web;

import com.microhabit.db.DbUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private ExecutorService executor;

    @Override
    public void init() {
        // Ensure DbUtil has DB_URL, DB_USER, DB_PASS (usually via web.xml context params)
        DbUtil.init(getServletContext());
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void destroy() {
        if (executor != null) executor.shutdown();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession s = req.getSession(false);
        Integer userId = (s == null) ? null : (Integer) s.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        // 1) Pick a habit (simple MVP: first active habit)
        String title = "5-minute Walk";
        String desc = "Walk at an easy pace for 5 minutes.";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT title, description " +
                     "FROM micro_habits " +
                     "WHERE is_active=TRUE " +
                     "ORDER BY habit_id " +
                     "LIMIT 1");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                title = rs.getString("title");
                desc = rs.getString("description");
            }
        } catch (SQLException e) {
            log("Dashboard habit fetch failed", e);
        }

        // 2) Compute streak from habit_completions
        int currentStreak = computeCurrentStreak(userId);

        // 3) Read longest streak from DB (optional but nice)
        int longestStreak = currentStreak;
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT longest_streak FROM streaks WHERE user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    longestStreak = rs.getInt("longest_streak");
                }
            }
        } catch (SQLException e) {
            log("Longest streak fetch failed", e);
        }

        // 4) Completed today?
        boolean completedToday = hasCompletedToday(userId);

        req.setAttribute("todayTitle", title);
        req.setAttribute("todayDescription", desc);
        req.setAttribute("currentStreak", currentStreak);
        req.setAttribute("longestStreak", longestStreak);
        req.setAttribute("completedToday", completedToday);

        // Flash message (if any)
        String flash = (String) s.getAttribute("flashMsg");
        if (flash != null) {
            req.setAttribute("flashMsg", flash);
            s.removeAttribute("flashMsg");
        }

        req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession s = req.getSession(false);
        Integer userId = (s == null) ? null : (Integer) s.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String action = req.getParameter("action");
        if (!"complete".equals(action)) {
            resp.sendRedirect("message.jsp?msg=Unknown dashboard action");
            return;
        }

        LocalDate today = LocalDate.now();

        // 1) Insert completion event (idempotent for the day)
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO habit_completions(user_id, completion_date) VALUES(?, ?) " +
                     "ON DUPLICATE KEY UPDATE completion_date = completion_date")) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(today));
            ps.executeUpdate();
        } catch (SQLException e) {
            log("Completion insert failed", e);
            resp.sendRedirect("message.jsp?msg=Error saving completion");
            return;
        }

        // 2) Update streaks table (persist current + longest)
        int current = computeCurrentStreak(userId);

        try (Connection c2 = DbUtil.getConnection();
             PreparedStatement ps2 = c2.prepareStatement(
                     "INSERT INTO streaks (user_id, current_streak, longest_streak, last_completed_date) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "  current_streak = VALUES(current_streak), " +
                     "  longest_streak = GREATEST(longest_streak, VALUES(longest_streak)), " +
                     "  last_completed_date = VALUES(last_completed_date)"
             )) {

            ps2.setInt(1, userId);
            ps2.setInt(2, current);
            ps2.setInt(3, current);
            ps2.setDate(4, Date.valueOf(today));
            ps2.executeUpdate();

        } catch (SQLException e) {
            log("Streak update failed", e);
            resp.sendRedirect("message.jsp?msg=Error updating streak");
            return;
        }

        // 3) Multithreading evidence (non-critical background task)
        executor.submit(() ->
                System.out.println("Async: user " + userId + " completed habit on " + today));

        // 4) Redirect back to dashboard (clean UX)
        s.setAttribute("flashMsg", "Habit completed!");
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private boolean hasCompletedToday(int userId) {
        LocalDate today = LocalDate.now();
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT 1 FROM habit_completions WHERE user_id=? AND completion_date=?")) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(today));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log("Completed-today check failed", e);
            return false;
        }
    }

    private int computeCurrentStreak(int userId) {
        // MVP: count consecutive days ending today
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT completion_date " +
                     "FROM habit_completions " +
                     "WHERE user_id=? " +
                     "ORDER BY completion_date DESC")) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                LocalDate expected = LocalDate.now();
                int streak = 0;

                while (rs.next()) {
                    LocalDate d = rs.getDate(1).toLocalDate();
                    if (d.equals(expected)) {
                        streak++;
                        expected = expected.minusDays(1);
                    } else if (d.isBefore(expected)) {
                        break;
                    }
                }
                return streak;
            }
        } catch (SQLException e) {
            log("Streak compute failed", e);
            return 0;
        }
    }
}

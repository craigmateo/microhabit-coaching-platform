package com.microhabit.web;

import com.microhabit.dao.AssignmentDAO;
import com.microhabit.dao.HabitDAO;
import com.microhabit.dao.mysql.MySqlAssignmentDAO;
import com.microhabit.dao.mysql.MySqlHabitDAO;
import com.microhabit.db.DbUtil;
import com.microhabit.domain.MicroHabit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private ExecutorService executor;
    private HabitDAO habitDao;
    private AssignmentDAO assignmentDao;

    @Override
    public void init() {
        DbUtil.init(getServletContext());
        executor = Executors.newFixedThreadPool(2);

        habitDao = new MySqlHabitDAO();
        assignmentDao = new MySqlAssignmentDAO();
    }

    @Override
    public void destroy() {
        if (executor != null) executor.shutdown();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // MVP defaults (later: load from profile)
        String difficulty = "BEGINNER";
        int maxMinutes = 10;

        HttpSession s = req.getSession(false);
        Integer userId = (s == null) ? null : (Integer) s.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        LocalDate today = LocalDate.now();

        try {
            // 1) Ensure today assignment exists
            if (!assignmentDao.hasAssignment(userId, today)) {
                MicroHabit picked = habitDao.getRandomActiveHabit(difficulty, maxMinutes);
                if (picked != null) {
                    // IMPORTANT: picked must include the DB habit_id
                    assignmentDao.createAssignment(userId, picked.getHabitId(), today);
                }
            }

            // 2) Load today's assigned habit from DB
            MicroHabit todayHabit = fetchAssignedHabit(userId, today);

            // 3) Tomorrow preview (not stored; just show a random habit)
            MicroHabit tomorrowHabit = habitDao.getRandomActiveHabit(difficulty, maxMinutes);

            // 4) Completion + streak
            boolean completedToday = assignmentDao.isCompleted(userId, today);
            int currentStreak = computeCurrentStreakFromAssignments(userId);

            // 5) Send to view
            req.setAttribute("todayTitle", todayHabit == null ? "No habit found" : todayHabit.getTitle());
            req.setAttribute("todayDescription", todayHabit == null ? "" : todayHabit.getDescription());

            req.setAttribute("tomorrowTitle", tomorrowHabit == null ? "Preview unavailable" : tomorrowHabit.getTitle());
            req.setAttribute("tomorrowDescription", tomorrowHabit == null ? "" : tomorrowHabit.getDescription());

            req.setAttribute("completedToday", completedToday);
            req.setAttribute("currentStreak", currentStreak);
            req.setAttribute("longestStreak", currentStreak); // MVP placeholder

            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);

        } catch (SQLException e) {
            log("Dashboard load failed", e);
            forwardMessage(req, resp, "Server error", "dashboard");
        }
    }

    @Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    HttpSession s = req.getSession(false);
    Integer userId = (s == null) ? null : (Integer) s.getAttribute("userId");
    if (userId == null) {
        resp.sendRedirect(req.getContextPath() + "/login");
        return;
    }

    String action = req.getParameter("action");
    if (!"complete".equals(action)) {
        forwardMessage(req, resp, "Unknown action", "dashboard");
        return;
    }

    LocalDate today = LocalDate.now();

    try {
        assignmentDao.markCompleted(userId, today);

        executor.submit(() ->
                System.out.println("Async: completion saved for user " + userId + " on " + today)
        );

        // SHOW message view + Back to Dashboard
        forwardMessage(req, resp, "Habit completed!", "dashboard");

    } catch (SQLException e) {
        log("Completion failed", e);
        forwardMessage(req, resp, "Error saving completion", "dashboard");
    }
}


    private int computeCurrentStreakFromAssignments(int userId) throws SQLException {
        LocalDate expected = LocalDate.now();
        int streak = 0;

        while (assignmentDao.isCompleted(userId, expected)) {
            streak++;
            expected = expected.minusDays(1);
        }

        return streak;
    }

    private MicroHabit fetchAssignedHabit(int userId, LocalDate date) throws SQLException {
        String sql =
                "SELECT h.habit_id, h.title, h.description, h.difficulty, h.minutes " +
                "FROM daily_assignments a " +
                "JOIN micro_habits h ON a.habit_id = h.habit_id " +
                "WHERE a.user_id=? AND a.assign_date=? " +
                "LIMIT 1";

        try (var c = DbUtil.getConnection();
             var ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));

            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                // Build only what your Builder definitely supports
                MicroHabit habit = new MicroHabit.Builder()
                        .habitId(rs.getInt("habit_id"))      // <-- requires Builder.habitId(int)
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .minutes(rs.getInt("minutes"))
                        .build();

                return habit;
            }
        }
    }

    private void forwardMessage(HttpServletRequest req, HttpServletResponse resp, String msg, String back)
            throws ServletException, IOException {
        req.setAttribute("msg", msg);
        req.setAttribute("back", back); // "dashboard", "login", "register", "home"
        req.getRequestDispatcher("/WEB-INF/views/message.jsp").forward(req, resp);
    }
}

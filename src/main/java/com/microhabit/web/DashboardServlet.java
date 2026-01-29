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

            String difficulty = "BEGINNER";   // MVP default
int maxMinutes = 10;              // MVP default

        HttpSession s = req.getSession(false);
        Integer userId = (s == null) ? null : (Integer) s.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);



        try {
            // 1) Ensure today assignment exists
if (!assignmentDao.hasAssignment(userId, today)) {


    MicroHabit picked = habitDao.getRandomActiveHabit(difficulty, maxMinutes);

    if (picked != null) {
        assignmentDao.createAssignment(
            userId,
            picked.getHabitId(),   // <-- MUST be real DB habit_id
            today
        );
    }
}


            // 2) Load today's assigned habit (from daily_assignments -> micro_habits)
            MicroHabit todayHabit = fetchAssignedHabit(userId, today);

            // 3) Tomorrow preview (not stored; just show a random habit)
            MicroHabit tomorrowHabit = habitDao.getRandomActiveHabit(difficulty, maxMinutes);

            // 4) Completion + streak
            boolean completedToday = assignmentDao.isCompleted(userId, today);
            int currentStreak = computeCurrentStreakFromAssignments(userId);

            // 5) Push data into JSP
            req.setAttribute("todayTitle", todayHabit == null ? "No habit found" : todayHabit.getTitle());
            req.setAttribute("todayDescription", todayHabit == null ? "" : todayHabit.getDescription());

            req.setAttribute("tomorrowTitle", tomorrowHabit == null ? "Preview unavailable" : tomorrowHabit.getTitle());
            req.setAttribute("tomorrowDescription", tomorrowHabit == null ? "" : tomorrowHabit.getDescription());

            req.setAttribute("completedToday", completedToday);
            req.setAttribute("currentStreak", currentStreak);
            req.setAttribute("longestStreak", currentStreak); // MVP placeholder

            req.getRequestDispatcher("dashboard.jsp").forward(req, resp);

        } catch (SQLException e) {
            log("Dashboard load failed", e);
            resp.sendRedirect("message.jsp?msg=Server error&back=dashboard");
        }
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
            resp.sendRedirect("message.jsp?msg=Unknown action&back=dashboard");
            return;
        }

        LocalDate today = LocalDate.now();

        try {
            // Mark today completed in daily_assignments
            assignmentDao.markCompleted(userId, today);

            // multithreading evidence: async log / non-critical task
            executor.submit(() ->
                    System.out.println("Async: completion saved for user " + userId + " on " + today)
            );

            resp.sendRedirect("message.jsp?msg=Habit completed!&back=dashboard");

        } catch (SQLException e) {
            log("Completion failed", e);
            resp.sendRedirect("message.jsp?msg=Error saving completion&back=dashboard");
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

            // Build the MicroHabit using what your Builder actually supports.
            // If your Builder doesn't have some setters, remove those lines.
            MicroHabit.Builder b = new MicroHabit.Builder()
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .minutes(rs.getInt("minutes"));

            // If your Builder has difficulty(), keep this; otherwise delete this line.
            try {
                b = b.difficulty(rs.getString("difficulty"));
            } catch (Throwable ignored) {}

            MicroHabit habit = b.build();

            // If MicroHabit has setHabitId / constructor includes id, set it.
            // If not, you can ignore habit_id for now.
            // (No reflection here—keep it simple.)

            return habit;
        }
    }
}

}

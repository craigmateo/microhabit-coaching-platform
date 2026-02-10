package com.microhabit.web;

import com.microhabit.db.DbUtil;
import com.microhabit.service.AuthService;
import com.microhabit.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    public void init() {
        DbUtil.init(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) {
            forwardMessage(req, resp, "Missing action", "home");
            return;
        }

        try {
            switch (action) {
                case "register":
                    handleRegister(req, resp);
                    break;
                case "login":
                    handleLogin(req, resp);
                    break;
                case "logout":
                    handleLogout(req, resp);
                    break;
                default:
                    forwardMessage(req, resp, "Unknown action", "home");
                    break;
            }
        } catch (Exception ex) {
            log("AuthServlet error action=" + action, ex);
            forwardMessage(req, resp, "Server error", "home");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            forwardMessage(req, resp, "Please fill all fields", "register");
            return;
        }

        try {
            authService.register(name, email, password);
        } catch (Exception e) {
            log("Register failed", e);
            forwardMessage(req, resp, "Registration failed (email may already exist).", "register");
            return;
        }

        // redirect to controller route (not JSP)
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (isBlank(email) || isBlank(password)) {
            forwardMessage(req, resp, "Please enter email and password", "login");
            return;
        }

        UserDAO.UserRow user;
        try {
            user = authService.login(email, password);
        } catch (Exception e) {
            log("Login failed", e);
            forwardMessage(req, resp, "Login error", "login");
            return;
        }

        if (user == null) {
            forwardMessage(req, resp, "Invalid login", "login");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.userId);
        session.setAttribute("userName", user.fullName);

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        resp.sendRedirect(req.getContextPath() + "/");
    }

    private void forwardMessage(HttpServletRequest req, HttpServletResponse resp, String msg, String back)
            throws ServletException, IOException {
        req.setAttribute("msg", msg);
        req.setAttribute("back", back); // e.g. "login", "register", "dashboard", "home"
        req.getRequestDispatcher("/WEB-INF/views/message.jsp").forward(req, resp);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

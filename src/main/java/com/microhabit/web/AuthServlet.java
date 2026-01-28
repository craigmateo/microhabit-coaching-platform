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
            resp.sendRedirect("message.jsp?msg=Missing action");
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
                    resp.sendRedirect("message.jsp?msg=Unknown action");
            }
        } catch (Exception ex) {
            log("AuthServlet error action=" + action, ex);
            resp.sendRedirect("message.jsp?msg=Server error");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            resp.sendRedirect("message.jsp?msg=Please fill all fields");
            return;
        }

        authService.register(name, email, password);
        resp.sendRedirect("login.jsp");
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (isBlank(email) || isBlank(password)) {
            resp.sendRedirect("message.jsp?msg=Please enter email and password");
            return;
        }

        UserDAO.UserRow user = authService.login(email, password);
        if (user == null) {
            resp.sendRedirect("message.jsp?msg=Invalid login");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.userId);
        session.setAttribute("userName", user.fullName);

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        resp.sendRedirect(req.getContextPath() + "/");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

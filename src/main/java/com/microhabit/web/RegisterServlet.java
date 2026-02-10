package com.microhabit.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // If already logged in, go to dashboard
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("userId") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }
}

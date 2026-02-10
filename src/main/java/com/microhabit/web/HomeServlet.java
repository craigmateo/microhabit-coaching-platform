package com.microhabit.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet({"/", "/home"})
public class HomeServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
  }
}

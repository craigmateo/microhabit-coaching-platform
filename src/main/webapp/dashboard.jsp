<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Dashboard – Microhabit Coach</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css">
</head>
<body>
<%
  if (session.getAttribute("userId") == null) {
    response.sendRedirect("login.jsp");
    return;
  }
  Boolean completedToday = (Boolean) request.getAttribute("completedToday");
  boolean disableBtn = (completedToday != null && completedToday);
%>

<div class="container">
  <div class="nav">
    <div class="brand">Microhabit Coach</div>
    <div class="pill">Logged in as: <b><%= session.getAttribute("userName") %></b></div>
  </div>

  <%
  String flash = (String) session.getAttribute("flashMsg");
  if (flash != null) {
    session.removeAttribute("flashMsg");
%>
  <div class="card" style="margin-bottom:16px;">
    <p class="badge"><%= flash %></p>
  </div>
<%
  }
%>


  <div class="grid">
    <div class="card">
      <h2>Today’s Habit</h2>
      <p class="badge"><%= request.getAttribute("todayTitle") %></p>
      <p><%= request.getAttribute("todayDescription") %></p>

      <form method="post" action="<%= request.getContextPath() %>/dashboard">
        <input type="hidden" name="action" value="complete"/>
        <button class="button" type="submit" <%= disableBtn ? "disabled" : "" %>>
          Mark Completed
        </button>
      </form>

      <%
        if (disableBtn) {
      %>
        <p class="badge">Completed today ✅</p>
      <%
        }
      %>

      <p class="small">Small steps build consistency.</p>
    </div>

    <div class="card kpi">
      <h3>Progress</h3>
      <div class="kpiRow">
        <div>
          <div class="label">Current streak</div>
          <div class="value"><%= request.getAttribute("currentStreak") %></div>
        </div>
        <div>
          <div class="label">Longest streak</div>
          <div class="value"><%= request.getAttribute("longestStreak") %></div>
        </div>
      </div>

      <form method="post" action="<%= request.getContextPath() %>/auth" style="margin-top: 14px;">
        <input type="hidden" name="action" value="logout"/>
        <button class="button secondary" type="submit">Logout</button>
      </form>
    </div>
  </div>
</div>

</body>
</html>

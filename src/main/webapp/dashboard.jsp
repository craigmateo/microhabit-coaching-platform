<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Dashboard – Micro-Habit Gym Coach</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css">
</head>
<body>
<%
  // Safety: if opened directly without servlet/session
  if (session.getAttribute("userId") == null) {
    response.sendRedirect("login.jsp");
    return;
  }

  boolean completedToday = Boolean.TRUE.equals(request.getAttribute("completedToday"));
%>

<div class="container">
  <div class="nav">
    <div class="brand">Micro-Habit Gym Coach</div>
    <div class="pill">
      Logged in as: <b><%= session.getAttribute("userName") %></b>
    </div>
  </div>

  <div class="grid">
    <!-- Today card -->
    <div class="card">
      <h2>Today’s Micro-Habit</h2>

      <p class="badge">${todayTitle}</p>
      <p>${todayDescription}</p>

      <% if (completedToday) { %>
        <p class="badge">Completed today ✅</p>
        <button class="button" type="button" disabled>Mark Completed</button>
      <% } else { %>
        <form method="post" action="<%= request.getContextPath() %>/dashboard">
          <input type="hidden" name="action" value="complete"/>
          <button class="button" type="submit">Mark Completed</button>
        </form>
      <% } %>

      <p class="small">Small steps build consistency.</p>
    </div>

    <!-- Progress card -->
    <div class="card kpi">
      <h3>Progress</h3>

      <div class="kpiRow">
        <div>
          <div class="label">Current streak</div>
          <div class="value">${currentStreak}</div>
        </div>
        <div>
          <div class="label">Longest streak</div>
          <div class="value">${longestStreak}</div>
        </div>
      </div>

      <!-- Tomorrow preview -->
      <div style="margin-top:16px;">
        <h3 style="margin-bottom:8px;">Tomorrow’s Habit (Preview)</h3>
        <p class="badge">${tomorrowTitle}</p>
        <p class="small">${tomorrowDescription}</p>
      </div>

      <!-- Logout via AuthServlet -->
      <form method="post" action="<%= request.getContextPath() %>/auth" style="margin-top: 16px;">
        <input type="hidden" name="action" value="logout"/>
        <button class="button secondary" type="submit">Logout</button>
      </form>

      <a class="button secondary" style="margin-top:10px; display:inline-block;"
         href="<%= request.getContextPath() %>/">Home</a>
    </div>
  </div>
</div>

</body>
</html>

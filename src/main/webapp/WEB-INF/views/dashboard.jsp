<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>

<div class="container">
  <div class="nav">
    <div class="brand">Micro-Habit Gym Coach</div>
    <div class="pill">
      Logged in as: <b>${userName}</b>
    </div>
  </div>

  <div class="grid">
    <!-- Today card -->
    <div class="card">
      <h2>Today’s Micro-Habit</h2>

      <p class="badge">${todayTitle}</p>
      <p>${todayDescription}</p>

      <c:choose>
        <c:when test="${completedToday}">
          <p class="badge">Completed today ✅</p>
          <button class="button" type="button" disabled>Mark Completed</button>
        </c:when>
        <c:otherwise>
          <form method="post" action="${pageContext.request.contextPath}/dashboard">
            <input type="hidden" name="action" value="complete"/>
            <button class="button" type="submit">Mark Completed</button>
          </form>
        </c:otherwise>
      </c:choose>

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

      <!-- Logout via controller -->
      <form method="post" action="${pageContext.request.contextPath}/auth" style="margin-top:16px;">
        <input type="hidden" name="action" value="logout"/>
        <button class="button secondary" type="submit">Logout</button>
      </form>

      <a class="button secondary" href="${pageContext.request.contextPath}/">Home</a>
    </div>
  </div>
</div>

</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Login</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css">
</head>
<body>
<div class="container">
  <div class="card">
    <h2>Login</h2>
    <form method="post" action="<%= request.getContextPath() %>/auth">
      <input type="hidden" name="action" value="login" />
      <label>Email</label>
      <input type="email" name="email" required />
      <label>Password</label>
      <input type="password" name="password" required />
      <button class="button" type="submit">Login</button>
    </form>
    <p class="small">No account? <a href="register.jsp">Register</a></p>
  </div>
</div>
</body>
</html>

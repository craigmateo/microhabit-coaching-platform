<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Register</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<div class="container">
  <div class="card">
    <h2>Register</h2>

    <form method="post" action="${pageContext.request.contextPath}/auth">
      <input type="hidden" name="action" value="register" />

      <label>Name</label>
      <input type="text" name="name" required />

      <label>Email</label>
      <input type="email" name="email" required />

      <label>Password</label>
      <input type="password" name="password" required />

      <button class="button" type="submit">Create account</button>
    </form>

    <p class="small">
      Already registered?
      <p class="small">Already registered? <a href="${pageContext.request.contextPath}/login">Login</a></p>
    </p>
  </div>
</div>
</body>
</html>

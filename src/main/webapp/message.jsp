<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Message</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css">
</head>
<body>

<div class="container">
  <div class="card">
    <h2>Update</h2>
    <p class="badge">
      <%= request.getParameter("msg") == null ? "" : request.getParameter("msg") %>
    </p>

    <div style="margin-top:14px;">
      <a class="button" href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a>
      <a class="button secondary" href="<%= request.getContextPath() %>/">Home</a>
    </div>
  </div>
</div>

</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Message</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css">
</head>
<body>

<%
  String msg = request.getParameter("msg");
  if (msg == null) msg = "";

  // Optional "back" target
  // message.jsp?msg=...&back=dashboard
  String back = request.getParameter("back");
  String backUrl = "index.jsp";
  if ("dashboard".equalsIgnoreCase(back)) {
    backUrl = request.getContextPath() + "/dashboard";
  } else if ("login".equalsIgnoreCase(back)) {
    backUrl = "login.jsp";
  } else if ("register".equalsIgnoreCase(back)) {
    backUrl = "register.jsp";
  }
%>

<div class="container">
  <div class="card">
    <h2>Message</h2>
    <p class="badge"><%= msg %></p>

    <div style="margin-top:16px;">
      <a class="button" href="<%= backUrl %>">Back</a>
      <a class="button secondary" href="<%= request.getContextPath() %>/">Home</a>
    </div>
  </div>
</div>

</body>
</html>

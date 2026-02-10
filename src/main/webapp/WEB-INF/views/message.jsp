<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Message</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>

<c:set var="msgText" value="${empty requestScope.msg ? '' : requestScope.msg}" />
<c:set var="back" value="${empty requestScope.back ? 'home' : requestScope.back}" />

<c:choose>
  <c:when test="${back eq 'dashboard'}">
    <c:set var="backUrl" value="${pageContext.request.contextPath}/dashboard" />
  </c:when>
  <c:when test="${back eq 'login'}">
    <c:set var="backUrl" value="${pageContext.request.contextPath}/login" />
  </c:when>
  <c:when test="${back eq 'register'}">
    <c:set var="backUrl" value="${pageContext.request.contextPath}/register" />
  </c:when>
  <c:otherwise>
    <c:set var="backUrl" value="${pageContext.request.contextPath}/" />
  </c:otherwise>
</c:choose>

<div class="container">
  <div class="card">
    <h2>Message</h2>

    <!-- escapeXml prevents HTML injection -->
    <p class="badge">${fn:escapeXml(msgText)}</p>

    <div style="margin-top:16px;">
      <a class="button" href="${backUrl}">Back</a>
      <a class="button secondary" href="${pageContext.request.contextPath}/">Home</a>
    </div>
  </div>
</div>

</body>
</html>

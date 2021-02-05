<%--
  Created by IntelliJ IDEA.
  User: thomas
  Date: 04/02/21
  Time: 15:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error</title>
</head>
<body>
    <%
        String errorMessage = (String) request.getAttribute("error");
    %>

    <h3>Error: <%=errorMessage != null ? errorMessage : "There is no error :P"%></h3>

    <a href="register">Register</a>
    <a href="login">Login</a>
</body>
</html>

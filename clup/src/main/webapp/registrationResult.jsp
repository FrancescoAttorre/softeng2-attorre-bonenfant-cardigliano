<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Registration</title>
</head>
<body>

    <%
        boolean registered = (boolean) request.getAttribute("registered");
        String name = request.getParameter("name");
    %>

    <h3><%= registered ? name + " successfully registered" : "Could not register " + name%></h3>

    <a href="login">Login</a>
    <a href="register">Register</a>
</body>
</html>

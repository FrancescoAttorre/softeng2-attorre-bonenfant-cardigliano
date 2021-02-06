<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Building Insertion Result</title>
</head>
<body>
    <%
        String accessCode = (String) request.getAttribute("result");
    %>

    <p>Your Access Code is: <%=accessCode != null ? accessCode : "(no access code)"%></p>
</body>
</html>

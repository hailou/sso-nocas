<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>应用列表</title>
</head>
<body>

<c:forEach items="${apps}" var="app">
     <a href="${app.appUrl}" target="_blank">${app.appName}</a><br/>
</c:forEach>


</body>
</html>

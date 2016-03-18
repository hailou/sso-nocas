<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>SSO-server-itsinic</title>
	</head>
	<body>
		<div align="center" style="margin-top: 150px;">
		<div>SSO-server 请登录</div>
		<form action="${pageContext.request.contextPath }/SSOAuth" method="post">
			<input type="hidden" name="action" value="login" />
			<input type="hidden" name="gotoURL" value="${param.gotoURL }" />
			<input type="hidden" name="setCookieURL" value="${param.setCookieURL }" />
			<table>
				<tr>
					<td>
						账号:
					</td>
					<td>
						<input type="text" name="username" />
					</td>
				</tr>
				<tr>
					<td>
						密码:
					</td>
					<td>
						<input type="password" name="password" />
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
					<td>
						<input type="checkbox" name="autoAuth" value="1" />记住我一周
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
					<td>
						<input type="submit" value="登录" />
					</td>
				</tr>
			</table>
			${errorInfo}
		</form>
		</div>
	</body>
</html>
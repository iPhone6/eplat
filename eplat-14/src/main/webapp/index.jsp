<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%response.sendRedirect("loginController.do?Login.html"); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>登录</title>
	
  </head>
  
  <body>
   	<a href="<%=basePath%>Login.html">首页，点击进入Elead招聘系统首页</a></h5>
  </body>
</html>

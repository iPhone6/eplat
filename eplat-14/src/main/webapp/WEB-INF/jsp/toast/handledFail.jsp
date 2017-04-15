<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>操作失败</title>
  <link href="<%=basePath %>css/404page.css" media="screen" rel="stylesheet" type="text/css" />
</head>

<body>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
  <h2>操作失败，请稍后再试！</h2>
</body>
</html>

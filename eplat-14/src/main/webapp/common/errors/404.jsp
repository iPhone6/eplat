<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>你所访问的页面不存在 (404)</title>
  <link href="<%=basePath %>css/404page.css" media="screen" rel="stylesheet" type="text/css" />
</head>

<body>
  <h1>404</h1>
  <h3>你所访问的页面不存在.</h3>
  <hr/>
  <p>资源不存在或者没有访问权限 <a href="<%=basePath %>loginController.do?LoginCheck">返回首页</a> 或 <a href="<%=basePath %>loginController.do?Login.html">登录</a></p>
</body>
</html>

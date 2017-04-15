<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
<base href="<%=basePath%>">
<title>导入（将要推送华为考勤系统的人员）信息</title>
</head>
<body>
	<h3>（将要推送华为考勤系统的人员）信息Excel文件上传</h3>
	<br>

	<h3>选择（将要推送华为考勤系统的人员）信息Excel表格文件（.xlsm 格式）</h3>
	<form name="form0" action="epDataController.do?impHwEmpInfo" method="post" enctype="multipart/form-data">
		<input type="file" name="hw_emp_info_xlsm"> <input type="submit" value="upload" />
	</form>
	<hr>

</body>
</html>

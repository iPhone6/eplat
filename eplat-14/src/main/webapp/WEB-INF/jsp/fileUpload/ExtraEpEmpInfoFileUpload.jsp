<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
<base href="<%=basePath%>">
<title>导入（移动考勤试用人员）信息</title>
</head>
<body>
	<h3>（移动考勤试用人员）信息Excel文件上传</h3>
	<br>

	<h3>选择（移动考勤试用人员）信息Excel表格文件（.xls 格式）</h3>
	<form name="form0" action="epDataController.do?impEmpInfo_extra_ep" method="post" enctype="multipart/form-data">
		<input type="file" name="ep_emp_info_extra_xls"> <input type="submit" value="upload" />
	</form>
	<hr>

</body>
</html>

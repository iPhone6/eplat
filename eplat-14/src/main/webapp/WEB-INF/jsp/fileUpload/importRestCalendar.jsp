<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
<base href="<%=basePath%>">
<title>导入休息日期表格</title>
<script type="text/javascript">
 function checkCalendar(importRestCalendarForm) {
 	var importFileValue = document.getElementById("importCalendarFileId").value;
	if(importFileValue==""){
		alert("请选择休息日期表格！");
		return false;
	}
	
	return true;
}
</script>
</head>
<body>
	<div style="margin-top: 20px;margin-left: 20px">
	<h3>导入休息日期表格</h3>
	<br>
	<form name="importRestCalendarForm" action="upLoadFileController.do?importRestCalendar" method="post" 
							enctype="multipart/form-data" onsubmit=" return checkCalendar(this)">
		<label>选择要导入休息日期的表格</label><input type="file" name="importFile"  id="importCalendarFileId"><br><br><br>
		<input type="submit" value="导入休息日期表格 "/>
	</form>
	<hr>
	</div>
</body>
</html>

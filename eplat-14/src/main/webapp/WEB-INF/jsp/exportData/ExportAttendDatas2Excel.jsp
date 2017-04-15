<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
<base href="<%=basePath%>">
<title>导出对应员工的考勤数据</title>
<script type="text/javascript">
 function checkDate(exportForm) {
 	var importFileValue = document.getElementById("importEmpsFileId").value;
	if(importFileValue==""){
		alert("请选择人员名单！");
		return false;
	}
	
	if(exportForm.startdate.value == ""||exportForm.enddate.value  == ""){
		alert("开始,结束日期必填");
		return false;
	}
	return true;
}
</script>
</head>
<body>
	<div style="margin-top: 20px;margin-left: 20px">
	<h3>导出员工的考勤数据到excel表格</h3>
	<br>
	<form name="exportForm" action="epAttenController.do?exportAttendDatas" method="post" 
							enctype="multipart/form-data" onsubmit=" return checkDate(this)">
		<label>选择要导出考勤数据对应的人员表格</label><input type="file" name="importFile"  id="importEmpsFileId"><br><br><br>
		输入开始日期   <label style="color: red">(格式:2015-04-08)</label><input type="text" name="startdate"><br><br><br>
		输入结束日期   <label style="color: red">(格式:2015-08-15)</label><input type="text" name="enddate"><br><br><br>
		<input type="submit" value="导出考勤数据" />
	</form>
	<hr>
	</div>
</body>
</html>

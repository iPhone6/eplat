<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
<base href="<%=basePath%>">
<title>发布新版本APK</title>
<script type="text/javascript">
	function checkApk(upLoadApkForm) {
		var importFileValue = document.getElementById("importApkFileId").value;
		if (importFileValue == "") {
			alert("选择要上传的APK！");
			return false;
		}

		var apkExp = /^.+.[a,A][p,P][k,K]$/;
		if (!apkExp.test(importFileValue)) {
			alert("文件必须是apk");
			return false;
		}

		var version = upLoadApkForm.version.value;
		if (version == "") {
			alert("版本号不能为空！");
			return false;
		}

		var regExp = /^\d+(\.\d+)+$/;
		if (!regExp.test(version)) {
			alert("版本号格式不正确！");
			return false;
		}

		return true;
	}
</script>
</head>
<body>
	<div style="margin-top: 20px;margin-left: 20px">
	<h3>发布新版本APK</h3>
	<br>
	<form name="upLoadApkForm" action="upLoadFileController.do?upLoadAndroidApk"  method="post"  enctype="multipart/form-data" onsubmit=" return checkApk(this)">
		选择要上传的APK:   <input type="file" name="importFile"  id="importApkFileId"><br><br><br>
		版本号:   <input type="text" name="version"><label style="color: red">*格式必须为1.1.1</label><br><br><br>
		版本更新内容:   <textarea rows="4" cols="50"  name="point"></textarea><br><br><br>
		<input type="submit" value="上传"/>
	</form>
	<hr>
	</div>
</body>
</html>

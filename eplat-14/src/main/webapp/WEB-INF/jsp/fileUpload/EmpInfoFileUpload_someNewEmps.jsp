<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
<base href="<%=basePath%>">
<title>导入（一部分新员工）信息</title>
</head>
<body>
	<h3>（一部分新员工）信息Excel文件上传</h3>
	<br>

	<h3>选择（一部分新员工）信息Excel表格文件（.xls 格式）</h3>
	<form name="form0" action="epDataController.do?impEmpInfo_someNewEmps" method="post" enctype="multipart/form-data">
		<input type="file" name="some_new_emps_info_xls"> <input type="submit" value="upload" />
	</form>
	<hr>

<%-- 
	<h3>采用 fileUpload_multipartFile ， file.transferTo 来保存上传的文件</h3>
	<form name="form1" action="FileUploadController.do?fileUpload_multipartFile" method="post" enctype="multipart/form-data">
		<input type="file" name="file_upload"> <input type="submit" value="upload" />
	</form>
	<hr>

	<h3>采用 fileUpload_multipartRequest file.transferTo 来保存上传文件</h3>
	<form name="form2" action="FileUploadController.do?fileUpload_multipartRequest" method="post" enctype="multipart/form-data">
		<input type="file" name="file_upload"> <input type="submit" value="upload" />
	</form>
	<hr>

	<h3>采用 CommonsMultipartResolver file.transferTo 来保存上传文件</h3>
	<form name="form3" action="FileUploadController.do?fileUpload_CommonsMultipartResolver" method="post" enctype="multipart/form-data">
		<input type="file" name="file_upload"> <input type="submit" value="upload" />
	</form>
	<hr>

	<h3>使通过流的方式上传文件--存在上传后无法使用的问题</h3>
	<form name="form4" action="FileUploadController.do?fileUpload_stream" method="post" enctype="multipart/form-data">
		<input type="file" name="file_upload"> <input type="submit" value="upload" />
	</form>
	<hr>

	<h3>多文件上传 采用 MultipartFile[] multipartFile 上传文件方法</h3>
	<form name="form5" action="FileUploadController.do?fileUpload_spring_list" method="post" enctype="multipart/form-data">
		<input type="file" name="file_upload"> <input type="file" name="file_upload"> <input type="file" name="file_upload"> <input type="submit" value="upload" />
	</form>
	<hr>

	<h3>通过 a 标签的方式进行文件下载</h3>
	<br>
	<a href="<%=basePath%>filesOut/Download/mst.txt">通过 a 标签下载文件 mst.txt</a>
	<hr>
	<h3>通过 Response 文件流的方式下载文件</h3>
	<a href="FileUploadController.do?fileDownload_servlet">通过 文件流 的方式下载文件 mst.txt</a>
 --%>

</body>
</html>

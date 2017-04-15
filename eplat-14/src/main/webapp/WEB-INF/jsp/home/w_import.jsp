<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script src="bootstrap-prettyfile.js"></script>

<script type="text/javascript">
	function tijiao() {
		//debugger;
		var fd = new FormData();
		var files = document.getElementById("selectFiles").files;
		var selectValue = document.getElementById("qd");
		var index = selectValue.selectedIndex; // selectedIndex代表的是你所选中项的index
		//console.log("run test试一下:"+selectValue.options[index].value);
		//选中多文件
		for (var i = 0; i < files.length; i++) {
			fd.append("file" + i, files[i]);
		}
		fd.append("qd", selectValue.options[index].value);

		//var radioValue = document.getElementById("rid");
		//for(var i=0;i<radioValue.length;i++)
		// {
		// if(radioValue[i].checked==true)
		// {
		// 	fd.append("rid",radioValue[i].value);
		//  }
		//  }

		$.ajax({
			url : "resumeController.do?uploadFile",
			type : "POST",
			processData : false,
			contentType : false,
			data : fd,
			success : function(data) {
				//data是服务器响应的结果
				console.log(JSON.stringify(data));//将结果转换成字符串json输出
				$("#uploadErrorMsg").text(data.message);
				$("#uploadErrorMsg").css({display:'block'});
				if(data.statusCode==300) {
					$("#uploadErrorMsg").attr('color','red');
				}
				if(data.statusCode==200) {
					$("#uploadErrorMsg").attr('color','blue');
				}
			}
		});
	}
</script>


<div class="form-group">
<br/>
	<p>
		<label>${message }</label>
	</p>
	<p>
		<label>请选择渠道：</label> <select id="qd" name="qd">
			<option value="2">智联招聘</option>
			<option value="4">人才热线</option>
			<option value="1">51job</option>
			<option value="7">猎聘网</option>
			<option value="5">e成</option>
			<option value="3">中华英才</option>
			<option value="6">枇杷派</option>
			<option value="7">拉勾网</option>
			<option value="8">BOSS直聘</option>
			<option value="9">招聘会</option>
			<option value="10">猎头推荐</option>
			<option value="11">内部推荐</option>
			<option value="12">校园招聘</option>
			<option value="13">论坛</option>
			<option value="14">技术群</option>
			<option value="15">其它</option>
		</select>
	</p>

	<div class="divider"></div>
	<div class="container">
		<div class="starter-template">
			多文件上传：
		</div>
		<br/>
	        <font color="red" id="uploadErrorMsg" style="display:none;" ></font>
		<br/>
		<input type="file" id="selectFiles" multiple="multiple">
	</div>
	
	<div class="divider"></div>
	<br/>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" onclick="tijiao()">提交</button>
</div>


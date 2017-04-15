<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	i = 1;
	j = 1;
	$(document)
			.ready(
					function() {

						$("#btn_add2")
								.click(
										function() {
											document
													.getElementById("newUpload2").innerHTML += '<div id="div_'+j+'"><input  name="file_'+j+'" type="file"  /><input type="button" value="删除"  onclick="del_2('
													+ j + ')"/></div>';
											j = j + 1;
										});

					});

	function del_2(o) {
		document.getElementById("newUpload2").removeChild(
				document.getElementById("div_" + o));
	}
</script>






<div class="pageContent">
	<form method="post" action="resumeController.do?uploadFile"
		enctype="multipart/form-data" class="pageForm required-validate"
		onsubmit="return iframeCallback(this);">
		<div class="pageFormContent" layoutH="56">
			<p>
				<label>请选择渠道：</label> <select name="qd">
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
			<!-- 
			<p>
				<label>简历上传：</label> <input id="myfile" name="" type="file" />
			</p>
			-->

			<p>
				<label>简历上传：</label>
			<div id="newUpload2">
				<input type="file" name="file">
			</div>

			</p>

			<p>
				<label></label> <input type="button" id="btn_add2" value="增加一行">
			</p>


			<table class="table" width="100%" layoutH="138">
				<thead>
					<tr>
						<th width="25"></th>
						<th width="100">岗位</th>
						<th width="80">项目负责人</th>
						<th width="120">项目</th>
						<th width="80">二级部门</th>
						<th width="80">一级部门</th>
						<th width="80">地区</th>
						<th width="80">优先级别</th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${!empty requirements }">
						<c:forEach items="${requirements}" var="requirement">
							<tr>
								<td><input type="radio" name="rid" value="${requirement.id }"/></td>
								<td><a
									href="requirementController.do?view&req_id=${requirement.id }"
									target="navTab" rel="view_requirement_t" title="查看需求"
									style="text-decoration:none;">${requirement.position }</a></td>
								<td>${requirement.project_header }</td>
								<td>${requirement.sector }</td>
								<td>${requirement.project_name }</td>
								<td>${requirement.dname }</td>
								<td>${requirement.regname }</td>
								<td>${requirement.level }</td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>

			
			<p><label>多文件上传：</label>
 				<a rel="w_uploadify" target="navTab" href="resumeController.do?importResume2" class="button"><span>uploadify上传示例</span></a>
			</p>
			 
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive">
						<div class="buttonContent">
							<button type="submit">提交</button>
						</div>
					</div></li>
				<li><div class="button">
						<div class="buttonContent">
							<button type="button" class="close">取消</button>
						</div>
					</div></li>
			</ul>
		</div>
	</form>
</div>

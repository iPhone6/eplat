<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="pageContent">
	<form onsubmit="return validateCallback(this, dialogAjaxDone);"
		action="resumeController.do?requirementSelectJson" method="post">
		<div class="pageFormContent" layoutH="56">
			<input type="hidden" value="${rid }" name="rid" />
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
								<td><input type="radio" name="reid"
									value="${requirement.id }" /></td>
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

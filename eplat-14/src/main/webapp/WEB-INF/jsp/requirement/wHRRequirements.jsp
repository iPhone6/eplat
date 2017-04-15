<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);" action="" method="post" id="">
		<div class="searchBar">
			<table class="searchContent" >
			    <tr>
			      <td colspan="5">&nbsp;<span style="font-weight:bold;color:#FF0000;">我名下的需求</span></td>
			    </tr>
			</table>
		</div>
	</form>
</div>
<div class="pageContent">
	
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th width="80">地区</th>
				<th width="80">一级部门</th>
				<th width="80">二级部门</th>
				<th width="120">项目</th>
			    <th width="100">岗位</th>
				<th width="80">项目负责人</th>
				<th width="80">优先级别</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty requirements }">
				<c:forEach items="${requirements}" var="requirement">
							<tr>
								<td><a
									href="interviewerController.do?viewRequirement&req_id=${requirement.id }"
									target="navTab" rel="view_requirement_t" title="查看需求"
									style="color:blue;">${requirement.regname }</a></td>
								<td>${requirement.dname }</td>
								<td>${requirement.sector }</td>
								<td>${requirement.project_name }</td>
								<td>${requirement.position }</td>
								<td>${requirement.project_header }</td>
								<td>${requirement.level }</td>
							</tr>
						</c:forEach>
			</c:if>
		</tbody>
	</table>
</div>
</div>

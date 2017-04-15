<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">

function cancel_requirement(url,data) {
	alertMsg.confirm("请确认是否暂停？", {
		okCall: function() {
			$.post(url,data,DWZ.ajaxDone,"json");
		},
		cancelCall: function() {
		}
	});
}

</script>

<form id="pagerForm" rel="searchForm" method="post" action="">
	<input type="hidden" name="pvo_status" value="${pvo.status}">
	<input type="hidden" name="keywords" value="${pvo.keywords}" />
	<input type="hidden" name="pageNum" value="${pvo.pageNum }" />
	<input type="hidden" name="numPerPage" value="${pvo.pageSize }" />
	<input type="hidden" name="orderField" value="${pvo.orderField}" />
	<input type="hidden" name="orderDirection" value="${pvo.orderDirection}" />
	<!-- 以下隐藏输入框用于保存用户输入的搜索条件，以便在按条件搜索后，可以点击页码或下一页等分页操作，查看到在指定搜索条件下的结果在不同分页下的结果数据。 -->
	<input type="hidden" name="name" type="text" value="${name }" />
	<input type="hidden" name="identityNo" type="text" value="${identityNo }" />
	<input type="hidden" name="mobilePhone" type="text" value="${mobilePhone }" />
	<input type="hidden" name="position" type="text" value="${position }" />
	<input type="hidden" name="status" type="text" value="${status }" />
	<input type="hidden" name="fuzzy_key" type="text" value="${fuzzy_key }" />
	<input type="hidden" name="recruiter_name" type="text" value="${recruiter_name }" />
	<input type="hidden" type="text" name="interviewTime_start" value="${interviewTime_start }">
	<input type="hidden" type="text" name="interviewTime_end" value="${interviewTime_end }">
</form>

<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);" action="" method="post" id="">
		<div class="searchBar">
			<table class="searchContent" >
			    <tr>
			      <td colspan="5">&nbsp;<span style="font-weight:bold;color:#FF0000;">需求处理</span></td>
			    </tr>
			    <tr>
				    <td >&nbsp;
				    </td>
			    </tr>
			</table>
			<div>
				<input type="hidden" name="pageNum" value="${pvo.pageNum }" />
				<input type="hidden" name="numPerPage" value="${pvo.pageSize }" />
			</div>
		</div>
	</form>
</div>
<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
			<li><a class="edit" href="requirementController.do?gotoAssignRecruiter&req_id={reqid}" target="dialog" mask="true" title="分配招聘专员" ><span>分配</span></a></li>
			<li><a class="edit" href="requirementController.do?gotoModifyRequirement&req_id={reqid}" target="navTab" rel="modify_requirement_tab" navTabId="modify_requirement_tab" title="修改需求" ><span>修改</span></a></li>
			<!-- <li><a href="evaluationController.do?addEvaluation&rid={rid}"
				target="navTab" title="信息记录"><span
					style="text-decoration:none;font-weight:bold;color:#FF0000;">信息记录</span></a></li>  -->
			<!-- 
				<li>
					<a class="edit" href="candidateController.do?gotoModifyInterviewTime&rid={rid}" target="dialog" mask="true"><span>修改面试时间</span></a>
				</li>
				<li>
					<a class="edit" href="candidateController.do?gotoModifyPosition&rid={rid}" target="dialog" mask="true"><span>修改岗位</span></a>
				</li>
			 -->
		</ul>

	</div>
	
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
			 	<th width="25"></th>
				<th width="80">地区</th>
				<th width="80">一级部门</th>
				<th width="80">二级部门</th>
				<th width="120">项目</th>
			    <th width="100">岗位</th>
				<th width="80">招聘人数</th>
				<th width="80">项目负责人</th>
			    <th width="100">优先级别</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty requirements }">
				<c:forEach items="${requirements}" var="requirement">
					<tr target="reqid" rel="${requirement.id }">
						<td><input type="checkbox" /></td>
						<td><a href="requirementController.do?view&req_id=${requirement.id }" target="navTab" rel="view_requirement_t" title="查看需求" style="color:blue;">${requirement.region_name }</a></td>
						<td>${requirement.department_name }</td>
						<td>${requirement.sector }</td>
						<td>${requirement.project_name }</td>
						<td>${requirement.position }</td>
						<td>${requirement.require_num }</td>
						<td>${requirement.project_header }</td>
						<td>${requirement.level }</td>
						<td>
						<!-- 
							<input type="button" value="分配" >
						    <input type="button" value="取消" onclick="queren()">
						    <input type="button" value="修改">
							<a href="javascript:;" style="text-decoration:none;font-weight:bold;color:#FF0000;" 
								onclick="del_candidate('candidateController.do?delete_candidate_by_rid_json&rid=${candidate.rid }')">取消</a>
						 -->
							<a href="requirementController.do?cancel_requirement_json&req_id=${requirement.id }" style="text-decoration:none;font-weight:bold;color:#FF0000;" 
								target="ajaxTodo" title="确定要暂停吗？" >暂停</a>
							<!-- <a class="delete" href="demo/common/ajaxDone.html?uid={sid_user}" target="ajaxTodo" title="确定要删除吗?"><span>删除</span></a> -->
						</td>
					</tr>				
				</c:forEach>
			</c:if>
		</tbody>
	</table>
	<div class="panelBar">
		<div class="pages">
			<span>每页</span>
			<select class="combox" name="numPerPage" onchange="navTabPageBreak({numPerPage:this.value})">
				<%-- <option value="1" <c:if test="${pvo.pageSize == 1}">selected</c:if>>1</option> --%>
				<option value="20" <c:if test="${pvo.pageSize == 20}">selected</c:if>>20</option>
				<option value="50" <c:if test="${pvo.pageSize == 50}">selected</c:if>>50</option>
				<option value="100" <c:if test="${pvo.pageSize == 100}">selected</c:if>>100</option>
				<option value="200" <c:if test="${pvo.pageSize == 200}">selected</c:if>>200</option>
			</select>
			<span>条，共 ${pvo.totalCount } 条</span>
		</div>
		<div class="pagination" targetType="navTab" totalCount="${pvo.totalCount }" numPerPage="${pvo.pageSize }" pageNumShown="10" currentPage="${pvo.pageNum }"></div>
	</div>
</div>

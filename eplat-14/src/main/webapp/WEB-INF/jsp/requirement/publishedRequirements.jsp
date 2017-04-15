<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	function cancel_requirement(url, data) {
		alertMsg.confirm("请确认是否暂停？", {
			okCall : function() {
				$.post(url, data, DWZ.ajaxDone, "json");
			},
			cancelCall : function() {
			}
		});
	}
	
	function resetForm_pub_req($form) {
	    $form.find('input:text, input:password, input:file, textarea').val('');
	    $form.find('input:radio, input:checkbox')
	         .removeAttr('checked').removeAttr('selected');
	    $('#sel_region_pub_req')[0].selectedIndex=0;
	    $('#sel_department_pub_req')[0].selectedIndex=0;
	    $('#sel_recruiter_pub_req')[0].selectedIndex=0;
	}
	function clearMySearchForm_pub_req() {
		resetForm_pub_req($('#mySearchForm_pub_req')); // by id, recommended
	}
	
</script>

<form id="pagerForm" rel="searchForm" method="post" action="requirementController.do?search_pub_req">
	<input type="hidden" name="pvo_status" value="${pvo.status}"> <input
		type="hidden" name="keywords" value="${pvo.keywords}" /> <input
		type="hidden" name="pageNum" value="${pvo.pageNum }" /> <input
		type="hidden" name="numPerPage" value="${pvo.pageSize }" /> <input
		type="hidden" name="orderField" value="${pvo.orderField}" /> <input
		type="hidden" name="orderDirection" value="${pvo.orderDirection}" />
	<!-- 以下隐藏输入框用于保存用户输入的搜索条件，以便在按条件搜索后，可以点击页码或下一页等分页操作，查看到在指定搜索条件下的结果在不同分页下的结果数据。 -->
	<input type="hidden" name="name" type="text" value="${name }" /> <input
		type="hidden" name="identityNo" type="text" value="${identityNo }" />
	<input type="hidden" name="mobilePhone" type="text"
		value="${mobilePhone }" /> <input type="hidden" name="position"
		type="text" value="${position }" /> <input type="hidden"
		name="status" type="text" value="${status }" /> <input type="hidden"
		name="fuzzy_key" type="text" value="${fuzzy_key }" /> <input
		type="hidden" name="recruiter_name" type="text"
		value="${recruiter_name }" /> <input type="hidden" type="text"
		name="interviewTime_start" value="${interviewTime_start }"> <input
		type="hidden" type="text" name="interviewTime_end"
		value="${interviewTime_end }">
		<input type="hidden" name="region_search" type="text" value="${region_search }" />
		<input type="hidden" name="department_search" type="text" value="${department_search }" />
		<input type="hidden" name="sector_department_search" type="text" value="${sector_department_search }" />
		<input type="hidden" name="project_search" type="text" value="${project_search }" />
		<input type="hidden" name="position_search" type="text" value="${position_search }" />
		<input type="hidden" name="recruiter_search" type="text" value="${recruiter_search }" />
		<input type="hidden" name="project_header_search" type="text" value="${project_header_search }" />
</form>

<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);" action="requirementController.do?search_pub_req" method="post"
		id="mySearchForm_pub_req">
		<div class="searchBar">
			<table class="searchContent">
				<tr>
					<!-- <td colspan="5">&nbsp;<span
						style="font-weight:bold;color:#FF0000;">已发布需求</span></td> -->

			      <!-- <td colspan="5">&nbsp;<span style="font-weight:bold;color:#FF0000;">已发布需求</span></td> -->
			    	<td>
				    	&nbsp; 地区：
				    	<%-- <input name="region_search" type="text" value="${region_search }" size="12" /> --%>
				    	<select name="region_search" class="" id="sel_region_pub_req" >
				    		<option value="0">请选择</option>
							<c:if test="${!empty regions }">
								<c:forEach items="${regions}" var="region">
									<option value="${region.id }" <c:if test="${region.id == region_search }">selected</c:if> >${region.NAME }</option>
								</c:forEach>
							</c:if>
						</select>
				    	&nbsp; 一级部门：
				    	<%-- <input name="department_search" type="text" value="${department_search }" size="12" /> --%>
				    	<select name="department_search" class="" id="sel_department_pub_req" >
				    		<option value="0">请选择</option>
							<c:if test="${!empty departments }">
								<c:forEach items="${departments}" var="department">
									<option value="${department.id }" <c:if test="${department.id == department_search }">selected</c:if> >${department.NAME }</option>
								</c:forEach>
							</c:if>
						</select>
						&nbsp; 二级部门：<input name="sector_department_search" type="text" value="${sector_department_search }" size="12" />
				    	&nbsp; 项目组：<input name="project_search" type="text" value="${project_search }" size="12" />
				    	&nbsp; 岗位：<input name="position_search" type="text" value="${position_search }" size="12" />
			    	</td>

				</tr>
				<tr>
					<!-- <td>&nbsp;</td> -->

				    <td >
				    	&nbsp; 招聘专员：
				    	<%-- <input name="recruiter_search" type="text" value="${recruiter_search }" /> --%>
				    	<select name="recruiter_search" class="" id="sel_recruiter_pub_req" >
							<option value="0">请选择</option>
							<c:if test="${!empty recruiters }">
								<c:forEach items="${recruiters}" var="recruiter">
									<option value="${recruiter.userId }" <c:if test="${recruiter.userId == recruiter_search }">selected</c:if> >${recruiter.userName }</option>
								</c:forEach>
							</c:if>
						</select>
				    	&nbsp; 项目负责人：<input name="project_header_search" type="text" value="${project_header_search }" />
				    	&nbsp; &nbsp;<input type="submit" value="搜索" />&nbsp;&nbsp;&nbsp;<input type="button" value="清空重输" onclick="clearMySearchForm_pub_req()" />
				    </td>

				</tr>
			</table>
			<div>
				<input type="hidden" name="pageNum" value="${pvo.pageNum }" /> <input
					type="hidden" name="numPerPage" value="${pvo.pageSize }" />
			</div>
		</div>
	</form>
</div>
<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
			<!-- <li><a class="edit" href="requirementController.do?gotoAssignRecruiter&req_id={reqid}" target="dialog" mask="true" title="分配招聘专员" ><span>分配</span></a></li> -->
			<c:if test="${sessionScope.user.rname  == '招聘主管' || sessionScope.user.rname  == '系统管理员'}">
				<li><a class="edit"
					href="requirementController.do?gotoModifyRequirement&req_id={reqid}"
					target="navTab" rel="modify_requirement_tab" navTabId="modify_requirement_tab" title="修改需求"><span>修改</span></a></li>
			</c:if>
		</ul>

	</div>

	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th width="25"></th>
				<th width="40">地区</th>
				<th width="80">一级部门</th>
				<th width="80">二级部门</th>
				<th width="120">项目</th>
				<th width="100">岗位</th>
				<th width="100">级别要求</th>
				<th width="100">面试官</th>
				<th width="80">项目负责人</th>
				<th width="80">招聘人数</th>
				<th width="80">已招人数</th>
				<th width="80">面试人数</th>
				<th width="100">发布时间</th>
				<th width="50">优先级别</th>
				<th width="200">招聘专员</th>
				<th width="50">操作</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty requirements }">
				<c:forEach items="${requirements}" var="requirement">
					<tr target="reqid" rel="${requirement.id }">
						<td><input type="checkbox" /></td>
						<td><a
							href="requirementController.do?view&req_id=${requirement.id }"
							target="navTab" rel="view_requirement_t" title="查看需求"
							style="color:blue;">${requirement.region_name }</a></td>
						<td>${requirement.department_name }</td>
						<td>${requirement.sector }</td>
						<td>${requirement.project_name }</td>
						<td>${requirement.position }</td>
						<td>${requirement.level_require }</td>
						<td>${requirement.interviewer_name }</td>
						<td>${requirement.project_header }</td>
						<td>${requirement.require_num }</td>
						<td>N/A</td>
						<td>
							<a href="requirementController.do?search_interviewed_candidates&req_id=${requirement.id }"
								target="navTab" rel="view_interviewed_candidates_t" title="已面候选人"
								style="color:blue">${requirement.interview_num }</a>
						</td>
						<td>${requirement.pub_time_formated }</td>
						<td>${requirement.level }</td>
						<td>${requirement.recruiter_name_list }</td>
						<td><c:if test="${sessionScope.user.rname  == '招聘主管' || sessionScope.user.rname  == '系统管理员'}">
								<%-- <a href="javascript:;"
									style="text-decoration:none;font-weight:bold;color:#FF0000;"
									onclick="cancel_requirement('requirementController.do?cancel_requirement_json&req_id=${requirement.id }')">暂停</a> --%>
								<a href="requirementController.do?cancel_requirement_json&req_id=${requirement.id }" style="text-decoration:none;font-weight:bold;color:#FF0000;" 
									target="ajaxTodo" title="确定要暂停吗？" >暂停</a>
							</c:if></td>
					</tr>
				</c:forEach>
			</c:if>
		</tbody>
	</table>
	<!-- 
	 -->
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

<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	function hxr(){
		alert('拉入成功！');
	}
	function resetForm4($form) {
	    $form.find('input:text, input:password, input:file, textarea').val('');
	    $form.find('input:radio, input:checkbox')
	         .removeAttr('checked').removeAttr('selected');
	    //$('#status_sel2')[0].selectedIndex=0;
	}
	function clearMySearchForm4() {
		resetForm4($('#mySearchForm4')); // by id, recommended
	}
</script>

<form id="pagerForm" rel="searchForm" method="post" action="candidateController.do?search_mystored_candidates">
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
	<form onsubmit="return navTabSearch(this);" action="candidateController.do?search_mystored_candidates" method="post" id="mySearchForm4">
		<div class="searchBar">
			<table class="searchContent" >
			    <tr>
			      <td colspan="5">&nbsp;<span style="font-weight:bold;color:#FF0000;">我的候选人提醒</span></td>
			    </tr>
			    <tr>
				    <td >&nbsp;姓名：<input name="name" type="text" value="${name }" />
				     		&nbsp;身份证：<input name="identityNo" type="text" value="${identityNo }" /> 
							&nbsp;手机号：<input name="mobilePhone" type="text" value="${mobilePhone }" /> 
				      		&nbsp;&nbsp;<input type="submit" value="搜索" />
				      		&nbsp;&nbsp;&nbsp;<input type="button" value="清空重输" onclick="clearMySearchForm4()" />
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
			
		</ul>
	</div>
	
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
			 	<th></th>
			    <th>姓名</th>
				<th>一级部门</th>
				<th>二级部门</th>
				<th>项目</th>
				<th>岗位</th>
				<th>学历</th>
				<th>专业</th>
				<th>工作经验</th>
				<th>面试时间</th>
				<th>联系电话</th>
				<th>邮件地址</th>
				<th>来源</th>
				<th>推荐人</th>
				<th>再联系日期</th>
				<th>操作</th>
				<th>导入时间</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty candidates }">
				<c:forEach items="${candidates}" var="candidate">
					<tr>
						<td><input type="checkbox" /></td>
						<td><a href="resumeController.do?view&rid=${candidate.rid }" target="navTab" rel="view_resume_t" title="查看简历" style="color:blue;">${candidate.name }</a></td>
						<td>${candidate.department.NAME }</td>
						<td>${candidate.sector }</td>
						<td>${candidate.project_name }</td>
						<td>${candidate.occupation_title }</td>
						<td>${candidate.eduDegree }</td>
						<td>${candidate.major }</td>
						<td>${candidate.workYears }</td>
						<td>${candidate.interviewTime }</td>
						<td>${candidate.mobilePhone }</td>
						<td>${candidate.contact_email}</td>
						<td>${candidate.channel }</td>
						<td>${candidate.proposer }</td>
						<td>${candidate.contactLaterDate }</td>
						<td>
							<a href="resumeController.do?candidateTime&cid=${candidate.cid }" target="dialog" rel="my_stored_candidates_t" mask="true" title="我的储备资源" style="text-decoration:none;font-weight:bold;color:#FF0000;" >修改再联系时间</a>&nbsp;&nbsp;&nbsp;
							<a href="resumeController.do?updateResume&rid=${candidate.rid }&cb=hxr"  target="navTab" rel="my_stored_candidates_t" title="我的储备资源" style="text-decoration:none;font-weight:bold;color:#FF0000;" onclick="hxr()">拉入候选人 </a>
						</td>
						<td>${candidate.RECORD_DATE }</td>
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

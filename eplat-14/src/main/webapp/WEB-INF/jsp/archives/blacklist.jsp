<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	function sf(){
		alert('释放成功！');
	}
	function resetForm3($form) {
	    $form.find('input:text, input:password, input:file, textarea').val('');
	    $form.find('input:radio, input:checkbox')
	         .removeAttr('checked').removeAttr('selected');
	    //$('#status_sel2')[0].selectedIndex=0;
	}
	function clearMySearchForm3() {
		resetForm3($('#mySearchForm3')); // by id, recommended
	}
</script>

<form id="pagerForm" rel="searchForm" method="post" action="candidateController.do?search_blacklist">
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
	<form onsubmit="return navTabSearch(this);" action="candidateController.do?search_blacklist" method="post" id="mySearchForm3">
		<div class="searchBar">
			<table class="searchContent" >
			    <tr>
			      <td colspan="5">&nbsp;<span style="font-weight:bold;color:#FF0000;">黑名单列表</span></td>
			    </tr>
			    <tr>
			      <td >&nbsp;姓名：<input name="name" type="text" value="${name }" />
						&nbsp;身份证：<input name="identityNo" type="text" value="${identityNo }" /> 
						&nbsp;手机号：<input name="mobilePhone" type="text" value="${mobilePhone }" /> 
						&nbsp;招聘专员：<input name="recruiter_name" type="text" value="${recruiter_name }" />
			      	&nbsp;<input type="submit" value="搜索" />
			      		&nbsp;&nbsp;&nbsp;<input type="button" value="清空重输" onclick="clearMySearchForm3()" />
			      </td>
			    </tr>
			</table>
			<div>
				<input type="hidden" name="pageNum" value="${pvo.pageNum }" />
				<input type="hidden" name="numPerPage" value="${pvo.pageSize }" />
			</div>
		</div>
	</form>

<!-- 	<form onsubmit="return navTabSearch(this);" action="demo_page1.html" method="post">
		<div class="searchBar">
			<table class="searchContent">
				<tr>
					<td>
						我的客户：<input type="text" name="keyword" />
					</td>
					<td>
						<select class="combox" name="province">
							<option value="">所有省市</option>
							<option value="北京">北京</option>
							<option value="上海">上海</option>
							<option value="天津">天津</option>
							<option value="重庆">重庆</option>
							<option value="广东">广东</option>
						</select>
					</td>
					<td>
						建档日期：<input type="text" class="date" readonly="true" />
					</td>
				</tr>
			</table>
			<div class="subBar">
				<ul>
					<li><div class="buttonActive"><div class="buttonContent"><button type="submit">检索</button></div></div></li>
					<li><a class="button" href="demo_page6.html" target="dialog" mask="true" title="查询框"><span>高级检索</span></a></li>
				</ul>
			</div>
		</div>
	</form>
 -->
</div>
<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
		</ul>
	</div>
	
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
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
			    <th>操作人</th>
			    <th>拉入黑名单时间</th>
			    <th>原因</th>
			    <th>原因分类</th>
				<th>操作</th>
				<th>导入时间</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty candidates }">
				<c:forEach items="${candidates}" var="candidate">
					<tr>
						<td><a href="resumeController.do?view&rid=${candidate.rid }" target="navTab" rel="view_resume_t" style="color:blue;" target="_Blank">${candidate.name }</a></td>
						<td>${candidate.department.NAME }</td>
						<td>${candidate.sector }</td>
						<td>${candidate.project_name }</td>
						<td>${candidate.occupation_title }</td>
						<td>${candidate.eduDegree }</td>
						<td>${candidate.major }</td>
						<td>${candidate.workYears }</td>
						<td>${candidate.interviewTime }</td>
						<td>${candidate.mobilePhone }</td>
						<td>${candidate.contact_email }</td>
						<td>${candidate.channel }</td>
						<td>${candidate.proposer }</td>
						<td>${candidate.operator }</td>
						<td>${candidate.blacklistTime }</td>
						<td>${candidate.reason }</td>
						<td>${candidate.category }</td>
						<td>
						<a href="resumeController.do?updateBlacklist&bid=${candidate.bid }&rid=${candidate.rid }" target="navTab" rel="blacklist_t" title="黑名单列表" style="text-decoration:none;font-weight:bold;color:#FF0000;" onclick="sf()">释放</a>
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
				<%-- <option value="1" <c:if test="${pvo.pageSize == 20}">selected</c:if>>1</option> --%>
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

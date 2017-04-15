<%@ page language="java" import="java.util.*,java.text.SimpleDateFormat" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	function cb() {
		alert('储备成功！请去我的储备资源添加再联系时间！');
	}
	function sf() {
		alert('释放成功！');
	}
	function resetForm($form) {
		//debugger;
	    $form.find('input:text, input:password, input:file, textarea').val('');
	    $form.find('input:radio, input:checkbox')
	         .removeAttr('checked').removeAttr('selected');
	    //var value = "请选择";
	    //$("select option[value='"+value+"']").attr("selected", "selected");
	    $('#status_sel_interviewed')[0].selectedIndex=0;
	}
	function clearMySearchForm_interviewed() {
		//debugger;
		// to call, use:
		resetForm($('#mySearchForm_interviewed')); // by id, recommended
	}
</script>
<%-- 
	<form id="pagerForm" method="post" action="demo_page1.html">
		<input type="hidden" name="status" value="${param.status}"> <input
			type="hidden" name="keywords" value="${param.keywords}" /> <input
			type="hidden" name="pageNum" value="1" /> <input type="hidden"
			name="numPerPage" value="${model.numPerPage}" /> <input type="hidden"
			name="orderField" value="${param.orderField}" />
	</form>
 --%>
 <form id="pagerForm" rel="searchForm" method="post" action="candidateController.do?search">
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
	<input type="hidden" name="channel" type="text" value="${channel }" />
	<input type="hidden" name="position" type="text" value="${position }" />
	<input type="hidden" name="status" type="text" value="${status }" />
	<%-- <input type="hidden" name="fuzzy_key" type="text" value="${fuzzy_key }" /> --%>
	<%-- <input type="hidden" name="recruiter_name" type="text" value="${recruiter_name }" /> --%>
	<input type="hidden" type="text" name="interviewTime_start" value="${interviewTime_start }">
	<input type="hidden" type="text" name="interviewTime_end" value="${interviewTime_end }">
	<input type="hidden" name="req_id" type="text" value="${req_id }" />
</form>
 

<div class="pageHeader">
	<!-- 注：这里form标签的onsubmit事件，写的是DWZ框架里的一个js函数调用，其作用是：navTabSearch函数调用完成后，会将action返回的页面数据在当前页面的当前tab标签下刷新显示出来，而不是刷新整个页面或弹出新页面显示！ -->
	<form onsubmit="return navTabSearch(this);" id="mySearchForm_interviewed" action="requirementController.do?search_interviewed_candidates" method="post">
		<div class="searchBar">
			<table class="searchContent" >
				<!-- <tr>
					<td colspan="5">&nbsp;<span
						style="font-weight:bold;color:#FF0000;">候选人</span></td>
				</tr> -->
				<tr>
					<td>&nbsp; 姓名：<input name="name" type="text" value="${name }" />
						&nbsp;身份证：<input name="identityNo" type="text" value="${identityNo }" /> &nbsp;手机号：<input
						name="mobilePhone" type="text" value="${mobilePhone }" /> &nbsp;渠道来源：<input
						name="channel" type="text" value="${channel }" /> &nbsp;状态： <select name="status" id="status_sel_interviewed">
	                    	<option <c:if test="${status == '请选择'}">selected</c:if>>请选择</option>
	                		<option <c:if test="${status == '待安排'}">selected</c:if>>待安排</option>
	                    	<option <c:if test="${status == '面试中'}">selected</c:if>>面试中</option>
	                		<option <c:if test="${status == 'Offer处理中'}">selected</c:if>>Offer处理中</option>
	                		<option <c:if test="${status == '不通过'}">selected</c:if>>不通过</option>
					</select> 
					</td>
				</tr>
				<tr>
					<td>
					&nbsp; 岗位：<input name="position" type="text" value="${position }" />
					&nbsp; 面试时间：
						<!-- <input type="text" name="date2" class="date" minDate="2000-01-15" maxDate="2012-12-15" readonly="true"/><a class="inputDateButton" href="javascript:;">选择</a> -->
						<input type="text" name="interviewTime_start" class="date" placeholder="开始日期" value="${interviewTime_start }" datefmt="yyyy-MM-dd" readonly="true" size="12">&nbsp;至
						<input type="text" name="interviewTime_end" class="date" placeholder="结束日期" value="${interviewTime_end }" datefmt="yyyy-MM-dd" readonly="true" size="12">&nbsp;&nbsp;
						<input type="submit" value="搜索" />&nbsp;&nbsp;&nbsp;<input type="button" value="清空重输" onclick="clearMySearchForm_interviewed()" />
					</td>
				</tr>
			</table>
			<div>
				<input type="hidden" name="req_id" type="text" value="${req_id }" />
			</div>
		</div>
	</form>
</div>
<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
			<!-- 
				<li><a class="edit" href="resumeController.do?importResume"
					target="dialog" mask="true" rel="my_candidates_t" title="我的候选人" fresh="false"><span>导入简历</span></a></li>
					
				<li><a
					class="edit" href="resumeController.do?updateResume&rid={rid}&cb=hxrsf"
					target="navTab" rel="my_candidates_t" title="我的候选人" onclick="sf()"><span>释放</span></a></li>
			 -->

			<!-- <li><a href="evaluationController.do?addEvaluation&rid={rid}"
				target="navTab" title="信息记录"><span
					style="text-decoration:none;font-weight:bold;color:#FF0000;">信息记录</span></a></li>  -->
			<!-- 
				<li>
					<a class="edit" href="candidateController.do?gotoModifyInterviewTime&rid={rid}" target="dialog" mask="true"><span>修改面试时间</span></a>
				</li>
			 -->
			<!-- 
				<li>
					<a class="edit" href="candidateController.do?gotoModifyPosition&rid={rid}" target="dialog" mask="true"><span>修改岗位</span></a>
				</li>
			 -->
		</ul>
	</div>

	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th> </th>
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
				<!-- 
					<th>推荐人</th>
				 -->
				<th>状态</th>
				<!-- 
					<th>操作</th>
				 -->
				<th>导入时间</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty candidates }">
				<c:forEach items="${candidates}" var="candidate">
					<tr target="rid" rel="${candidate.rid }">
						<td><input type="checkbox" /></td>
						<td><a href="resumeController.do?view&rid=${candidate.rid }"
							target="navTab" rel="view_resume_t" title="查看简历"
							style="color:blue;">${candidate.name }</a></td>
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
						<!-- 
							<td>${candidate.proposer }</td>
						 -->
						<td>${candidate.status }</td>
						<!-- 
							<td>
								<a
								href="resumeController.do?updateResume&rid=${candidate.rid }&cb=cb"
								target="navTab" rel="my_candidates_t" title="我的候选人"
								style="text-decoration:none;font-weight:bold;color:#FF0000;"
								onclick="cb()">储备 </a> <a
								href="resumeController.do?addBlacklist&rid=${candidate.rid }&cb=hmd"
								target="dialog" rel="my_candidates_t" mask="true" title="我的候选人"
								style="text-decoration:none;font-weight:bold;color:#FF0000;">加入黑名单</a>
							</td>
						 -->
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

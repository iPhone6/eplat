<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	function del_candidate(url, data) {
		alertMsg.confirm("请确认是否删除？", {
			okCall : function() {
				$.post(url, data, DWZ.ajaxDone, "json");
			},
			cancelCall : function() {
			}
		});
	}

	function resetForm4($form) {
		$form.find('input:text, input:password, input:file, textarea').val('');
		$form.find('input:radio, input:checkbox').removeAttr('checked')
				.removeAttr('selected');
		$('#regionId')[0].selectedIndex = 0;
		$('#departmentId')[0].selectedIndex = 0;
	}
	function clearMySearchForm4() {
		resetForm4($('#mySearchForm4')); // by id, recommended
	}
</script>

<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);"
		action="interviewerController.do?findInterviewer" method="post"
		id="mySearchForm4">
		<div class="searchBar">
			<table class="searchContent">
				<tr>
					<td colspan="5">&nbsp;<span
						style="font-weight:bold;color:#FF0000;">面试官查询</span></td>
				</tr>
				<tr>
					<td>&nbsp;姓名：<input name="name" type="text" value="${name }" />
						&nbsp;&nbsp;地区： <select name="regionId" id="regionId">
							<option value="0">请选择</option>
							<c:forEach items="${regionsList }" var="regions">
								<option value="${regions.id }"
									<c:if test="${regions.id == regionId }">selected</c:if>>${regions.NAME }</option>
							</c:forEach>
					</select> &nbsp;&nbsp; 部门： <select name="departmentId" id="departmentId">
							<option value="0">请选择</option>
							<c:forEach items="${departmentsList }" var="departments">
								<option value="${departments.id }"
									<c:if test="${departments.id == departmentId }">selected</c:if>>${departments.NAME }</option>
							</c:forEach>
					</select> &nbsp;&nbsp;技术方向：<input name="TECHNICAL_DIRECTION" type="text"
						value="${TECHNICAL_DIRECTION }" /> &nbsp;&nbsp;<input
						type="submit" value="搜索" /> &nbsp;&nbsp;&nbsp;<input
						type="button" value="清空重输" onclick="clearMySearchForm4()" />
					</td>
				</tr>
			</table>
		</div>
	</form>
</div>
<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
			<li><a class="edit"
				href="interviewerController.do?findInterviewerById&iId={id}"
				target="navTab" rel="modify_interviewer_tab" navTabId="modify_interviewer_tab" title="修改面试官"><span>修改</span></a></li>
		</ul>
	</div>

	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th></th>
				<th>姓名</th>
				<th>地区</th>
				<th>部门</th>
				<th>项目</th>
				<th>技术方向</th>
				<th>已面试人数</th>
				<th>未通过人数</th>
				<th>通过人数</th>
				<th>电话</th>
				<th>公司邮箱</th>
				<th>华为邮箱</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${interviewersList}" var="interviewers">
				<tr target="id" rel="${interviewers.id }">
					<td><input type="checkbox" /></td>
					<td>${interviewers.NAME }</td>
					<td>${interviewers.rname }</td>
					<td>${interviewers.dname }</td>
					<td>${interviewers.project }</td>
					<td>${interviewers.TECHNICAL_DIRECTION }</td>
					<td>N/A</td>
					<td>N/A</td>
					<td>N/A</td>
					<td>${interviewers.MOBILE_PHONE }</td>
					<td>${interviewers.ELEAD_EMAIL }</td>
					<td>${interviewers.HUAWEI_EMAIL }</td>
					<td>
						<%-- <a href="javascript:;"
							style="text-decoration:none;font-weight:bold;color:#FF0000;"
							onclick="del_candidate('interviewerController.do?deleteInterviewerJson&iId=${interviewers.id }')">删除</a> --%>
						<a href="interviewerController.do?deleteInterviewerJson&iId=${interviewers.id }"
							style="text-decoration:none;font-weight:bold;color:#FF0000;"
							target="ajaxTodo" title="确定要删除吗？" >删除</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

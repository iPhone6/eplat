<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<form id="pagerForm" method="post" action="QuestionBank.html">
	<input type="hidden" name="pageNum" value="1" /> <input type="hidden"
		name="numPerPage" value="${model.numPerPage}" /> <input type="hidden"
		name="orderField" value="${param.orderField}" /> <input type="hidden"
		name="orderDirection" value="${param.orderDirection}" />
</form>
<div class="pageHeader">
	<form name="pushlogsForm" rel="pagerForm"  onsubmit="return navTabSearch(this);" action="pushLogController.do?getAllPushLogs" method="post">
		<div class="searchBar">
			<label>开始日期:</label><input type="date" name="startdate"><br><br> 
			<label>结束日期:</label> <input type="date" name="enddate">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit" value="查找" />
		</div>
	</form>
</div>

<div class="pageContent">
	<div class="panelBar"></div>

	<div id="w_list_print">
		<table class="table" width="100%" layoutH="138">
			<thead>
				<tr>
					<th align="center">工号</th>
					<th align="center">姓名</th>
					<th align="center">状态</th>
					<th align="center">时间</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${!empty datas }">
					<c:forEach items="${datas}" var="data">
						<tr>
							<td>${data.work_no }</td>
							<td>${data.name }</td>
							<c:if test="${data.result}">
							<td style="color:green">成功</td>
							</c:if>
							<c:if test="${!data.result}">
							<td style="color:red">失败</td>
							</c:if>
							<td>${data.time }</td>
						</tr>
					</c:forEach>
				</c:if>
			</tbody>
		</table>
	</div>

	<!--<div class="panelBar" >-->
	<!--<div class="pages">-->
	<!--<span>显示</span>-->
	<!--<select name="numPerPage" onchange="navTabPageBreak({numPerPage:this.value})">-->
	<!--<option value="20">20</option>-->
	<!--<option value="50">50</option>-->
	<!--<option value="100">100</option>-->
	<!--<option value="200">200</option>-->
	<!--</select>-->
	<!--<span>条，共23条</span>-->
	<!--</div>-->

	<!--<div class="pagination" targetType="navTab" totalCount="200" numPerPage="20" pageNumShown="10" currentPage="2"></div>-->

	<!--</div>-->

</div>

<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<form id="pagerForm" method="post" action="demo_page1.html">
	<input type="hidden" name="status" value="${param.status}"> <input
		type="hidden" name="keywords" value="${param.keywords}" /> <input
		type="hidden" name="pageNum" value="1" /> <input type="hidden"
		name="numPerPage" value="${model.numPerPage}" /> <input type="hidden"
		name="orderField" value="${param.orderField}" />
</form>


<div class="pageHeader">
	<div class="searchBar">
		<!-- 注：这里form标签的onsubmit事件，写的是DWZ框架里的一个js函数调用，其作用是：navTabSearch函数调用完成后，会将action返回的页面数据在当前页面的当前tab标签下刷新显示出来，而不是刷新整个页面或弹出新页面显示！ -->
		<form>
			<table width="100%" border="0">
				<tr>
					<td colspan="5">&nbsp;<span
						style="font-weight:bold;color:#FF0000;">用 户 列 表</span></td>
				</tr>
				<tr>
					<td>&nbsp;用户名：<input name="text" type="text" /> <input
						type="button" value="搜索" /></td>
				</tr>
				<tr>
					<td><br />&nbsp;</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
			<c:if test="${sessionScope.user.rname  == '招聘主管' || sessionScope.user.rname  == '系统管理员'}">
				<li><a class="add"
					href="<%=basePath%>menuController.do?gotoAddUser" target="dialog"
					rel="add_user_dialog" mask="true"><span>添加用户</span></a></li>
			</c:if>
			<li><a class="edit"
				href="menuController.do?gotoUpdateUser&uid={user_id}"
				target="dialog" mask="true"><span>修改用户</span></a></li>

		</ul>
	</div>

	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th>用户名</th>
				<th>手机号</th>
				<th>邮箱号</th>
				<th>角色</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty users }">
				<c:forEach items="${users}" var="usersi">
					<tr target="user_id" rel="${usersi.userId }">
						<td>${usersi.userName }</td>
						<td>${usersi.telephone }</td>
						<td>${usersi.eleadEmail }</td>
						<td>${usersi.rname }</td>
						<td>${usersi.creator }</td>
						<td>${usersi.createTime }</td>
						<td><c:if
								test="${usersi.userName == sessionScope.user.userName }">
								<a class="edit"
									href="menuController.do?gotoUpdatePassword&uid=${usersi.userId }"
									target="dialog" mask="true"
									style="text-decoration:none;font-weight:bold;color:#FF0000;">修改密码</a>
							</c:if></td>
					</tr>
				</c:forEach>
			</c:if>
		</tbody>
	</table>
</div>

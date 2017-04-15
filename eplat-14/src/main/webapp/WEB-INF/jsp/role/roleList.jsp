<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<script type="text/javascript">
	function del_candidate(url,data) {
		alertMsg.confirm("请确认是否删除？", {
			okCall: function() {
				$.post(url,data,DWZ.ajaxDone,"json");
			},
			cancelCall: function() {
			}
		});
	}
</script>


<div class="pageHeader">
	<div class="searchBar">
		<!-- 注：这里form标签的onsubmit事件，写的是DWZ框架里的一个js函数调用，其作用是：navTabSearch函数调用完成后，会将action返回的页面数据在当前页面的当前tab标签下刷新显示出来，而不是刷新整个页面或弹出新页面显示！ -->
		<form>
			<table width="100%" border="0">
			    <tr>
			      <td colspan="5">&nbsp;<span style="font-weight:bold;color:#FF0000;">角色列 表</span></td>
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
				<li><a class="add" href="<%=basePath %>loginController.do?addRole" target="dialog" rel="role_list_t" mask="true"><span>添加角色</span></a></li>
				<li><a class="edit" href="loginController.do?updaetRole&rid={rid}" target="dialog" rel="role_list_t" mask="true"><span>修改角色</span></a></li>
		</ul>
	</div>
	
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th>角色名</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${!empty roles }">
				<c:forEach items="${roles}" var="roles">
					<tr target="rid" rel="${roles.id }">
						<td>${roles.name }</td>
						<td>${roles.creator }</td>
						<td>${roles.createTime }</td>
						<td>
							<%-- <a href="javascript:;"
								style="text-decoration:none;font-weight:bold;color:#FF0000;"  onclick="del_candidate('loginController.do?deleteRoleJson&rid=${roles.id }')"><span>删除</span></a> --%>
							<a href="loginController.do?deleteRoleJson&rid=${roles.id }" target="ajaxTodo" title="确定要删除吗？"
								style="text-decoration:none;font-weight:bold;color:#FF0000;" >删除</a>
						</td>
					</tr>				
				</c:forEach>
			</c:if>
		</tbody>
	</table>
</div>

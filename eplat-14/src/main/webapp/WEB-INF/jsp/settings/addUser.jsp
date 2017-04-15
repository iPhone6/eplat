<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="pageContent">
	<form method="post" action="<%=basePath%>menuController.do?addUserJson" name="userForm" class="pageForm required-validate" onsubmit="return validateCallback(this, dialogAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<p>
				<label>用 户 名：</label>
				<input name="userName" class="required" type="text" size="30" alt="请输入用户名" />
			</p>
			<p>
				<label>密       码：</label>
				<input id="validate_mima" class="required" name="mima" type="password" size="30" alt="请输入密码"/>
			</p>
			<p>
				<label>确认密码：</label>
				<input name="repeat_mima" class="required" equalto="#validate_mima" type="password" size="30" alt="请输入密码"/>
			</p>
			<p>
				<label>角       色：</label>
				<select name = "roleId"  class="required">
					<option value="">请选择</option>
					<c:forEach items="${roles }" var="roles">
						<option value="${roles.id }">${roles.name }</option>
					</c:forEach>
				</select>
			</p>
			<p>
				<label>手  机  号：</label>
				<input name="telephone" class="phone" type="text" size="30" alt="请输入手机号"/>
			</p>
			<p>
				<label>公司邮箱：</label>
				<input name="eleadEmail" class="email" type="text" size="30" alt="请输入邮箱"/>
			</p>
		</div>
		<div class="formBar">
			<ul>
				<!--<li><a class="buttonActive" href="javascript:;"><span>保存</span></a></li>-->
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">保存</button></div></div></li>
				<li>
					<div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div>
				</li>
			</ul>
		</div>
	</form>
</div>



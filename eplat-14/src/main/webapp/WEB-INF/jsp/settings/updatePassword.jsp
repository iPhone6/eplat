<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="pageContent">
	<form method="post" action="<%=basePath%>menuController.do?updatePasswordJson" name="userForm" class="pageForm required-validate" onsubmit="return validateCallback(this, dialogAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<p>
				<label>用 户 名：</label>
				<input name="userName" type="text" readonly="readonly" size="30" value="${user_upd.userName }" />
				<input name="userId" type="hidden" value="${user_upd.userId }" >
			</p>
			<p>
				<label>旧 密  码：</label>
				<input name="old_password" type="password" size="30" value="" class="required" />
			</p>
			<p>
				<label>新 密 码：</label>
				<input name="new_password" type="password" size="30" value="" class="required" id="validate_new_password" />
			</p>
			<p>
				<label>确认新密码：</label>
				<input name="confirm_new_password" type="password" size="30" value="" class="required" equalto="#validate_new_password" />
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



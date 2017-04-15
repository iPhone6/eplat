<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="pageContent">
	<form method="post"
		action="<%=basePath%>menuController.do?updateUserJson" name="userForm"
		class="pageForm required-validate"
		onsubmit="return validateCallback(this, dialogAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<p>
				<label>用 户 名：</label> <input name="userName" type="text"
					readonly="readonly" size="30" value="${user_upd.userName }" /> <input
					name="userId" type="hidden" value="${user_upd.userId }">
			</p>

			<c:if
				test="${sessionScope.user.rname  == '招聘主管' || sessionScope.user.rname  == '系统管理员'}">
				<p>
					<label>角 色：</label> <select name="roleId"
						onfocus="this.defOpt=this.selectedIndex"
						onchange="this.selectedIndex=this.defOpt;">
						<option value="">请选择</option>
						<c:forEach items="${roles }" var="roles">
							<option value="${roles.id }"
								<c:if test="${roles.name == user_upd.rname }">selected</c:if>>${roles.name }</option>
						</c:forEach>
					</select>
				</p>
				<p>
					<label>手 机 号：</label> <input name="telephone" type="text" size="30"
						alt="请输入手机号" value="${user_upd.telephone }" class="phone" />
				</p>
				<p>
					<label>公司邮箱：</label> <input name="eleadEmail" type="text" size="30"
						alt="请输入邮箱" value="${user_upd.eleadEmail }" class="email" />
				</p>
			</c:if>

			<c:if
				test="${sessionScope.user.rname  != '招聘主管' && sessionScope.user.rname  != '系统管理员'}">

				<c:if test="${sessionScope.user.userName  == user_upd.userName }">
					<p>
						<label>角 色：</label> <select name="roleId"
							onfocus="this.defOpt=this.selectedIndex"
							onchange="this.selectedIndex=this.defOpt;">
							<option value="">请选择</option>
							<c:forEach items="${roles }" var="roles">
								<option value="${roles.id }"
									<c:if test="${roles.name == user_upd.rname }">selected</c:if>>${roles.name }</option>
							</c:forEach>
						</select>
					</p>
					<p>
						<label>手 机 号：</label> <input name="telephone" type="text"
							size="30" alt="请输入手机号" value="${user_upd.telephone }"
							class="phone" />
					</p>
					<p>
						<label>公司邮箱：</label> <input name="eleadEmail" type="text"
							size="30" alt="请输入邮箱" value="${user_upd.eleadEmail }"
							class="email" />
					</p>
				</c:if>

				<c:if test="${sessionScope.user.userName  != user_upd.userName }">
					<p>
						<label>角 色：</label> <select name="roleId"
							onfocus="this.defOpt=this.selectedIndex"
							onchange="this.selectedIndex=this.defOpt;">
							<option value="">请选择</option>
							<c:forEach items="${roles }" var="roles">
								<option value="${roles.id }"
									<c:if test="${roles.name == user_upd.rname }">selected</c:if>>${roles.name }</option>
							</c:forEach>
						</select>
					</p>
					<p>
						<label>手 机 号：</label> <input name="telephone" type="text"
							readonly="readonly" size="30" value="${user_upd.telephone }"
							class="phone" />
					</p>
					<p>
						<label>公司邮箱：</label> <input name="eleadEmail" type="text"
							readonly="readonly" size="30" value="${user_upd.eleadEmail }"
							class="email" />
					</p>
				</c:if>
			</c:if>
		</div>
		<div class="formBar">
			<ul>
				<!--<li><a class="buttonActive" href="javascript:;"><span>保存</span></a></li>-->
				<li><div class="buttonActive">
						<div class="buttonContent">
							<button type="submit">保存</button>
						</div>
					</div></li>
				<li>
					<div class="button">
						<div class="buttonContent">
							<button type="button" class="close">取消</button>
						</div>
					</div>
				</li>
			</ul>
		</div>
	</form>
</div>



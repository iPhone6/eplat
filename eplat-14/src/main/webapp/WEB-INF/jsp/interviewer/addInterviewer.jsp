<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>



<div class="pageContent">
<%-- 	<form onsubmit="return navTabSearch(this);"
			action="interviewerController.do?addInterviewerT" method="post">
	<form method="post" action="requirementController.do?saveRequirementJson" class="pageForm required-validate" onsubmit="return validateCallback(this, navTabAjaxDone);"> --%>
	<form method="post" action="interviewerController.do?addInterviewerT" class="pageForm required-validate" onsubmit="return validateCallback(this, navTabAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<p>
				<label>姓名：</label> <input name="NAME" type="text" size="30"
					alt="请输入用户名" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>地区：</label> <select name="regionId">
					<c:forEach items="${regionsList }" var="regions">
						<option value="${regions.id }">${regions.NAME }</option>
					</c:forEach>
				</select>
			</p>
			<div class="divider"></div>
			<p>
				<label>部门：</label> <select name="departmentId">
					<c:forEach items="${departmentsList }" var="departments">
						<option value="${departments.id }">${departments.NAME }</option>
					</c:forEach>
				</select>
			</p>
			<div class="divider"></div>
			<p>
				<label>项目：</label> <input name="project" type="text" size="30"
					alt="请输入项目名称" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>手机号：</label> <input name="MOBILE_PHONE" type="text" size="30"
					alt="请输入手机号" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>公司邮箱：</label> 
				<input name="ELEAD_EMAIL" type="text" size="30"
					alt="请输入公司邮箱@e-lead.cn" class="required" />
					
			</p>
			<div class="divider"></div>
			<p>
				<label>华为邮箱：</label> <input name="HUAWEI_EMAIL" type="text"
					size="30" alt="请输入华为邮箱" />
			</p>
			<div class="divider"></div>
			<p>
				<label>技术方向：</label> <input name="TECHNICAL_DIRECTION" type="text"
					size="30" alt="请输入面试官技术方向" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label></label> 
				<input type="submit" value="提交"  />
			</p>

		</div>
		<div class="formBar">
			<ul>
				<!-- <li><div class="buttonActive"><div class="buttonContent"><button type="submit">提交</button></div></div></li> -->
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

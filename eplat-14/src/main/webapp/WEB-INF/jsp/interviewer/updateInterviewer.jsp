<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>



<div class="pageContent">
	<div class="pageFormContent" layoutH="56">
			<form method="post" action="interviewerController.do?findInterviewerByIdUpdate" class="pageForm required-validate" onsubmit="return validateCallback(this, navTabAjaxDone);">

			<p>
				<label>姓名：</label> <input name="NAME" type="text" size="30"
					value="${interviewers.NAME }" />
					<input name="iId" type="hidden" size="30"
					value="${interviewers.id }" />
			</p>
			<div class="divider"></div>
			<p>
				<label>地区：</label> <select name="regionId">
					<c:forEach items="${regionsList }" var="regions">
						<option value="${regions.id }"
							<c:if test="${regions.NAME == interviewers.rname }">selected</c:if>>${regions.NAME }</option>
					</c:forEach>
				</select>
			</p>
			<div class="divider"></div>
			<p>
				<label>部门：</label> <select name="departmentId">
					<c:forEach items="${departmentsList }" var="departments">
						<option value="${departments.id }"
							<c:if test="${departments.NAME == interviewers.dname }">selected</c:if>>${departments.NAME }</option>
					</c:forEach>
				</select>
			</p>
			<div class="divider"></div>
			<p>
				<label>项目：</label> <input name="project" type="text" size="30"
					value="${interviewers.project }" />
			</p>
			<div class="divider"></div>
			<p>
				<label>手机号：</label> <input name="MOBILE_PHONE" type="text" size="30"
					value="${interviewers.MOBILE_PHONE }" />
			</p>
			<div class="divider"></div>
			<p>
				<label>公司邮箱：</label> <input name="ELEAD_EMAIL" type="text" size="30"
					value="${interviewers.ELEAD_EMAIL }" />

			</p>
			<div class="divider"></div>
			<p>
				<label>华为邮箱：</label> <input name="HUAWEI_EMAIL" type="text"
					size="30" value="${interviewers.HUAWEI_EMAIL }" />
			</p>
			<div class="divider"></div>
			<p>
				<label>技术方向：</label> <input name="TECHNICAL_DIRECTION" type="text"
					size="30" value="${interviewers.TECHNICAL_DIRECTION }" />
			</p>
			<div class="divider"></div>
			<p>
				<label></label> <input type="submit" value="提交" />
			</p>
			</form>
	</div>
	<div class="formBar">
		<ul>
			<li>
				<div class="button">
					<div class="buttonContent">
						<button type="button" class="close">取消</button>
					</div>
				</div>
			</li>
		</ul>
	</div>
</div>

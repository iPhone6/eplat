<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="pageContent">
	<form method="post" action="<%=basePath%>candidateController.do?modifyInterviewTimeJson" name="userForm" class="pageForm required-validate" onsubmit="return validateCallback(this, dialogAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<div class="unit">
				<label>面试时间：</label>
				<input type="text" name="interviewTime" class="date textInput readonly valid" datefmt="yyyy-MM-dd HH:mm:ss" readonly="true">
				<a class="inputDateButton" href="javascript:;">选择</a>
				<!-- <span class="info">yyyy-MM-dd HH:mm</span> -->
			</div>
			<div>
				<input name="rid" type="hidden" value="${rid }" >
			</div>
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



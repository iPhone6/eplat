<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="pageContent">
	<%-- 
	<form method="post" action="<%=basePath%>candidateController.do?modifyInterviewTimeJson" name="userForm" class="pageForm required-validate" onsubmit="return validateCallback(this, dialogAjaxDone);">
	 --%>
	<form method="post" action="offerController.do?insertOfferJson"
		class="pageForm required-validate"
		onsubmit="return validateCallback(this, dialogAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<div class="divider"></div>
			<p>
				<label>试用期：</label> <input type="text" name="TRIAL_TIME" /> <input
					type="hidden" name="rid" value="${rid }" />
			</p>
			<div class="divider"></div>
			<p>
				<label>试用期工资：</label> <input type="text" name="TRIAL_SALARY"
					class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>转正工资：</label> <input type="text" name="FULL_DUTY_SALARY"
					class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>社保是否有特殊要求：</label> <input type="text" name="SOCIAL" />
			</p>
			<div class="divider"></div>
			<p class="nowrap">
				<label>贴发票：</label> <input type="text" name="SUPPLEMENT_RECEIPT" />
				<!-- <textarea rows="6" cols="100" name="SUPPLEMENT_RECEIPT" ></textarea> -->
			</p>
			<div class="divider"></div>
			<p>
				<label>预计到岗时间：</label> <input type="text" name="REQUIRE_DUTY_TIME"
					class="date" datefmt="yyyy-MM-dd" readonly="true">
			</p>
			<div class="divider"></div>

		</div>

		<div class="formBar">
			<ul>
				<li><div class="buttonActive">
						<div class="buttonContent">
							<button type="submit">提交</button>
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

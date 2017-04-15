<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="pageHeader">
	<div class="searchBar">
		<form onsubmit="return validateCallback(this, dialogAjaxDone);"
			action="resumeController.do?updateEvaluationContenJson" method="post">
			<div class="pageFormContent" layoutH="56">
				<input name="cid" type="hidden" value="${cid }"/>
					<p>
				<label>评价：</label>
				<textarea cols="45" rows="5" name="content"></textarea>
			</p>
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
</div>

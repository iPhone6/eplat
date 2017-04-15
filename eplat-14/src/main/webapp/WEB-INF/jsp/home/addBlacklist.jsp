<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="pageContent">

	<form onsubmit="return validateCallback(this, dialogAjaxDone);" action="resumeController.do?addBlacklistNOJson" method="post">
		<div class="pageFormContent" layoutH="56">
			<input name="rid" type="hidden" value="${rid }" />
			<p>
				<label>加入黑名单原因：</label>
				<input name="yy" type="text" />
			</p>
			<p>
				<label>加入黑名单原因分析：</label>
				<select name = "fenx">
					<option value="地区问题">地区问题</option>
					<option value="薪资问题">薪资问题</option>
				</select>
			</p>
		</div>
		<div class="formBar">
			<ul>
				<!--<li><a class="buttonActive" href="javascript:;"><span>保存</span></a></li>-->
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">提交</button></div></div></li>
				<li>
					<div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div>
				</li>
			</ul>
		</div>
	</form>

</div>

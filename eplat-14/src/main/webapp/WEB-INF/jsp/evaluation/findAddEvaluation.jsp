<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>



<div class="pageContent">
	<form onsubmit="return navTabSearch(this);"
		action="evaluationController.do?findAddEvaluation" method="post">
		<div class="pageFormContent" layoutH="56">
			<dl class="nowrap">
				<dt style="text-decoration:none;font-weight:bold;color:#FF0000;">信息记录</dt>
				<dd></dd>
			</dl>
			<dl>
				<dt>
					<input type="hidden" id="rid" name="rid" value="${rid }" />
				</dt>
				<dd>&nbsp;&nbsp;候选人：${user.userName }</dd>
			</dl>
			<div class="divider"></div>
			<div class="divider"></div>
			<c:forEach items="${evaluation}" var="evaluation">
				<dl class="nowrap">
					<dt></dt>
					<dd>&nbsp;&nbsp;${evaluation.EVALUATER }：${evaluation.CONTENT }</dd>
					<dd>&nbsp;&nbsp;评价日期：${ evaluation.TIME}</dd>
				</dl>
				<div class="divider"></div>
			</c:forEach>


			<div class="divider"></div>
			<dl class="nowrap">
				<dt>评 &nbsp;&nbsp;&nbsp;&nbsp;价：</dt>
				<dd>
					<textarea cols="45" rows="5" name="content"></textarea>
				</dd>
			</dl>
			<dl class="nowrap">
				<dt></dt>
				<dd>
					<input type="submit" value="提交" />
				</dd>
			</dl>
		</div>
	</form>
	<div class="formBar"></div>

</div>

<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="pageContent">
	<form method="post" action="<%=basePath%>requirementController.do?assignRecruiterJson" name="userForm" class="pageForm required-validate" onsubmit="return validateCallback(this, dialogAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<div class="unit">
				<%-- <label>请选择招聘专员：</label>
				<select name="recruiter_id" class="combox" >
					<c:if test="${!empty recruiters }">
						<c:forEach items="${recruiters}" var="recruiter">
							<option value="${recruiter.userId }">${recruiter.userName }</option>
						</c:forEach>
					</c:if>
				</select> --%>
				<fieldset>
					<legend>选择招聘专员：</legend>
					<%-- 
						<dl class="nowrap">
							<dt>&nbsp;&nbsp;&nbsp;&nbsp;1号：</dt>
								<select name="recruiter_id" class="combox" >
									<c:if test="${!empty recruiters }">
										<c:forEach items="${recruiters}" var="recruiter">
											<option value="${recruiter.userId }">${recruiter.userName }</option>
										</c:forEach>
									</c:if>
								</select>
						</dl>
						<dl class="nowrap">
							<dt>&nbsp;&nbsp;&nbsp;&nbsp;2号（暂时无效）：</dt>
								<select name="recruiter_id2" class="combox" >
									<c:if test="${!empty recruiters }">
										<c:forEach items="${recruiters}" var="recruiter">
											<option value="${recruiter.userId }">${recruiter.userName }</option>
										</c:forEach>
									</c:if>
								</select>
						</dl>
						<dl class="nowrap">
							<dt>&nbsp;&nbsp;&nbsp;&nbsp;3号（暂时无效）：</dt>
								<select name="recruiter_id3" class="combox" >
									<c:if test="${!empty recruiters }">
										<c:forEach items="${recruiters}" var="recruiter">
											<option value="${recruiter.userId }">${recruiter.userName }</option>
										</c:forEach>
									</c:if>
								</select>
						</dl>
					 --%>
					<c:if test="${!empty recruiters }">
						<c:forEach items="${recruiters}" var="recruiter">
							<label><input type="checkbox" name="check_recruiter" value="${recruiter.userId }" />${recruiter.userName }</label>
						</c:forEach>
					</c:if>
					<!-- 
						<label><input type="checkbox" name="c1" value="1" />选择1</label>
						<label><input type="checkbox" name="c1" value="2" />选择2</label>
						<label><input type="checkbox" name="c1" value="3" />选择3</label>
						<label><input type="checkbox" name="c1" value="4" />选择4</label>
						<label><input type="checkbox" name="c1" value="5" />选择5</label>
						<label><input type="checkbox" name="c1" value="6" />选择6</label>
						<label><input type="checkbox" name="c1" value="7" />选择7</label>
						<label><input type="checkbox" name="c1" value="8" />选择8</label>
						<label><input type="checkbox" name="c1" value="9" />选择9</label>
						<label><input type="checkbox" name="c1" value="10" />选择10</label>
					 -->
				</fieldset>
			</div>
			<div>
				<input name="req_id" type="hidden" value="${req_id }" >
			</div>
		</div>
		<div class="formBar">
			<label style="float:left"><input type="checkbox" class="checkboxCtrl" group="check_recruiter" />全选</label>
			<span>&nbsp;&nbsp;&nbsp;</span>
			<div class="button"><div class="buttonContent"><button type="button" class="checkboxCtrl" group="check_recruiter" selectType="invert">反选</button></div></div>
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



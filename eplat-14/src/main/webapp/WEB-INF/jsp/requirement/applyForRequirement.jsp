<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="pageContent">
	<form method="post" action="requirementController.do?saveRequirementJson" class="pageForm required-validate" onsubmit="return validateCallback(this, navTabAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<p>
				<label>地 区：</label>
				<select name="region" class="combox" >
					<c:if test="${!empty regions }">
						<c:forEach items="${regions}" var="region">
							<option value="${region.id }">${region.NAME }</option>
						</c:forEach>
					</c:if>
				</select>
			</p>
			<div class="divider"></div>
			<p>
				<label>一级部门：</label>
				<select name="department" class="combox" id="sel_department" >
					<c:if test="${!empty departments }">
						<c:forEach items="${departments}" var="department">
							<option value="${department.id }">${department.NAME }</option>
						</c:forEach>
					</c:if>
				</select>
			</p>
			<!-- 
				<select class="combox" name="province" ref="w_combox_city" refUrl="demo/combox/city_{value}.html">
					<option value="all">所有省市</option>
					<option value="bj">北京</option>
					<option value="sh">上海</option>
					<option value="zj">浙江省</option>
				</select>
				<select class="combox" name="city" id="w_combox_city" ref="w_combox_region" refUrl="demo/combox/region_{value}.html">
					<option value="all">所有城市</option>
				</select>
				<select class="combox" name="region" id="w_combox_region">
					<option value="all">所有区县</option>
				</select>
			 -->
			<div class="divider"></div>
			<p>
				<label>二级部门：</label>
				<input type="text" name="sector" />
			</p>
			<div class="divider"></div>
			<p>
				<label>项目组：</label>
				<input type="text" name="Project_name" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>岗 位：</label>
				<input type="text" name="position" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>工作经验：</label>
				<input type="text" class="required digits" name="work_exp" alt="请输入数字" />
				<span class="info">（单位：年）</span>
			</p>
			<div class="divider"></div>
			<p>
				<label>级别要求：</label>
				<input type="text" name="level_require" class="required" />
			</p>
			<div class="divider"></div>
			<p class="nowrap">
				<label>岗位要求：</label>
				<textarea rows="6" cols="100" name="position_require" class="required" ></textarea>
			</p>
			<div class="divider"></div>
			<p>
				<label>部门主管：</label>
				<input type="text" name="Department_header" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>项目负责人：</label>
				<input type="text" name="Project_header" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>部门面试官：</label>
				<select name="interviewer">
					<c:if test="${!empty interviewers }">
						<c:forEach items="${interviewers}" var="interviewer">
							<option value="${interviewer.id }">${interviewer.NAME }</option>
						</c:forEach>
					</c:if>
		        </select>
			</p>
			<div class="divider"></div>
			<p>
				<label>计划招聘人数：</label>
				<input type="text" class="required digits" name="require_num" alt="请输入数字" />
				<span class="info">（单位：人）</span>
			</p>
			<div class="divider"></div>
			<p>
				<label>要求到岗时间：</label>
				<input type="text" name="REQUIRE_DUTY_TIME" class="date" datefmt="yyyy-MM-dd" readonly="true" >
			</p>
			<div class="divider"></div>
			<p>
				<label>优先级别：</label>
				<select name="level" class="combox">
					<option value="3级">3级</option>
					<option value="2级">2级</option>
					<option value="1级">1级</option>
				</select>
				<span class="info">（注：3级最低）</span>
			</p>
			<div class="divider"></div>
	
		</div>
	
		<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">提交</button></div></div></li>
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

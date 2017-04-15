<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="pageContent">
	<form method="post" action="requirementController.do?modifyRequirementJson" class="pageForm required-validate" onsubmit="return validateCallback(this, navTabAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<p>
				<%-- <label>优先级别：</label>
				<input type="text" name="sector" value="${requirement.level }" readonly="readonly" /> --%>
				<label>优先级别：</label>
				<select name="level" class="combox">
					<%-- <option <c:if test="${status == '请选择'}">selected</c:if>>请选择</option> --%>
					<option <c:if test="${requirement.level == '3级'}">selected</c:if> value="3级">3级</option>
					<option <c:if test="${requirement.level == '2级'}">selected</c:if> value="2级">2级</option>
					<option <c:if test="${requirement.level == '1级'}">selected</c:if> value="1级">1级</option>
				</select>
				<span class="info">（注：3级最低）</span>
			</p>
			<div class="divider"></div>
			<p>
				<label>地 区：</label>
				<%-- <input type="text" name="region" value="${requirement.region_name }" readonly="readonly" /> --%>
				<select name="region" class="combox" >
					<c:if test="${!empty regions }">
						<c:forEach items="${regions}" var="region_iter">
							<option <c:if test="${region_iter.id == region.id}">selected</c:if> value="${region_iter.id }">${region_iter.NAME }</option>
						</c:forEach>
					</c:if>
				</select>
			</p>
			<div class="divider"></div>
			<p>
				<label>一级部 门：</label>
				<%-- <input type="text" name="department" value="${requirement.dept_name }" readonly="readonly" /> --%>
				<select name="department" class="combox" id="sel_department" >
					<c:if test="${!empty departments }">
						<c:forEach items="${departments}" var="department_iter">
							<option <c:if test="${department_iter.id == department.id}">selected</c:if> value="${department_iter.id }">${department_iter.NAME }</option>
						</c:forEach>
					</c:if>
				</select>
			</p>
			<div class="divider"></div>
			<p>
				<label>二级部 门：</label>
				<%-- <input type="text" name="sector" value="${requirement.sector }" readonly="readonly" /> --%>
				<input type="text" name="sector" value="${requirement.sector }" />
			</p>
			<div class="divider"></div>
			<p>
				<label>项目组：</label>
				<%-- <input type="text" name="Project_name" value="${requirement.project_name }" readonly="readonly" /> --%>
				<input type="text" name="Project_name" value="${requirement.project_name }" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>岗 位：</label>
				<%-- <input type="text" name="position" value="${requirement.position }" readonly="readonly" /> --%>
				<input type="text" name="position" value="${requirement.position }" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>工作经验：</label>
				<%-- <input type="text" name="work_exp" value="${requirement.work_exp } 年" readonly="readonly" /> --%>
				<input type="text" class="required digits" name="work_exp" alt="请输入数字" value="${requirement.work_exp }" class="required" />
				<span class="info">（单位：年）</span>
			</p>
			<div class="divider"></div>
			<p>
				<label>级别要求：</label>
				<%-- <input type="text" name="level_require" value="${requirement.level_require }" readonly="readonly" /> --%>
				<input type="text" name="level_require" value="${requirement.level_require }" class="required" />
			</p>
			<div class="divider"></div>
			<p class="nowrap">
				<label>岗位要求：</label>
				<%-- <textarea rows="6" cols="100" name="position_require" value="${requirement.position_require }" readonly="readonly" ></textarea> --%>
				<textarea rows="6" cols="100" name="position_require" class="required" >${requirement.position_require }</textarea>
			</p>
			<div class="divider"></div>
			<p>
				<label>计划申请人数：</label>
				<%-- <input type="text" name="require_num" value="${requirement.require_num } 人" readonly="readonly" /> --%>
				<input type="text" class="required digits" name="require_num" alt="请输入数字" value="${requirement.require_num }" />
				<span class="info">（单位：人）</span>
			</p>
			<div class="divider"></div>
			<p>
				<label>要求到岗时间：</label>
				<%-- <input type="text" name="REQUIRE_DUTY_TIME" value="${requirement.require_duty_time_formated }" readonly="true" > --%>
				<input type="text" name="REQUIRE_DUTY_TIME" class="required date" datefmt="yyyy-MM-dd" readonly="true" value="${requirement.require_duty_time_formated }" >
			</p>
			<div class="divider"></div>
			<p>
				<label>部门主管：</label>
				<%-- <input type="text" name="Department_header" value="${requirement.department_header }" readonly="readonly" /> --%>
				<input type="text" name="Department_header" value="${requirement.department_header }" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>项目负责人：</label>
				<%-- <input type="text" name="Project_header" value="${requirement.project_header }" readonly="readonly" /> --%>
				<input type="text" name="Project_header" value="${requirement.project_header }" class="required" />
			</p>
			<div class="divider"></div>
			<p>
				<label>面试官：</label>
				<%-- <input type="text" name="" value="${requirement.interviewer_name }" readonly="readonly" /> --%>
				<select name="interviewer" class="combox" >
					<c:if test="${!empty interviewers }">
						<c:forEach items="${interviewers}" var="interviewer_iter">
							<option <c:if test="${interviewer_iter.id == interviewer.id}">selected</c:if> value="${interviewer_iter.id }">${interviewer_iter.NAME }</option>
						</c:forEach>
					</c:if>
		        </select>
			</p>
			<%-- 
				<p>
					<label>公司邮箱：</label>
					<input type="text" name="" value="${interviewer.ELEAD_EMAIL }" readonly="readonly" /><br/>
				</p>
				<p>
					<label>华为邮箱：</label>
					<input type="text" name="" value="${interviewer.HUAWEI_EMAIL }" readonly="readonly" /><br/>
				</p>
				<p>
					<label>电话：</label>
					<input type="text" name="" value="${interviewer.MOBILE_PHONE }" readonly="readonly" />
				</p> 
				<div class="divider"></div>
			--%>
			<%-- 
				<p>
					<label>招聘专员：</label>
					<input type="text" name="" value="" readonly="readonly" />
				</p>
				<p>
					<label>邮箱：</label>
					<input type="text" name="" value="" readonly="readonly" />
				</p>
				<p>
					<label>电话：</label>
					<input type="text" name="" value="" readonly="readonly" />
				</p>
				<div class="divider"></div>
				<p>
					<label>申请人：</label>
					<input type="text" name="interviewer" value="${requirement.applicant }" readonly="readonly" />
				</p>
			 --%>
			<div class="divider"></div>
			<div>
				<input type="hidden" name="req_id" value="${req_id}">
			</div>
		</div>
	
		<div class="formBar">
			<ul>
				<!-- 
				 -->
					<li><div class="buttonActive"><div class="buttonContent"><button type="submit">保存</button></div></div></li>
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

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
				<label>优先级别：</label>
				<input type="text" name="sector" value="${requirement.level }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>地 区：</label>
				<input type="text" name="region" value="${requirement.region_name }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>一级部 门：</label>
				<input type="text" name="department" value="${requirement.dept_name }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>二级部 门：</label>
				<input type="text" name="level" value="${requirement.sector }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>项目组：</label>
				<input type="text" name="Project_name" value="${requirement.project_name }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>岗 位：</label>
				<input type="text" name="position" value="${requirement.position }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>工作经验：</label>
				<input type="text" name="work_exp" value="${requirement.work_exp } 年" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>级别要求：</label>
				<input type="text" name="level_require" value="${requirement.level_require }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p class="nowrap">
				<label>岗位要求：</label>
				<textarea rows="6" cols="100" name="position_require" readonly="readonly" >${requirement.position_require }</textarea>
				<%-- <input type="text" name="" value="${requirement.position_require }" readonly="readonly" /> --%>
			</p>
			<div class="divider"></div>
			<p>
				<label>计划申请人数：</label>
				<input type="text" name="require_num" value="${requirement.require_num } 人" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>要求到岗时间：</label>
				<input type="text" name="REQUIRE_DUTY_TIME" value="${requirement.require_duty_time_formated }" readonly="true" >
			</p>
			<div class="divider"></div>
			<p>
				<label>部门主管：</label>
				<input type="text" name="Department_header" value="${requirement.department_header }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>项目负责人：</label>
				<input type="text" name="Project_header" value="${requirement.project_header }" readonly="readonly" />
			</p>
			<div class="divider"></div>
			<p>
				<label>面试官：</label>
				<input type="text" name="" value="${requirement.interviewer_name }" readonly="readonly" />
			</p>
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
			<c:if test="${!empty recruiters }">
				<c:forEach items="${recruiters }" var="recruiter" >
					<p>
						<label>招聘专员：</label>
						<input type="text" name="" value="${recruiter.userName }" readonly="readonly" />
					</p>
					<p>
						<label>邮箱：</label>
						<input type="text" name="" value="${recruiter.eleadEmail }" readonly="readonly" />
					</p>
					<p>
						<label>电话：</label>
						<input type="text" name="" value="${recruiter.telephone }" readonly="readonly" />
					</p>
					<div class="divider"></div>
				</c:forEach>
			</c:if>
			<p>
				<label>申请人：</label>
				<input type="text" name="interviewer" value="${requirement.applicant }" readonly="readonly" />
			</p>
			<div class="divider"></div>
	
		</div>
	
		<div class="formBar">
			<ul>
				<!-- 
					<li><div class="buttonActive"><div class="buttonContent"><button type="submit">提交</button></div></div></li>
				 -->
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

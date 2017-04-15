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
			<c:if test="${!empty parseResume }">
				<p>
					<label>姓 名：</label> <input name="sn" type="text" size="30"
						value="${parseResume.name }" readonly="readonly" />
				</p>
				<p>
					<label>性 别：</label> <input name="name" readonly="readonly"
						class="required" type="text" size="30"
						value="${parseResume.gender }" />
				</p>
				<p>
					<label>出生年月：</label> <input readonly="readonly" type="text"
						size="30" value="${parseResume.birthday }" />
				</p>
				<p>
					<label>年 龄：</label> <input readonly="readonly" type="text"
						size="30" value="${parseResume.age }" />
				</p>
				<p>
					<label>民 族：</label> <input readonly="readonly" type="text"
						size="30" value="${parseResume.nation }" />
				</p>
				<p>
					<label>手 机 号：</label> <input readonly="readonly" class="required"
						type="text" size="30" value="${parseResume.contact_mobile }" />
				</p>
				<p>
					<label>邮 箱：</label> <input readonly="readonly" class="required"
						type="text" size="30" value="${parseResume.contact_email }" />
				</p>
				<p>
					<label>身 份 证：</label> <input readonly="readonly" type="text"
						size="30" value="${parseResume.id_number }" />
				</p>
				<p>
					<label>工作年限：</label> <input readonly="readonly" type="text"
						size="30" value="${parseResume.work_experience }年" />
				</p>
				<p>
					<label>目前所在地：</label> <input readonly="readonly" type="text"
						size="30" value="${parseResume.location_city }" />
				</p>
				<p>
					<label>应聘工作地点：</label> <input readonly="readonly" type="text"
						size="30" value="${parseResume.location_city }" />
				</p>
				<div class="divider"></div>
				<dl>
					<dt>
						<h2>教育背景：</h2>
					</dt>
					<dd></dd>
				</dl>
				<c:forEach items="${educations}" var="educations">

					<dl class="nowrap">
						<dt></dt>
						<dd>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${educations.start_time }-${educations.end_time }
							&nbsp;&nbsp; ${educations.school } &nbsp;&nbsp;
							${educations.major }</dd>
					</dl>

					<dl class="nowrap">
						<dt></dt>
						<dd>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${educations.degree }
							<br />
					</dl>
				</c:forEach>

				<div class="divider"></div>
				<dl>
					<dt>
						<h2>工作经历：</h2>
					</dt>
					<dd></dd>
				</dl>
				<c:forEach items="${occupations}" var="occupations">
					<dl class="nowrap">
						<dt>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${occupations.start_time }-${occupations.end_time }</dt>
						<dd></dd>
					</dl>
					<dl class="nowrap">
						<dt></dt>
						<dd>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${occupations.company }
							&nbsp;&nbsp;|&nbsp;&nbsp;${occupations.industry }&nbsp;&nbsp;|&nbsp;&nbsp;${occupations.title }</dd>
					</dl>
					<dl class="nowrap">
						<dt></dt>
						<dd>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${occupations.desc }</dd>
					</dl>
				</c:forEach>

				<div class="divider"></div>
				<dl>
					<dt>
						<h2>项目经历：</h2>
					</dt>
					<dd></dd>
				</dl>
				<c:forEach items="${parseProjects}" var="parseProjects">
					<dl class="nowrap">
						<dt>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${parseProjects.start_year }/${parseProjects.start_month }-${parseProjects.end_year }/${parseProjects.end_month }</dt>
						<dd></dd>
					</dl>
					<dl class="nowrap">
						<dt></dt>
						<dd>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${parseProjects.name }
							&nbsp;&nbsp;|&nbsp;&nbsp;${parseProjects.post }</dd>
					</dl>
					<dl class="nowrap">
						<dt></dt>
						<dd>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${parseProjects.desc }</dd>
					</dl>
				</c:forEach>

				<div class="divider"></div>
				<dl class="nowrap">
					<dt>
						<h2>自我评价：</h2>
					</dt>
					<dd></dd>
				</dl>
				<dl class="nowrap">
					<dt></dt>
					<dd>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${parseResume.self_evaluate }</dd>
				</dl>
			</c:if>

			<div class="divider"></div>
			<div class="divider"></div>

			<form onsubmit="return navTabSearch(this);"
				action="resumeController.do?view" method="post">
				<dl class="nowrap">
					<dt ></dt>
					<dd style="text-decoration:none;font-weight:bold;color:#FF0000;">面试记录</dd>
				</dl>
				<dl>
					<dt>
						<input type="hidden" id="rid" name="rid" value="${rid }" />
					</dt>
					<dd>&nbsp;&nbsp;候选人：${parseResume.name }</dd>
				</dl>
				<div class="divider"></div>
				<div class="divider"></div>
				<c:forEach items="${evaluation}" var="evaluation">
					<dl class="nowrap">
						<dt></dt>
						<dd>&nbsp;&nbsp;${evaluation.EVALUATER }：${evaluation.CONTENT }&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a href="resumeController.do?evaluationConten&eid=${evaluation.id }" target="dialog" rel="my_candidates_t" mask="true" title="${parseResume.name }" style="text-decoration:none;font-weight:bold;color:#FF0000;" >修改评价</a></dd>
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

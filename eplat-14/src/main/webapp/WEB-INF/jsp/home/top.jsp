<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>易立德招聘系统</title>

<link href="dwz_jui/themes/default/style.css" rel="stylesheet"
	type="text/css" media="screen" />
<link href="dwz_jui/themes/css/core.css" rel="stylesheet"
	type="text/css" media="screen" />
<link href="dwz_jui/themes/css/print.css" rel="stylesheet"
	type="text/css" media="print" />
<link href="dwz_jui/uploadify/css/uploadify.css" rel="stylesheet"
	type="text/css" media="screen" />


<!--[if IE]>
<link href="themes/css/ieHack.css" rel="stylesheet" type="text/css" media="screen"/>
<![endif]-->

<!--[if lt IE 9]><script src="js/speedup.js" type="text/javascript"></script><script src="js/jquery-1.11.3.min.js" type="text/javascript"></script><![endif]-->
<!--[if gte IE 9]><!-->
<script src="dwz_jui/js/jquery-2.1.4.min.js" type="text/javascript"></script>
<!--<![endif]-->

<script src="dwz_jui/js/jquery.cookie.js" type="text/javascript"></script>
<script src="dwz_jui/js/jquery.validate.js" type="text/javascript"></script>
<script src="dwz_jui/js/jquery.bgiframe.js" type="text/javascript"></script>
<script src="dwz_jui/xheditor/xheditor-1.2.2.min.js"
	type="text/javascript"></script>
<script src="dwz_jui/xheditor/xheditor_lang/zh-cn.js"
	type="text/javascript"></script>
<script src="dwz_jui/uploadify/scripts/jquery.uploadify.js"
	type="text/javascript"></script>

<!-- svg图表  supports Firefox 3.0+, Safari 3.0+, Chrome 5.0+, Opera 9.5+ and Internet Explorer 6.0+ -->
<script type="text/javascript" src="dwz_jui/chart/raphael.js"></script>
<script type="text/javascript" src="dwz_jui/chart/g.raphael.js"></script>
<script type="text/javascript" src="dwz_jui/chart/g.bar.js"></script>
<script type="text/javascript" src="dwz_jui/chart/g.line.js"></script>
<script type="text/javascript" src="dwz_jui/chart/g.pie.js"></script>
<script type="text/javascript" src="dwz_jui/chart/g.dot.js"></script>

<script type="text/javascript"
	src="http://api.map.baidu.com/api?v=2.0&ak=6PYkS1eDz5pMnyfO0jvBNE0F"></script>
<script type="text/javascript"
	src="http://api.map.baidu.com/library/Heatmap/2.0/src/Heatmap_min.js"></script>

<script src="dwz_jui/js/dwz.core.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.util.date.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.validate.method.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.barDrag.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.drag.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.tree.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.accordion.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.ui.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.theme.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.switchEnv.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.alertMsg.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.contextmenu.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.navTab.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.tab.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.resize.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.dialog.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.dialogDrag.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.sortDrag.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.cssTable.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.stable.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.taskBar.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.ajax.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.pagination.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.database.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.datepicker.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.effects.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.panel.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.checkbox.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.history.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.combox.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.file.js" type="text/javascript"></script>
<script src="dwz_jui/js/dwz.print.js" type="text/javascript"></script>

<!-- 可以用dwz.min.js替换前面全部dwz.*.js (注意：替换时下面dwz.regional.zh.js还需要引入)
<script src="bin/dwz.min.js" type="text/javascript"></script>
-->
<script src="dwz_jui/js/dwz.regional.zh.js" type="text/javascript"></script>

<script type="text/javascript">
	$(function() {
		DWZ.init("dwz_jui/dwz.frag.xml", {
			loginUrl : "dwz_jui/login_dialog.html",
			loginTitle : "登录", // 弹出登录对话框
			//		loginUrl:"login.html",	// 跳到登录页面
			statusCode : {
				ok : 200,
				error : 300,
				timeout : 301
			}, //【可选】
			pageInfo : {
				pageNum : "pageNum",
				numPerPage : "numPerPage",
				orderField : "orderField",
				orderDirection : "orderDirection"
			}, //【可选】
			keys : {
				statusCode : "statusCode",
				message : "message"
			}, //【可选】
			ui : {
				hideMode : 'offsets'
			}, //【可选】hideMode:navTab组件切换的隐藏方式，支持的值有’display’，’offsets’负数偏移位置的值，默认值为’display’
			debug : false, // 调试模式 【true|false】
			callback : function() {
				initEnv();
				$("#themeList").theme({
					themeBase : "themes"
				}); // themeBase 相对于index页面的主题base路径
			}
		});
	});
</script>

</head>

<body>
	<div id="layout">
		<div id="header">
			<div class="headerNav">
				<a class="logo">标志</a>
				<ul class="nav">
					<li><a target="_blank">${sessionScope.user.userName }</a></li>
					<li><a href="loginController.do?logout">退出</a></li>
				</ul>

			</div>

			<!-- navMenu -->

		</div>

		<div id="leftside">
			<div id="sidebar_s">
				<div class="collapse">
					<div class="toggleCollapse">
						<div></div>
					</div>
				</div>
			</div>
			<div id="sidebar">
				<div class="toggleCollapse">
					<h2>主菜单</h2>
					<div>收缩</div>
				</div>

				<div class="accordion" fillSpace="sidebar">
					<c:if test="${!empty top_menu }">
						<c:forEach items="${top_menu}" var="top_menui">
							<div class="accordionHeader">
								<h2>
									<span>Folder</span>${top_menui.menuName }</h2>
							</div>

							<div class="accordionContent">
								<ul class="tree treeFolder">
									<c:if test="${!empty all_sub_menu }">
										<c:forEach items="${all_sub_menu}" var="all_sub_menui">
											<c:if test="${all_sub_menui.menuId == top_menui.menuId }">
												<li><a href="${all_sub_menui.subMenuUrl }"
													target="navTab" rel="${all_sub_menui.subMenuRel }"
													navTabId="${all_sub_menui.subMenuRel }" fresh="false">${all_sub_menui.subMenuName }</a></li>
											</c:if>
										</c:forEach>
									</c:if>
								</ul>
							</div>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</div>
		<div id="container">
			<div id="navTab" class="tabsPage">
				<div class="tabsPageHeader">
					<div class="tabsPageHeaderContent">
						<!-- 显示左右控制时添加 class="tabsPageHeaderMargin" -->
						<ul class="navTab-tab">
							<li tabid="main" class="main"><a href="javascript:;"><span><span
										class="home_icon">我的主页</span></span></a></li>
						</ul>
					</div>
					<div class="tabsLeft">left</div>
					<!-- 禁用只需要添加一个样式 class="tabsLeft tabsLeftDisabled" -->
					<div class="tabsRight">right</div>
					<!-- 禁用只需要添加一个样式 class="tabsRight tabsRightDisabled" -->
					<div class="tabsMore">more</div>
				</div>
				<ul class="tabsMoreList">
					<li><a href="javascript:;">我的主页</a></li>
				</ul>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div class="page unitBox">
						<div class="accountInfo">

							<p>
								<h4 style="color:red">欢迎${sessionScope.user.userName }！</h4>
							</p>
						</div>
						<div class="pageFormContent" layoutH="80"
							style="margin-right:230px"></div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="footer"></div>
</body>
</html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN""http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>E-platform主页</title>

<script src="dwz/js/jquery-2.1.4.min.js" type="text/javascript"></script>
<script src="dwz/js/jquery.cookie.js" type="text/javascript"></script>
<script src="dwz/js/jquery.validate.js" type="text/javascript"></script>
<script src="dwz/js/jquery.bgiframe.js" type="text/javascript"></script>
<script src="dwz/xheditor/xheditor-1.2.2.min.js" type="text/javascript"></script>
<script src="dwz/xheditor/xheditor_lang/zh-cn.js" type="text/javascript"></script>
<script src="dwz/uploadify/scripts/jquery.uploadify.js"  type="text/javascript"></script>

<!-- svg图表  supports Firefox 3.0+, Safari 3.0+, Chrome 5.0+, Opera 9.5+ and Internet Explorer 6.0+ -->
<script type="text/javascript" src="dwz/chart/raphael.js"></script>
<script type="text/javascript" src="dwz/chart/g.raphael.js"></script>
<script type="text/javascript" src="dwz/chart/g.bar.js"></script>
<script type="text/javascript" src="dwz/chart/g.line.js"></script>
<script type="text/javascript" src="dwz/chart/g.pie.js"></script>
<script type="text/javascript" src="dwz/chart/g.dot.js"></script>

<link href="dwz/themes/default/style.css" rel="stylesheet"  type="text/css" media="screen" />
<link href="dwz/themes/css/core.css" rel="stylesheet" type="text/css" media="screen" />
<link href="dwz/themes/css/print.css" rel="stylesheet" type="text/css" media="print" />
<link href="dwz/uploadify/css/uploadify.css" rel="stylesheet" type="text/css" media="screen" />
<script src="dwz/bin/dwz.min.js" type="text/javascript"></script>
<script src="dwz/js/dwz.regional.zh.js" type="text/javascript"></script>

<script type="text/javascript">
	$(function() {
		DWZ.init("dwz/dwz.frag.xml", {
			loginUrl : "login_dialog.html",
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

				<div class="accordion"  fillSpace="sidebar">
					<div class="accordionContent">
						<ul class="tree treeFolder">
							<li><a href="pushLogController.do?getAllPushLogs" target="navTab" rel="pushlogs">推送日志</a></li>

							<li><a href="epAttenController.do?gotoExportAttendDatas" target="navTab" rel="exportAttend">导出考勤数据</a>
							</li>

							<li><a href="upLoadFileController.do?gotoImportRestCalendar"  target="navTab" rel="importCalendar">导入休息日历</a>
							</li>
							
							<li><a href="upLoadFileController.do?gotoUpLoadApk" target="navTab" rel="uploadApk">上传新版本</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>

		<div id="container">
			<div id="navTab" class="tabsPage">
				<div class="tabsPageHeader">
					<div class="tabsPageHeaderContent">
						<!-- 显示左右控制时添加 class="tabsPageHeaderMargin" -->
						<ul class="navTab-tab">
							<li tabid="main" class="main"><a href="javascript:;"> <span>
										<span class="home_icon">主页 </span>
								</span>
							</a></li>
						</ul>
					</div>
					<div class="tabsLeft">left</div>
					<!-- 禁用只需要添加一个样式 class="tabsLeft tabsLeftDisabled" -->
					<div class="tabsRight">right</div>
					<!-- 禁用只需要添加一个样式 class="tabsRight tabsRightDisabled" -->
					<div class="tabsMore">more</div>
				</div>
				<ul class="tabsMoreList">
					<li><a href="javascript:;">主页</a></li>
				</ul>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div class="page unitBox">
						<h3>E-platform简略版主页</h3>
					</div>
				</div>
			</div>
		</div>

	</div>

	<div id="footer">Copyright &copy; 2016 Elead团队 深ICP备15053290号-2</div>
</body>
</html>
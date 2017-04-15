<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>首页</title>
<link href="resources/css/jquery-ui-themes.css" type="text/css" rel="stylesheet"/>
    <link href="resources/css/axure_rp_page.css" type="text/css" rel="stylesheet"/>
    <link href="data/styles.css" type="text/css" rel="stylesheet"/>
    <link href="files/dl/styles.css" type="text/css" rel="stylesheet"/>
    <script src="resources/scripts/jquery-1.7.1.min.js"></script>
    <script src="resources/scripts/jquery-ui-1.8.10.custom.min.js"></script>
    <script src="resources/scripts/axure/axQuery.js"></script>
    <script src="resources/scripts/axure/globals.js"></script>
    <script src="resources/scripts/axutils.js"></script>
    <script src="resources/scripts/axure/annotation.js"></script>
    <script src="resources/scripts/axure/axQuery.std.js"></script>
    <script src="resources/scripts/axure/doc.js"></script>
    <script src="data/document.js"></script>
    <script src="resources/scripts/messagecenter.js"></script>
    <script src="resources/scripts/axure/events.js"></script>
    <script src="resources/scripts/axure/action.js"></script>
    <script src="resources/scripts/axure/expr.js"></script>
    <script src="resources/scripts/axure/geometry.js"></script>
    <script src="resources/scripts/axure/flyout.js"></script>
    <script src="resources/scripts/axure/ie.js"></script>
    <script src="resources/scripts/axure/model.js"></script>
    <script src="resources/scripts/axure/repeater.js"></script>
    <script src="resources/scripts/axure/sto.js"></script>
    <script src="resources/scripts/axure/utils.temp.js"></script>
    <script src="resources/scripts/axure/variables.js"></script>
    <script src="resources/scripts/axure/drag.js"></script>
    <script src="resources/scripts/axure/move.js"></script>
    <script src="resources/scripts/axure/visibility.js"></script>
    <script src="resources/scripts/axure/style.js"></script>
    <script src="resources/scripts/axure/adaptive.js"></script>
    <script src="resources/scripts/axure/tree.js"></script>
    <script src="resources/scripts/axure/init.temp.js"></script>
    <script src="files/dl/data.js"></script>
    <script src="resources/scripts/axure/legacy.js"></script>
    <script src="resources/scripts/axure/viewer.js"></script>
    <script type="text/javascript">
      $axure.utils.getTransparentGifPath = function() { return 'resources/images/transparent.gif'; };
      $axure.utils.getOtherPath = function() { return 'resources/Other.html'; };
      $axure.utils.getReloadPath = function() { return 'resources/reload.html'; };
    </script>
  </head>
  <body>
    <div id="base" class="">

      <!-- Unnamed (图片) -->
      <div id="u0" class="ax_图片">
        <img id="u0_img" class="img " src="images/dl/u0.jpg"/>
        <!-- Unnamed () -->
        <div id="u1" class="text">
          <p><span></span></p>
        </div>
      </div>

      <!-- Unnamed (图片) -->
      <div id="u2" class="ax_图片">
        <img id="u2_img" class="img " src="images/dl/u0.jpg"/>
        <!-- Unnamed () -->
        <div id="u3" class="text">
          <p><span></span></p>
        </div>
      </div>

      <!-- 04 (图片) -->
      <div id="u4" class="ax_图片" data-label="04">
        <img id="u4_img" class="img " src="images/dl/04_u4.png"/>
        <!-- Unnamed () -->
        <div id="u5" class="text">
          <p><span></span></p>
        </div>
      </div>

      <!-- Unnamed (动态面板) -->
      <div id="u6" class="ax_动态面板">
        <div id="u6_state0" class="panel_state" data-label="State1">
          <div id="u6_state0_content" class="panel_state_content">

            <!-- Unnamed (图片) -->
            <div id="u7" class="ax_图片">
              <img id="u7_img" class="img " src="images/dl/u7.png"/>
              <!-- Unnamed () -->
              <div id="u8" class="text">
                <p><span></span></p>
              </div>
            </div>
          </div>
        </div>
      </div>

	<form  action="<c:url value="loginController.do?LoginCheck"/>" method="post">
      <!-- Unnamed (形状) -->
      <div id="u9" class="ax_形状">
        <img id="u9_img" class="img " src="images/dl/u9.png"/>
        <!-- Unnamed () -->
        <div id="u10" class="text">
          <!-- 隐藏表单域，用以标记是否是重复提交的登录操作，防止重复提交登录请求。 -->
          <input type="hidden" name="token" value="${token }" />
          <p><span></span></p>
        </div>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u11" class="ax_形状">
      
        <input type="password" name="mima"/>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u13" class="ax_文本段落">
        <img id="u13_img" class="img " src="resources/images/transparent.gif"/>
        <!-- Unnamed () -->
        <div id="u14" class="text">
          <p><span>登录密码</span></p>
        </div>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u15" class="ax_文本段落">
        <img id="u15_img" class="img " src="resources/images/transparent.gif"/>
        <!-- Unnamed () -->
        <div id="u16" class="text">
          <p><span>登录账号</span></p>
        </div>
      </div>
      
      
      <!-- Unnamed (形状) -->
      <div id="u17" class="ax_形状">
      <c:if test="${!empty error}">
        <font color="red"><c:out value="${error}" /></font>
	</c:if>
        <input type="text" name="userName"/>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u19" class="ax_形状">
        <img id="u19_img" class="img " src="images/dl/u19.png"/>
        <!-- Unnamed () -->
        <div id="u20" class="text">
          <input type="submit" value="登录" />
        </div>
      </div>
</form>
      <!-- Unnamed (形状) -->
      <div id="u21" class="ax_形状">
        <img id="u21_img" class="img " src="images/dl/u19.png"/>
        <!-- Unnamed () -->
        <div id="u22" class="text">
          <p><span>忘记密码</span></p>
        </div>
      </div>

      <!-- Unnamed (复选框) -->
      <div id="u23" class="ax_复选框">
        <label for="u23_input">
          <!-- Unnamed () -->
          <div id="u24" class="text">
            <p><span>记住用户名</span></p>
          </div>
        </label>
        <input id="u23_input" type="checkbox" value="checkbox"/>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u25" class="ax_h2">
        <img id="u25_img" class="img " src="resources/images/transparent.gif"/>
        <!-- Unnamed () -->
        
        <div id="u26" class="text">
          <p><span>登录招聘中心</span></p>
        </div>
      </div>

      <!-- Unnamed (水平线) -->
      <div id="u27" class="ax_水平线">
        <img id="u27_start" class="img " src="resources/images/transparent.gif" alt="u27_start"/>
        <img id="u27_end" class="img " src="resources/images/transparent.gif" alt="u27_end"/>
        <img id="u27_line" class="img " src="images/dl/u27_line.png" alt="u27_line"/>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u28" class="ax_h2">
        <img id="u28_img" class="img " src="resources/images/transparent.gif"/>
        <!-- Unnamed () -->
        <div id="u29" class="text">
          <p><span>上海易立德登录招聘中心</span></p>
        </div>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u30" class="ax_文本段落">
        <img id="u30_img" class="img " src="resources/images/transparent.gif"/>
        <!-- Unnamed () -->
        <div id="u31" class="text">
          <p><span>当前时间: ${time }</span></p>
        </div>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u32" class="ax_形状">
        <img id="u32_img" class="img " src="images/dl/u32.png"/>
        <!-- Unnamed () -->
        <div id="u33" class="text">
          <p><span></span></p>
        </div>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u34" class="ax_文本段落">
        <img id="u34_img" class="img " src="resources/images/transparent.gif"/>
        <!-- Unnamed () -->
        <div id="u35" class="text">
          <p><span>总部地址：</span></p><p><span>地址：上海市长宁区新华路728号华联发展大厦908-910室</span></p><p><span>电话：(86) 021-33625698</span></p><p><span>传真：(86) 021- 33625633</span></p><p><span>邮箱：lvfm@e-lead.cn</span></p><p><span>&nbsp;</span></p>
        </div>
      </div>

      <!-- Unnamed (形状) -->
      <div id="u36" class="ax_文本段落">
        <img id="u36_img" class="img " src="resources/images/transparent.gif"/>
        <!-- Unnamed () -->
        <div id="u37" class="text">
          <p><span>上海易立德企业管理咨询有限公司（简称易立德咨询）</span></p>
        </div>
      </div>
    </div>
  </body>
</html>

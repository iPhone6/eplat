package com.cn.eplat.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.util.TextUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.eplat.service.IPushLogService;
import com.cn.eplat.utils.DateUtil;

@Controller
@RequestMapping("/pushLogController")
public class PushLogController {
	private final String DEFAULT_START_DATE = "2016-12-10";
	
	@Resource
	private IPushLogService pushLogService;

	@RequestMapping(params = "getAllPushLogs")
	public String getAllPushLogsBetweenStartAndEnd(HttpServletRequest request) {
		String start = request.getParameter("startdate");
		System.out.println("startdate     "+start);
		String end = request.getParameter("enddate");
		System.out.println("enddate     "+end);
		Date startDate = null;
		Date endDate = null;
		if(TextUtils.isEmpty(start)){
			startDate = DateUtil.parse2date(1, DEFAULT_START_DATE);
		}else{
			startDate = DateUtil.parse2date(1, start);
		}
		
		if(TextUtils.isEmpty(end)){
			endDate = new Date();
		}else{
			endDate = DateUtil.parse2date(1, end);
		}
		
		
		List<HashMap<String, Object>> logs = pushLogService.getAllPushLogsBetweenStartAndEnd(startDate, endDate);
		request.setAttribute("datas",logs);
//		JSONObject object = new JSONObject();
//		if(logs == null || logs.isEmpty()){
//			object.put("ret_code", 0);
//			object.put("ret_message", "暂无数据！");
//			return JSONObject.toJSONString(object,SerializerFeature.WriteMapNullValue);
//		}
//		
//		JSONArray array = new JSONArray();
//		for (HashMap<String, Object> map : logs) {
//			JSONObject obj = new JSONObject();
//			obj.put("work_no", map.get("work_no"));
//			obj.put("name", map.get("name"));
//			obj.put("result", map.get("result"));
//			obj.put("time", DateUtil.formatDate(2, (Date)map.get("time")));
//			array.add(obj);
//		}
//		
//		object.put("datas", array);
//		object.put("ret_code", 1);
//		object.put("ret_message", "获取数据成功！");
//		
//		return JSONObject.toJSONString(object,SerializerFeature.WriteMapNullValue);
		return "pushlog/push_log";
	}
	
	@RequestMapping(params = "gotoPushLogIndex")
	public String gotoPushLogIndex(HttpServletRequest request) {
		return "pushlog/push_main";
	}
	
}

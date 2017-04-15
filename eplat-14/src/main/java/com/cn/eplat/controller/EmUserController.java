package com.cn.eplat.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cn.eplat.service.IEmUserService;
import com.easemob.server.api.IMUserAPI;
import com.easemob.server.comm.wrapper.ResponseWrapper;
//import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/emUserController")
public class EmUserController {
	
	private static Logger logger = Logger.getLogger(EmUserController.class);
	
	private static IMUserAPI em_user_api = EpUserController.getEm_user_api();
	
	@Resource
	private IEmUserService emUserService;
	
	
	
	/** 【OK】
	 * 根据环信用户名获取用户信息 [单个] <br>
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUserInfoByEmUsername", produces = "application/json; charset=utf-8")
	@ResponseBody
	// 注：@RequestBody(required = false) 中，required = false表示请求体中的参数不是必要的，也即是请求参数体可以为空，或者说可以接受空参数体的请求。当接收到了空参数体的请求后，对应的参数值就是null。
	public String getUserInfoByEmUsernameJson(HttpServletRequest request, @RequestBody(required = false) Map<String,Object> map) {
		JSONObject jsonObj = new JSONObject();
		
		if(map != null) {
			System.out.println("hhhhhh" + map);
		}
		
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	/** 【OK】
	 * 根据环信用户名获取用户信息 [批量] <br>
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUserInfoByEmUsernames", produces = "application/json; charset=utf-8")
	@ResponseBody
	// 注：@RequestBody(required = false) 中，required = false表示请求体中的参数不是必要的，也即是请求参数体可以为空，或者说可以接受空参数体的请求。当接收到了空参数体的请求后，对应的参数值就是null。
	public String getUserInfoByEmUsernamesJson(HttpServletRequest request, @RequestBody(required = false) List<Map<String,Object>> params_list) {
		JSONObject jsonObj = new JSONObject();
		
		if(params_list != null) {
			for(Map<String, Object> mm:params_list) {
				System.out.println("hhhhhh : " + mm);
			}
		}
		
		
		
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	/** 【OK】
	 * 根据环信用户名获取用户信息 [单个 - JSONObject] <br>
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUserInfoByEmUsernameJo", produces = "application/json; charset=utf-8")
	@ResponseBody
	// 注：@RequestBody(required = false) 中，required = false表示请求体中的参数不是必要的，也即是请求参数体可以为空，或者说可以接受空参数体的请求。当接收到了空参数体的请求后，对应的参数值就是null。
	public String getUserInfoByEmUsernameJsonObj(HttpServletRequest request, @RequestBody(required = false) JSONObject jo) {
		JSONObject jsonObj = new JSONObject();
		
		if(jo != null) {
			System.out.println("jo ==> " + jo);
			System.out.println("jo2 : " + JSONObject.toJSONString(jo, SerializerFeature.WriteMapNullValue));
		}
		
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	/** 【OK】
	 * 根据环信用户名获取用户信息 [批量 - JSONArray] <br>
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUserInfoByEmUsernamesJa", produces = "application/json; charset=utf-8")
	@ResponseBody
	// 注：@RequestBody(required = false) 中，required = false表示请求体中的参数不是必要的，也即是请求参数体可以为空，或者说可以接受空参数体的请求。当接收到了空参数体的请求后，对应的参数值就是null。
	public String getUserInfoByEmUsernamesJsonArr(HttpServletRequest request, @RequestBody(required = false) JSONArray ja) {
		JSONObject jsonObj = new JSONObject();
		
//		if(ja != null) {
//			System.out.println("ja1 : " + ja);
//			System.out.println("ja2 : " + JSONObject.toJSONString(ja, SerializerFeature.WriteMapNullValue));
//		}
		
//		String em_usernames_str = request.getParameter("em_usernames");
		
		/*
		if(em_usernames_str != null) {
			if(StringUtils.isBlank(em_usernames_str)) {
				jsonObj.put("ret_code", -4);
				jsonObj.put("ret_message", "环信用户名JSON数组字符串参数为空");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			String em_usernames_trim = em_usernames_str.trim();
			JSONArray parse_ja = JSONArray.parseArray(em_usernames_trim);
			
		}
		*/
		
		JSONArray ja_params = null;	// 用来准备存放最终要进行处理的JSON数组参数
		/*
		if(StringUtils.isBlank(em_usernames_str)) {
			if(ja == null) {
				jsonObj.put("ret_code", -1);
				jsonObj.put("ret_message", "环信用户名JSON数组参数为空");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			} else {
				ja_params = ja;
			}
		} else {
			if(ja == null) {
				String em_usernames_trim = em_usernames_str.trim();
				JSONArray parse_ja = null;
				try {
					parse_ja = JSONArray.parseArray(em_usernames_trim);
				} catch (Exception e) {
//					e.printStackTrace();
					logger.error("转换环信用户名的JSON数组字符串时出现异常");
					jsonObj.put("ret_code", -4);
					jsonObj.put("ret_message", "环信用户名的字符串参数不为空，但转换环信用户名的JSON数组字符串时出现了异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				ja_params = parse_ja;
			} else {
				ja_params = ja;	// 如果同时提供了请求体中的ja和请求参数中的em_usernames，则默认选取请求体中的ja
			}
		}
		*/
		
		if(ja == null) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "环信用户名JSON数组参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		ja_params = ja;
		
//		Iterator<Object> ja_iter = ja.iterator();
//		while(ja_iter.hasNext()) {
//			ja_iter.next();
//		}
		
		/*
		jsonObj.put("count", ja_params.size());	// 传入的JSON数组中参数的个数
		
		ArrayList<Object> all_em_unames = new ArrayList<Object>();	// 用来存放所有传入的环信用户名参数的数组，以保证按照传入参数的顺序返回结果
		ArrayList<String> valid_em_unames = new ArrayList<String>();	// 用来存放有效的（字符串类型的）环信用户名参数的数组
		ArrayList<Object> invalid_em_unames = new ArrayList<Object>();	// 用来存放无效的（非字符串类型的）环信用户名参数的数组
		
		for(Object obj_i:ja_params) {
			all_em_unames.add(obj_i);
			if(obj_i instanceof String) {
//				System.out.println(obj_i);
				valid_em_unames.add((String) obj_i);
			} else {
				if(obj_i != null) {
					System.out.println(obj_i.getClass());
				} else {
					System.out.println("null obj_i");
				}
				invalid_em_unames.add(obj_i);
			}
		}
		
		if(valid_em_unames.size() == 0) {
			JSONArray ja_datas = new JSONArray();
			
			if(invalid_em_unames.size() == 0) {
//				jsonObj.put("datas", ja_datas);
			} else {
			}
			
			
			for(Object obj_a : all_em_unames) {
				if(invalid_em_unames.contains(obj_a)) {
					JSONObject jo_data = new JSONObject();
					jo_data.put("ep_name", null);
					jo_data.put("work_no", null);
					jo_data.put("em_username", null);
					jo_data.put("em_nickname", null);
					jo_data.put("data_code", -2);
					jo_data.put("data_msg", "原始参数不是字符串");
					jo_data.put("original_param", obj_a);
					ja_datas.add(jo_data);
				}
			}
			
			for(Object obj_tmp : invalid_em_unames) {
				JSONObject jo_data = new JSONObject();
				jo_data.put("ep_name", null);
				jo_data.put("work_no", null);
				jo_data.put("em_username", null);
				jo_data.put("em_nickname", null);
				jo_data.put("data_code", -2);
				jo_data.put("data_msg", "原始参数不是字符串");
				jo_data.put("original_param", obj_tmp);
				ja_datas.add(jo_data);
			}
			
			jsonObj.put("datas", ja_datas);
			
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "环信用户名JSON数组参数中没有字符串");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		List<Map<String, Object>> valid_results = emUserService.batchGetUserInfoByEmUsernames(valid_em_unames);
		
		if(valid_results == null) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "根据环信用户名批量获取用户信息出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(valid_results.size() == 0) {
			JSONArray ja_datas = new JSONArray();
			
			for(String str_tmp:valid_em_unames) {
				JSONObject jo_data1 = new JSONObject();
				jo_data1.put("ep_name", null);
				jo_data1.put("work_no", null);
				jo_data1.put("em_username", null);
				jo_data1.put("em_nickname", null);
				jo_data1.put("data_code", -1);
				jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
				jo_data1.put("original_param", str_tmp);
				ja_datas.add(jo_data1);
			}
			
			for(Object obj_tmp : invalid_em_unames) {
				JSONObject jo_data2 = new JSONObject();
				jo_data2.put("ep_name", null);
				jo_data2.put("work_no", null);
				jo_data2.put("em_username", null);
				jo_data2.put("em_nickname", null);
				jo_data2.put("data_code", -2);
				jo_data2.put("data_msg", "原始参数不是字符串");
				jo_data2.put("original_param", obj_tmp);
				ja_datas.add(jo_data2);
			}
			
			for(Object obj_a : all_em_unames) {
				if(valid_em_unames.contains(obj_a)) {
					JSONObject jo_data1 = new JSONObject();
					jo_data1.put("ep_name", null);
					jo_data1.put("work_no", null);
					jo_data1.put("em_username", null);
					jo_data1.put("em_nickname", null);
					jo_data1.put("data_code", -1);
					jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
					jo_data1.put("original_param", obj_a);
					ja_datas.add(jo_data1);
				} else if(invalid_em_unames.contains(obj_a)) {
					JSONObject jo_data2 = new JSONObject();
					jo_data2.put("ep_name", null);
					jo_data2.put("work_no", null);
					jo_data2.put("em_username", null);
					jo_data2.put("em_nickname", null);
					jo_data2.put("data_code", -2);
					jo_data2.put("data_msg", "原始参数不是字符串");
					jo_data2.put("original_param", obj_a);
					ja_datas.add(jo_data2);
				}
			}
			
			jsonObj.put("datas", ja_datas);
			
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "根据环信用户名批量获取用户信息结果为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		JSONArray ja_datas = new JSONArray();
		
		ArrayList<Object> valid_em_unames_found = new ArrayList<Object>();
		ArrayList<Object> valid_em_unames_not_found = new ArrayList<Object>();	// 用于存放是字符串类型的参数，但是未找到匹配的用户信息的那部分字符串参数
		ArrayList<Map<String, Object>> vrs_in_order = new ArrayList<Map<String, Object>>();	// 用于存放调整顺序后的有效的查询结果数据
//		for(int i=0; i<valid_results.size(); i++) {
//			vrs_in_order.add(null);
//		}
//		for(String str : valid_em_unames) {
//			valid_em_unames_not_found.add(str);
//		}
//		valid_em_unames_not_found.addAll(valid_em_unames);
		
		for(Map<String, Object> vr : valid_results) {
			
			String em_uname = (String) vr.get("em_uname");
//			Object ep_name = vr.get("ep_name");
//			Object work_no = vr.get("work_no");
//			Object em_nickname = vr.get("em_nickname");
			
			if(valid_em_unames.contains(em_uname)) {
				valid_em_unames_found.add(em_uname);
				vrs_in_order.add(vr);
			} else {
				valid_em_unames_not_found.add(em_uname);
//				jo_data3.put("original_param", em_uname);
//				valid_em_unames_not_found.add(em_uname);
//				valid_em_unames_found.remove(em_uname);
//				vrs_in_order.set(index, element);
			}
			
			JSONObject jo_data3 = new JSONObject();
			jo_data3.put("ep_name", ep_name);
			jo_data3.put("work_no", work_no);
			jo_data3.put("em_username", em_uname);
			jo_data3.put("em_nickname", em_nickname);
			jo_data3.put("data_code", 1);
			jo_data3.put("data_msg", "原始参数是字符串，且已找到匹配的用户信息");
			jo_data3.put("original_param", em_uname);
			ja_datas.add(jo_data3);
			
		}
		
		for(Object obj_t : valid_em_unames_not_found) {
			JSONObject jo_data1 = new JSONObject();
			jo_data1.put("ep_name", null);
			jo_data1.put("work_no", null);
			jo_data1.put("em_username", null);
			jo_data1.put("em_nickname", null);
			jo_data1.put("data_code", -1);
			jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
			jo_data1.put("original_param", obj_t);
			ja_datas.add(jo_data1);
		}
		
		for(Object obj_tmp : invalid_em_unames) {
			JSONObject jo_data4 = new JSONObject();
			jo_data4.put("ep_name", null);
			jo_data4.put("work_no", null);
			jo_data4.put("em_username", null);
			jo_data4.put("em_nickname", null);
			jo_data4.put("data_code", -2);
			jo_data4.put("data_msg", "原始参数不是字符串");
			jo_data4.put("original_param", obj_tmp);
			ja_datas.add(jo_data4);
		}
		
		for(Object obj_a : all_em_unames) {
			if(valid_em_unames_found.contains(obj_a)) {
				Map<String, Object> vr = vrs_in_order.get(valid_em_unames_found.indexOf(obj_a));
				String em_uname = (String) vr.get("em_uname");
				Object ep_name = vr.get("ep_name");
				Object work_no = vr.get("work_no");
				Object em_nickname = vr.get("em_nickname");
				
				JSONObject jo_data3 = new JSONObject();
				jo_data3.put("ep_name", ep_name);
				jo_data3.put("work_no", work_no);
				jo_data3.put("em_username", em_uname);
				jo_data3.put("em_nickname", em_nickname);
				jo_data3.put("data_code", 1);
				jo_data3.put("data_msg", "原始参数是字符串，且已找到匹配的用户信息");
				jo_data3.put("original_param", em_uname);
				ja_datas.add(jo_data3);
			} else if(invalid_em_unames.contains(obj_a)) {
				JSONObject jo_data4 = new JSONObject();
				jo_data4.put("ep_name", null);
				jo_data4.put("work_no", null);
				jo_data4.put("em_username", null);
				jo_data4.put("em_nickname", null);
				jo_data4.put("data_code", -2);
				jo_data4.put("data_msg", "原始参数不是字符串");
				jo_data4.put("original_param", obj_a);
				ja_datas.add(jo_data4);
			} else {
				JSONObject jo_data1 = new JSONObject();
				jo_data1.put("ep_name", null);
				jo_data1.put("work_no", null);
				jo_data1.put("em_username", null);
				jo_data1.put("em_nickname", null);
				jo_data1.put("data_code", -1);
				jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
				jo_data1.put("original_param", obj_a);
				ja_datas.add(jo_data1);
			}
		}
		
		jsonObj.put("datas", ja_datas);
		
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "根据环信用户名批量获取用户信息成功");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		*/
		
		return processEmUsernamesParams(ja_params);
	}
	
	/** 【OK】
	 * 根据环信用户名获取用户信息 [批量 - JSONArray Key-Value] <br>
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUserInfoByEmUsernamesJaKv", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getUserInfoByEmUsernamesJsonArrKv(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		String em_usernames_str = request.getParameter("em_usernames");
		
		JSONArray ja_params = null;	// 用来准备存放最终要进行处理的JSON数组参数
		
		if(StringUtils.isBlank(em_usernames_str)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "环信用户名JSON数组参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		} else {
			String em_usernames_trim = em_usernames_str.trim();
			JSONArray parse_ja = null;
			try {
				parse_ja = JSONArray.parseArray(em_usernames_trim);
			} catch (Exception e) {
//					e.printStackTrace();
				logger.error("转换环信用户名的JSON数组字符串时出现异常");
				jsonObj.put("ret_code", -4);
				jsonObj.put("ret_message", "环信用户名的字符串参数不为空，但转换环信用户名的JSON数组字符串时出现了异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			ja_params = parse_ja;
		}
		
		/*
		jsonObj.put("count", ja_params.size());	// 传入的JSON数组中参数的个数
		
		ArrayList<String> valid_em_unames = new ArrayList<String>();	// 用来存放有效的（字符串类型的）环信用户名参数的数组
		ArrayList<Object> invalid_em_unames = new ArrayList<Object>();	// 用来存放无效的（非字符串类型的）环信用户名参数的数组
		
		for(Object obj_i:ja_params) {
			if(obj_i instanceof String) {
//				System.out.println(obj_i);
				valid_em_unames.add((String) obj_i);
			} else {
				if(obj_i != null) {
					System.out.println(obj_i.getClass());
				} else {
					System.out.println("null obj_i");
				}
				invalid_em_unames.add(obj_i);
			}
		}
		
		if(valid_em_unames.size() == 0) {
			JSONArray ja_datas = new JSONArray();
			
			if(invalid_em_unames.size() == 0) {
//				jsonObj.put("datas", ja_datas);
			} else {
			}
			
			for(Object obj_tmp : invalid_em_unames) {
				JSONObject jo_data = new JSONObject();
				jo_data.put("ep_name", null);
				jo_data.put("work_no", null);
				jo_data.put("em_username", null);
				jo_data.put("em_nickname", null);
				jo_data.put("data_code", -2);
				jo_data.put("data_msg", "原始参数不是字符串");
				jo_data.put("original_param", obj_tmp);
				ja_datas.add(jo_data);
			}
			jsonObj.put("datas", ja_datas);
			
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "环信用户名JSON数组参数中没有字符串");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		List<Map<String, Object>> valid_results = emUserService.batchGetUserInfoByEmUsernames(valid_em_unames);
		
		if(valid_results == null) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "根据环信用户名批量获取用户信息出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(valid_results.size() == 0) {
			JSONArray ja_datas = new JSONArray();
			
			for(String str_tmp:valid_em_unames) {
				JSONObject jo_data1 = new JSONObject();
				jo_data1.put("ep_name", null);
				jo_data1.put("work_no", null);
				jo_data1.put("em_username", null);
				jo_data1.put("em_nickname", null);
				jo_data1.put("data_code", -1);
				jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
				jo_data1.put("original_param", str_tmp);
				ja_datas.add(jo_data1);
			}
			
			for(Object obj_tmp : invalid_em_unames) {
				JSONObject jo_data2 = new JSONObject();
				jo_data2.put("ep_name", null);
				jo_data2.put("work_no", null);
				jo_data2.put("em_username", null);
				jo_data2.put("em_nickname", null);
				jo_data2.put("data_code", -2);
				jo_data2.put("data_msg", "原始参数不是字符串");
				jo_data2.put("original_param", obj_tmp);
				ja_datas.add(jo_data2);
			}
			jsonObj.put("datas", ja_datas);
			
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "根据环信用户名批量获取用户信息结果为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		JSONArray ja_datas = new JSONArray();
		
		ArrayList<Object> valid_em_unames_not_found = new ArrayList<Object>();	// 用于存放是字符串类型的参数，但是未找到匹配的用户信息的那部分字符串参数
//		for(String str : valid_em_unames) {
//			valid_em_unames_not_found.add(str);
//		}
		valid_em_unames_not_found.addAll(valid_em_unames);
		
		for(Map<String, Object> vr : valid_results) {
			String em_uname = (String) vr.get("em_uname");
			Object ep_name = vr.get("ep_name");
			Object work_no = vr.get("work_no");
			Object em_nickname = vr.get("em_nickname");
			
			if(valid_em_unames.contains(em_uname)) {
				valid_em_unames_not_found.remove(em_uname);
			} else {
//				jo_data3.put("original_param", em_uname);
//				valid_em_unames_not_found.add(em_uname);
			}
			
			JSONObject jo_data3 = new JSONObject();
			jo_data3.put("ep_name", ep_name);
			jo_data3.put("work_no", work_no);
			jo_data3.put("em_username", em_uname);
			jo_data3.put("em_nickname", em_nickname);
			jo_data3.put("data_code", 1);
			jo_data3.put("data_msg", "原始参数是字符串，且已找到匹配的用户信息");
			jo_data3.put("original_param", em_uname);
			ja_datas.add(jo_data3);
		}
		
		for(Object obj_t : valid_em_unames_not_found) {
			JSONObject jo_data1 = new JSONObject();
			jo_data1.put("ep_name", null);
			jo_data1.put("work_no", null);
			jo_data1.put("em_username", null);
			jo_data1.put("em_nickname", null);
			jo_data1.put("data_code", -1);
			jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
			jo_data1.put("original_param", obj_t);
			ja_datas.add(jo_data1);
		}
		
		for(Object obj_tmp : invalid_em_unames) {
			JSONObject jo_data4 = new JSONObject();
			jo_data4.put("ep_name", null);
			jo_data4.put("work_no", null);
			jo_data4.put("em_username", null);
			jo_data4.put("em_nickname", null);
			jo_data4.put("data_code", -2);
			jo_data4.put("data_msg", "原始参数不是字符串");
			jo_data4.put("original_param", obj_tmp);
			ja_datas.add(jo_data4);
		}
		jsonObj.put("datas", ja_datas);
		
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "根据环信用户名批量获取用户信息成功");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		*/
		return processEmUsernamesParams(ja_params);
	}
	
	private String processEmUsernamesParams(JSONArray ja_params) {
//		if(ja_params == null) {
//			return;
//		}
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("count", ja_params.size());	// 传入的JSON数组中参数的个数
		
		ArrayList<Object> all_em_unames = new ArrayList<Object>();	// 用来存放所有传入的环信用户名参数的数组，以保证按照传入参数的顺序返回结果
		ArrayList<String> valid_em_unames = new ArrayList<String>();	// 用来存放有效的（字符串类型的）环信用户名参数的数组
		ArrayList<Object> invalid_em_unames = new ArrayList<Object>();	// 用来存放无效的（非字符串类型的）环信用户名参数的数组
		
		for(Object obj_i:ja_params) {
			all_em_unames.add(obj_i);
			if(obj_i instanceof String) {
//				System.out.println(obj_i);
				valid_em_unames.add((String) obj_i);
			} else {
				if(obj_i != null) {
					System.out.println(obj_i.getClass());
				} else {
					System.out.println("null obj_i");
				}
				invalid_em_unames.add(obj_i);
			}
		}
		
		if(valid_em_unames.size() == 0) {
			JSONArray ja_datas = new JSONArray();
			/*
			if(invalid_em_unames.size() == 0) {
//				jsonObj.put("datas", ja_datas);
			} else {
			}
			*/
			
			for(Object obj_a : all_em_unames) {
				if(invalid_em_unames.contains(obj_a)) {
					JSONObject jo_data = new JSONObject();
					jo_data.put("ep_name", null);
					jo_data.put("work_no", null);
					jo_data.put("em_username", null);
					jo_data.put("em_nickname", null);
					jo_data.put("data_code", -2);
					jo_data.put("data_msg", "原始参数不是字符串");
					jo_data.put("original_param", obj_a);
					ja_datas.add(jo_data);
				}
			}
			/*
			for(Object obj_tmp : invalid_em_unames) {
				JSONObject jo_data = new JSONObject();
				jo_data.put("ep_name", null);
				jo_data.put("work_no", null);
				jo_data.put("em_username", null);
				jo_data.put("em_nickname", null);
				jo_data.put("data_code", -2);
				jo_data.put("data_msg", "原始参数不是字符串");
				jo_data.put("original_param", obj_tmp);
				ja_datas.add(jo_data);
			}
			*/
			jsonObj.put("datas", ja_datas);
			
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "环信用户名JSON数组参数中没有字符串");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		List<Map<String, Object>> valid_results = emUserService.batchGetUserInfoByEmUsernames(valid_em_unames);
		
		if(valid_results == null) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "根据环信用户名批量获取用户信息出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(valid_results.size() == 0) {
			JSONArray ja_datas = new JSONArray();
			/*
			for(String str_tmp:valid_em_unames) {
				JSONObject jo_data1 = new JSONObject();
				jo_data1.put("ep_name", null);
				jo_data1.put("work_no", null);
				jo_data1.put("em_username", null);
				jo_data1.put("em_nickname", null);
				jo_data1.put("data_code", -1);
				jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
				jo_data1.put("original_param", str_tmp);
				ja_datas.add(jo_data1);
			}
			
			for(Object obj_tmp : invalid_em_unames) {
				JSONObject jo_data2 = new JSONObject();
				jo_data2.put("ep_name", null);
				jo_data2.put("work_no", null);
				jo_data2.put("em_username", null);
				jo_data2.put("em_nickname", null);
				jo_data2.put("data_code", -2);
				jo_data2.put("data_msg", "原始参数不是字符串");
				jo_data2.put("original_param", obj_tmp);
				ja_datas.add(jo_data2);
			}
			*/
			for(Object obj_a : all_em_unames) {
				if(valid_em_unames.contains(obj_a)) {
					JSONObject jo_data1 = new JSONObject();
					jo_data1.put("ep_name", null);
					jo_data1.put("work_no", null);
					jo_data1.put("em_username", null);
					jo_data1.put("em_nickname", null);
					jo_data1.put("data_code", -1);
					jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
					jo_data1.put("original_param", obj_a);
					ja_datas.add(jo_data1);
				} else if(invalid_em_unames.contains(obj_a)) {
					JSONObject jo_data2 = new JSONObject();
					jo_data2.put("ep_name", null);
					jo_data2.put("work_no", null);
					jo_data2.put("em_username", null);
					jo_data2.put("em_nickname", null);
					jo_data2.put("data_code", -2);
					jo_data2.put("data_msg", "原始参数不是字符串");
					jo_data2.put("original_param", obj_a);
					ja_datas.add(jo_data2);
				}
			}
			
			jsonObj.put("datas", ja_datas);
			
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "根据环信用户名批量获取用户信息结果为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		JSONArray ja_datas = new JSONArray();
		
		ArrayList<Object> valid_em_unames_found = new ArrayList<Object>();
		ArrayList<Object> valid_em_unames_not_found = new ArrayList<Object>();	// 用于存放是字符串类型的参数，但是未找到匹配的用户信息的那部分字符串参数
		ArrayList<Map<String, Object>> vrs_in_order = new ArrayList<Map<String, Object>>();	// 用于存放调整顺序后的有效的查询结果数据
//		for(int i=0; i<valid_results.size(); i++) {
//			vrs_in_order.add(null);
//		}
//		for(String str : valid_em_unames) {
//			valid_em_unames_not_found.add(str);
//		}
//		valid_em_unames_not_found.addAll(valid_em_unames);
		
		for(Map<String, Object> vr : valid_results) {
			
			String em_uname = (String) vr.get("em_uname");
//			Object ep_name = vr.get("ep_name");
//			Object work_no = vr.get("work_no");
//			Object em_nickname = vr.get("em_nickname");
			
			if(valid_em_unames.contains(em_uname)) {
				valid_em_unames_found.add(em_uname);
				vrs_in_order.add(vr);
			} else {
				valid_em_unames_not_found.add(em_uname);
//				jo_data3.put("original_param", em_uname);
//				valid_em_unames_not_found.add(em_uname);
//				valid_em_unames_found.remove(em_uname);
//				vrs_in_order.set(index, element);
			}
			/*
			JSONObject jo_data3 = new JSONObject();
			jo_data3.put("ep_name", ep_name);
			jo_data3.put("work_no", work_no);
			jo_data3.put("em_username", em_uname);
			jo_data3.put("em_nickname", em_nickname);
			jo_data3.put("data_code", 1);
			jo_data3.put("data_msg", "原始参数是字符串，且已找到匹配的用户信息");
			jo_data3.put("original_param", em_uname);
			ja_datas.add(jo_data3);
			*/
		}
		/*
		for(Object obj_t : valid_em_unames_not_found) {
			JSONObject jo_data1 = new JSONObject();
			jo_data1.put("ep_name", null);
			jo_data1.put("work_no", null);
			jo_data1.put("em_username", null);
			jo_data1.put("em_nickname", null);
			jo_data1.put("data_code", -1);
			jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
			jo_data1.put("original_param", obj_t);
			ja_datas.add(jo_data1);
		}
		
		for(Object obj_tmp : invalid_em_unames) {
			JSONObject jo_data4 = new JSONObject();
			jo_data4.put("ep_name", null);
			jo_data4.put("work_no", null);
			jo_data4.put("em_username", null);
			jo_data4.put("em_nickname", null);
			jo_data4.put("data_code", -2);
			jo_data4.put("data_msg", "原始参数不是字符串");
			jo_data4.put("original_param", obj_tmp);
			ja_datas.add(jo_data4);
		}
		*/
		for(Object obj_a : all_em_unames) {
			if(valid_em_unames_found.contains(obj_a)) {
				Map<String, Object> vr = vrs_in_order.get(valid_em_unames_found.indexOf(obj_a));
				String em_uname = (String) vr.get("em_uname");
				Object ep_name = vr.get("ep_name");
				Object work_no = vr.get("work_no");
				Object em_nickname = vr.get("em_nickname");
				
				JSONObject jo_data3 = new JSONObject();
				jo_data3.put("ep_name", ep_name);
				jo_data3.put("work_no", work_no);
				jo_data3.put("em_username", em_uname);
				jo_data3.put("em_nickname", em_nickname);
				jo_data3.put("data_code", 1);
				jo_data3.put("data_msg", "原始参数是字符串，且已找到匹配的用户信息");
				jo_data3.put("original_param", em_uname);
				ja_datas.add(jo_data3);
			} else if(invalid_em_unames.contains(obj_a)) {
				JSONObject jo_data4 = new JSONObject();
				jo_data4.put("ep_name", null);
				jo_data4.put("work_no", null);
				jo_data4.put("em_username", null);
				jo_data4.put("em_nickname", null);
				jo_data4.put("data_code", -2);
				jo_data4.put("data_msg", "原始参数不是字符串");
				jo_data4.put("original_param", obj_a);
				ja_datas.add(jo_data4);
			} else {
				JSONObject jo_data1 = new JSONObject();
				jo_data1.put("ep_name", null);
				jo_data1.put("work_no", null);
				jo_data1.put("em_username", null);
				jo_data1.put("em_nickname", null);
				jo_data1.put("data_code", -1);
				jo_data1.put("data_msg", "原始参数是字符串，但未找到匹配的用户信息");
				jo_data1.put("original_param", obj_a);
				ja_datas.add(jo_data1);
			}
		}
		
		jsonObj.put("datas", ja_datas);
		
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "根据环信用户名批量获取用户信息成功");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
	}
	
	/** 【NOT OK】
	 * 根据环信用户名获取用户信息 [单个、批量 - JSON] <br>
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUserInfoByEmUsernameJ", produces = "application/json; charset=utf-8")
	@ResponseBody
	// 注：@RequestBody(required = false) 中，required = false表示请求体中的参数不是必要的，也即是请求参数体可以为空，或者说可以接受空参数体的请求。当接收到了空参数体的请求后，对应的参数值就是null。
	public String getUserInfoByEmUsernameJsonJ(HttpServletRequest request, @RequestBody(required = false) JSON json) {
		JSONObject jsonObj = new JSONObject();
		
		if(json != null) {
			System.out.println("json1 : " + json);
			System.out.println("json2 : " + JSONObject.toJSONString(json, SerializerFeature.WriteMapNullValue));
		}
		
		
		
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	/** 【OK】
	 * 根据环信用户名获取用户信息 [单个/多个] </br>
	 * 		注：以Object对象接收Json参数，既可以是JSONObject也可以是JSONArray <br>
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUserInfoByEmUsernameObj", produces = "application/json; charset=utf-8")
	@ResponseBody
	// 注：@RequestBody(required = false) 中，required = false表示请求体中的参数不是必要的，也即是请求参数体可以为空，或者说可以接受空参数体的请求。当接收到了空参数体的请求后，对应的参数值就是null。
	public String getUserInfoByEmUsernameObjJson(HttpServletRequest request, @RequestBody(required = false) Object obj) {
		JSONObject jsonObj = new JSONObject();
		
		if(obj != null) {
			System.out.println("hhhhhh  --> " + obj);
		}
		
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	/**
	 * 获取IM用户[单个] <br>
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getIMUsersByUserName", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getIMUsersByUserNameJson(HttpServletRequest request) {

		String em_username_str = request.getParameter("em_username");
		JSONObject jsonObj = new JSONObject();
		
		if(StringUtils.isBlank(em_username_str)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "环信用户名参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		String em_username = em_username_str.trim();
		
		ResponseWrapper resw = (ResponseWrapper) em_user_api.getIMUsersByUserName(em_username);
		
		if (resw.getResponseStatus() != null && resw.getResponseStatus() == 200) {
			logger.info("====获取[单个]IM用户成功====");
			Object resp_body = resw.getResponseBody();
			
		} else {
			logger.error("====获取[单个]IM用户失败====");
			System.out.println("status:" + resw.getResponseStatus());
			System.out.println("messages:" + resw.getMessages());
			System.out.println("body:" + resw.getResponseBody());
		}
		
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	/**
	 * 获取IM用户[批量]，参数为空时默认返回最早创建的10个用户 <br>
	 * 
	 * @param limit
	 *            单页获取数量
	 * @param cursor
	 *            游标，大于单页记录时会产生
	 * --> Object getIMUsersBatch(Long limit, String cursor);
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getIMUsersBatch", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getIMUsersBatchJson(HttpServletRequest request) {
		
		String limit_str = request.getParameter("limit");
		String cursor_str = request.getParameter("cursor");
		JSONObject jsonObj = new JSONObject();
		
//		if(StringUtils.isBlank(em_username_str)) {
//			jsonObj.put("ret_code", -1);
//			jsonObj.put("ret_message", "环信用户名参数为空");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
//		String em_username = em_username_str.trim();
		
		Long limit = null;
		if(StringUtils.isNotBlank(limit_str)) {
			try {
				limit = Long.parseLong(limit_str.trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				logger.error("转换Long类型参数（limit）时出现异常！");
			}
		}
		
		ResponseWrapper resw = (ResponseWrapper) em_user_api.getIMUsersBatch(limit, cursor_str);
		
		if (resw.getResponseStatus() != null && resw.getResponseStatus() == 200) {
			logger.info("====获取[批量]IM用户成功====");
			Object resp_body = resw.getResponseBody();
			
		} else {
			logger.error("====获取[批量]IM用户失败====");
			System.out.println("status:" + resw.getResponseStatus());
			System.out.println("messages:" + resw.getMessages());
			System.out.println("body:" + resw.getResponseBody());
		}
		
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
}

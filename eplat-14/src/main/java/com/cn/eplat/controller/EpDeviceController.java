package com.cn.eplat.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cn.eplat.model.BindHistory;
import com.cn.eplat.model.EpDevice;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.service.IBindHistoryService;
import com.cn.eplat.service.IEpAttenService;
import com.cn.eplat.service.IEpDeviceService;
import com.cn.eplat.service.IEpUserService;
import com.cn.eplat.utils.DateUtil;

@Controller
@RequestMapping("/epDeviceController")
public class EpDeviceController {
	private static Logger logger = Logger.getLogger(EpDeviceController.class);
	
	private static IEpUserService epUserService;
	private static IEpDeviceService epDeviceService;
	@Resource
	private IBindHistoryService bindHistoryService;
	
	public static IEpUserService getEpUserService() {
		return epUserService;
	}
	@Resource
	public void setEpUserService(IEpUserService epUserService) {
		EpDeviceController.epUserService = epUserService;
	}
	public IEpDeviceService getEpDeviceService() {
		return epDeviceService;
	}
	@Resource
	public void setEpDeviceService(IEpDeviceService epDeviceService) {
		EpDeviceController.epDeviceService = epDeviceService;
	}

	/*
	 * 根据设备唯一标识码（IMEI）获取设备id
	 * 只有返回值大于0时才为正常的设备id，其余null、0或负数时为异常情况
	 */
	@Deprecated
	private static Integer getDeviceIdByImei(String imei) {
		if(StringUtils.isBlank(imei)) {	// 如果传入的IMEI码为空白字符串，则直接返回null
			return null;
		}
		
		String imei_trim = imei.trim();
		EpDevice epd_query = new EpDevice();
		epd_query.setImei(imei_trim);
		
		List<EpDevice> ret_epds = epDeviceService.getEpDeviceByCriterion(epd_query);
		if(ret_epds == null) {	// 如果根据IMEI码查询设备信息失败，则直接返回-1
			return -1;
		}
		
		int epds_size = ret_epds.size();
		if(epds_size == 0) {	// 如果根据IMEI码未查询到匹配的设备信息，即查询到的匹配的设备信息条数为0时，直接返回0
			return 0;
		}
		
		if(epds_size == 1) {	// 如果查询到唯一的一个设备信息与给定的IMEI码匹配，则返回唯一的这个设备的id
			EpDevice epd_one = ret_epds.get(0);
			return epd_one.getId();
		}
		
		if(epds_size > 1) {	// 如果根据IMEI码查询到多个设备信息与之匹配，则直接返回-2
			return -2;
		}
		
		return null;
	}
	
	/*
	 * 根据设备唯一标识码（UDID）获取设备id
	 * 只有返回值大于0时才为正常的设备id，其余null、0或负数时为异常情况
	 */
	/**
	 * 根据设备唯一标识码（UDID）获取设备id
	 * 
	 * @param udid 设备UDID
	 * @return 只有返回值大于0时才为正常的设备id；当返回0时，表示这是一台新设备（数据库中没有该设备的相关信息）；其余null或负数时为异常情况
	 */
	private static Integer getDeviceIdByUdid(String udid) {
		if(StringUtils.isBlank(udid)) {	// 如果传入的IMEI码为空白字符串，则直接返回null
			return null;
		}
		
		String udid_trim = udid.trim();
		EpDevice epd_query = new EpDevice();
		epd_query.setUdid(udid_trim);
		
		List<EpDevice> ret_epds = epDeviceService.getEpDeviceByCriterion(epd_query);
		if(ret_epds == null) {	// 如果根据IMEI码查询设备信息失败，则直接返回-1
			return -1;
		}
		
		int epds_size = ret_epds.size();
		if(epds_size == 0) {	// 如果根据IMEI码未查询到匹配的设备信息，即查询到的匹配的设备信息条数为0时，直接返回0
			return 0;
		}
		
		if(epds_size == 1) {	// 如果查询到唯一的一个设备信息与给定的IMEI码匹配，则返回唯一的这个设备的id
			EpDevice epd_one = ret_epds.get(0);
			return epd_one.getId();
		}
		
		if(epds_size > 1) {	// 如果根据IMEI码查询到多个设备信息与之匹配，则直接返回-2
			return -2;
		}
		
		return null;
	}
	
	/*
	 * 根据用户id和设备id获取设备绑定状态信息
	 * 只有当返回值为非null且为非负值时才为正常情况的返回值，否则如果返回为null或负数时，为异常情况的返回值
	 */
	/**
	 * 根据用户id和设备id获取设备绑定状态信息
	 * 
	 * @param uid 用户id
	 * @param did 设备id
	 * @return 设备绑定状态信息</br>
	 * 各返回值说明：</br>
	 * 		null：根据设备id未查询到该设备的信息；</br>
	 * 		 0：设备绑定状态为“未绑定”；</br>
	 * 		-1：用户id参数为0或负数；</br>
	 * 		-2：设备id参数为0或负数；</br>
	 * 		-3：设备绑定状态字段的值为null；</br>
	 * 		-4：设备绑定信息中的用户id为null；</br>
	 * 		-5：设备绑定信息中的用户id为0或负数；</br>
	 * 		 1：该设备已由自己绑定；</br>
	 * 		 2：该设备已被其他用户绑定。</br>
	 * 		
	 */
	private static Integer getDeviceBoundStatusByEpUidAndDeviceId(int uid, int did) {
		if(uid <= 0) {	// 如果传入的用户id参数为0或负数，则直接返回-1
			return -1;
		}
		
		if(did <= 0) {	// 如果传入的设备id参数为0或负数，则直接返回-2
			return -2;
		}
		
		EpDevice ret_epd = epDeviceService.getEpDeviceById(did);
		if(ret_epd == null) {	// 如果根据设备id未查询到该设备的信息，则直接返回null
			return null;
		}
		
		Boolean bind_status = ret_epd.getBind_status();	// 得到设备的绑定状态字段的值
		if(bind_status == null) {	// 如果绑定状态字段的值为null，则直接返回-3，表示设备绑定状态异常
			return -3;
		}
		
		Integer ep_uid = ret_epd.getEp_uid();	// 得到设备对应的用户id信息
		if(ep_uid == null) {	// 如果查询到的设备信息中的用户id为null，则表示该设备对应的用户信息异常，直接返回-4
			return -4;
		}
		
		if(ep_uid <= 0) {	// 如果查询到的设备信息中的用户id为0或负数，则直接返回-5
			return -5;
		}
		
		if(bind_status == false) {	// 如果绑定状态为“未绑定”，则直接返回0
			return 0;
		}
		
		if(ep_uid == uid) {	// 如果绑定状态为“已绑定”，且已绑定的用户id（ep_uid）的值等于传入的用户id（uid）的值，则说明该设备已由自己绑定，此时直接返回1,；否则，说明该设备已被其他用户绑定，返回2
			return 1;
		}
		
		return 2;	// 该设备已被其他用户绑定，返回2
	}
	
	
	/*
	 * 获取设备绑定状态（工号、IMEI）
	 */
	@RequestMapping(params = "getBindStatus", produces = "application/json; charset=UTF-8")
	@ResponseBody
	@Deprecated
	public String getBindStatusJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");
		String imei = request.getParameter("imei");
		
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(imei)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或设备唯一标识码参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer device_id = getDeviceIdByImei(imei);
		if(device_id == 0) {	 // 当前设备尚未在系统中绑定过，没有相关绑定信息记录，此时直接返回0表示该设备是一台新设备
			jsonObj.put("ret_code", 3);
			jsonObj.put("ret_message", "该设备是一台新设备（数据库中未找到该设备的相关信息）");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		if(device_id == null || device_id < 0) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "根据设备唯一标识码获取设备id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		/*
		EpDevice epd_query = new EpDevice();
		epd_query.setEp_uid(ep_uid);
		epd_query.setId(device_id);
		List<EpDevice> ret_epds = epDeviceService.getEpDeviceByCriterion(epd_query);
		if(ret_epds == null) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "查询设备绑定状态时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int epds_size = ret_epds.size();	// 得到按用户id和设备id的条件查询到匹配的设备信息条数
		if(epds_size == 0) {
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "未查询到匹配的设备信息");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(epds_size > 1) {
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "查询到多个匹配的设备信息");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 当epds_size=1时，即查询到唯一一条匹配的设备信息
		EpDevice epd_one = ret_epds.get(0);	// 取出唯一匹配的这条设备信息
		*/
		
		Integer ret_bind_status = getDeviceBoundStatusByEpUidAndDeviceId(ep_uid, device_id);
		if(ret_bind_status == null || ret_bind_status < 0) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "根据用户id和设备id获取设备绑定状态异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 0) {	// 如果设备为“未绑定”状态，则直接返回0
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备目前处于未绑定状态");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 1) {	// 如果设备为“已绑定”状态，且为传入的工号对应的用户所绑定，则返回1
			jsonObj.put("ret_code", 1);
			jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备已由用户自己绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 2) {	// 如果设备为“已绑定”状态，且已由其他用户所绑定，则返回2
			jsonObj.put("ret_code", 2);
			jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备已由其他用户绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		jsonObj.put("ret_code", -5);
		jsonObj.put("ret_message", "未知错误或异常");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	/*
	 * 查询绑定设备个数或次数是否已超过限制（传入工号和设备IMEI）
	 */
	@RequestMapping(params = "getIfExceedLimit_deprecated", produces = "application/json; charset=UTF-8")
	@ResponseBody
	@Deprecated
	public String getIfExceedLimitJsonOld(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("num_left", null);	// 在返回结果中加入当前用户剩余可绑定的设备个数，初值设为null
		jsonObj.put("times_left", null);	// 在返回结果中加入当前设备剩余可绑定的次数，初值设为null
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");
		String imei = request.getParameter("imei");
		
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(imei)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或设备唯一标识码参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer device_id = getDeviceIdByImei(imei);
//		if(device_id == 0) {	// 如果当前设备是一台新设备，则
//			
//		}
		if(device_id == null || device_id < 0) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "根据设备唯一标识码获取设备id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		EpDevice epd_query = new EpDevice();
		epd_query.setEp_uid(ep_uid);
		int bound_count = epDeviceService.getDeviceBoundCount(epd_query);	// 根据用户id获取该用户已绑定的设备个数
		
		// 开始根据用户id获取该用户最多同时绑定的设备个数
		EpUser epu_info = epUserService.getEpUserById(ep_uid);	// 首先根据用户id获取用户信息
		if(epu_info == null) {	// 如果获取用户信息失败，则直接返回错误信息
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "获取用户信息失败");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int max_device_num = epu_info.getMax_device_num();	// 从用户信息中取得该用户最多同时绑定的设备数
//		int max_bind_times = epu_info.getMax_bind_times();	// 从用户信息中取得该用户在有效时间内每台设备最多绑定次数
		
//		if(max_device_num < 0 || max_bind_times < 0) {	// 如果最多同时能绑定的设备个数或在有效时间内每台设备最多的绑定次数为负数，则直接返回错误信息
//			jsonObj.put("ret_code", -5);
//			jsonObj.put("ret_message", "当前用户最多同时绑定的设备数或每台设备最多绑定次数异常");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
//		
//		if(max_device_num == 0 || max_bind_times == 0) {	// 如果最多同时能绑定的设备个数或在有效时间内每台设备最多的绑定次数为0，则说明该用户已被禁止绑定设备，返回相应的提示信息
//			jsonObj.put("ret_code", 0);
//			jsonObj.put("ret_message", "当前用户已被禁止绑定设备");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		if(max_device_num < 0) {	// 如果最多同时能绑定的设备个数为负数，则直接返回错误信息
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "当前用户最多同时绑定的设备数异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(max_device_num == 0) {	// 如果最多同时能绑定的设备个数为0，则说明该用户已被禁止绑定设备，返回相应的提示信息
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "当前用户已被禁止绑定设备");
//			jsonObj.put("num_left", 0);
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 只要当前用户没有被禁止绑定设备，他的剩余可绑定设备的个数都可以用 (max_device_num-bound_count) 这个表达式来计算得到。如果计算的结果是一个负数，则其绝对值表示的是已超出最大值可绑定的设备个数
		jsonObj.put("num_left", max_device_num-bound_count);	// 注：
		
		// 只有该用户最多同时能绑定的设备个数为正数时，该用户的设备绑定功能才是正常的
		if(bound_count == max_device_num) {	// 如果当前用户已绑定的设备个数已达到该用户最大能同时绑定的设备个数时，说明该用户已不能再继续绑定新设备
			jsonObj.put("ret_code", -7);
			jsonObj.put("ret_message", "当前用户已绑定的设备数量已达最大值");
//			jsonObj.put("num_left", 0);
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(bound_count > max_device_num) {	// 如果当前用户已绑定的设备个数已超过该用户最大能同时绑定的设备个数时，则说明该用户已绑定的设备个数已出现异常
			jsonObj.put("ret_code", -8);
			jsonObj.put("ret_message", "当前用户已绑定的设备数量已超出最大值");
//			jsonObj.put("num_left", max_device_num-bound_count);	// 注：这里超出最大可绑定设备个数后，剩余可绑定设备个数的值为一个负整数，该负整数的绝对值即表示已超出可绑定的设备个数
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 下面开始属于当前用户已绑定的设备数量未超过该用户最大能同时绑定的设备个数的范围
		
		jsonObj.put("num_left", max_device_num-bound_count);	// 当前用户剩余可绑定设备个数，从这里开始就是一个正整数了，表示他还可以继续绑定多少个设备
		
		if(device_id == 0) {	// 如果当前设备是一台新设备，则可以直接返回绑定设备个数和设备绑定次数未超过限制的正常提示信息
			jsonObj.put("ret_code", 5);
			jsonObj.put("ret_message", "用户已绑定的设备个数未超过限制，且当前设备是一台新设备，可进行绑定操作");
			jsonObj.put("times_left", EpDevice.DEFAULT_MAX_BOUND_TIMES);	// 对于新设备，剩余可绑定次数默认就是每台设备的默认最大可绑定次数，因为新设备的已绑定次数可以看作是0次。
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		EpDevice device_info = epDeviceService.getEpDeviceById(device_id);	// 根据设备id获取设备信息
		if(device_info == null) {	// 如果获取设备信息失败，则直接返回错误信息
			jsonObj.put("ret_code", -9);
			jsonObj.put("ret_message", "获取设备信息时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Date bind_start_time = device_info.getBind_start_time();	// 取得该设备的绑定次数生效时间
		Integer bind_count = device_info.getBind_count();	// 取得该设备的已绑定次数
		Integer bind_valid_time = device_info.getBind_valid_time();	// 取得该设备的绑定次数有效时间（以天为单位，默认初值为一年，即365天）
		Boolean bind_status = device_info.getBind_status();	// 取得该设备的绑定状态
		Integer max_bound_times = device_info.getMax_bound_times();	// 取得该设备最多绑定次数
		
		// 如果上述3个关键信息有一个为空或数值字段的值为负数，则直接返回错误信息
		if(bind_start_time == null || bind_count == null || bind_valid_time == null || max_bound_times == null || bind_count < 0 || bind_valid_time < 0 || max_bound_times < 0) {
			jsonObj.put("ret_code", -9);
			jsonObj.put("ret_message", "当前设备的绑定次数相关信息异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		jsonObj.put("bind_status", bind_status==null?false:bind_status);	// 返回的Json数据中加入设备当前的绑定状态，表示该设备是否已被绑定（null默认为未绑定，即false）
		
		// 如果在有效时间内该设备最多绑定次数为0，则认为该设备已被禁止绑定，此时直接返回错误提示信息
		if(max_bound_times == 0) {
			jsonObj.put("ret_code", -10);
			jsonObj.put("ret_message", "当前设备已被禁止绑定");
//			jsonObj.put("times_left", 0);
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// //  只要当前设备没有被禁止绑定，它的剩余可绑定次数都可以用(max_bound_times-bind_count)这个表达式来计算得到。如果为负值，则其绝对值表示已超过最大可绑定的次数
		jsonObj.put("times_left", max_bound_times-bind_count);
		
		if(bind_valid_time == 0) {	// 如果绑定次数有效时间的值为0，则特别约定该设备的绑定次数无有效时间限制，即该设备的已绑定次数终生有效不清零
			if(bind_count == max_bound_times) {	// 如果当前设备的已绑定次数已达到该设备最多绑定次数，则返回错误信息
				jsonObj.put("ret_code", -11);
				jsonObj.put("ret_message", "当前设备已绑定次数已达到该设备最多绑定次数");
//				jsonObj.put("times_left", 0);
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			if(bind_count > max_bound_times) {	// 如果当前设备的已绑定次数已超过该设备最多绑定次数，则返回错误信息
				jsonObj.put("ret_code", -12);
				jsonObj.put("ret_message", "当前设备已绑定次数已超过该设备最多绑定次数");
//				jsonObj.put("times_left", max_bound_times-bind_count);
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 如果当前设备的已绑定次数小于该用户每台设备最多绑定次数，则说明当前设备还可以继续再绑定（至少一次）。
			// 同时此时也处于已绑定的设备个数小于最大值的条件下，所以可以返回正常提示信息。
			jsonObj.put("ret_code", 1);
			jsonObj.put("ret_message", "用户已绑定的设备个数和设备已绑定的次数均未超过限制");
//			jsonObj.put("bind_status", bind_status==null?false:bind_status);	// 返回的Json数据中加入设备当前的绑定状态，表示该设备是否已被绑定（null默认为未绑定，即false）
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果绑定次数有效时间大于0，则首先要验证当前已绑定次数是否已过期（即判断“绑定次数生效时间+绑定次数有效时间”(即绑定次数失效时间)是否早于当前系统时间，如果早于则说明已过期，否则说明还未过期）
		Date bind_expired_time = DateUtil.calcXDaysAfterADate(bind_valid_time, bind_start_time);	// 根据绑定次数生效时间和绑定次数有效时间计算得到绑定次数过期时间
		if(bind_expired_time == null) {
			jsonObj.put("ret_code", -13);
			jsonObj.put("ret_message", "计算绑定次数失效时间时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Date now_time = new Date();	// 取得当前系统时间
		if(now_time.after(bind_expired_time)) {	// 如果当前时间已经晚于绑定次数失效时间，换句话说，就是绑定次数失效时间早于当前时间，则说明已绑定次数已过期，需要进行绑定次数相关的重置操作
			EpDevice epd_mod = new EpDevice();	// 准备用于重置修改设备已绑定次数相关的设备信息对象
			epd_mod.setId(device_id);	// 设置将要重置信息的设备id
			// 如果该设备当前的绑定状态为null时，则默认认为该设备处于未绑定状态，即如果为null时，则设置绑定状态为false
			// 此时，如果设备的绑定状态为false时，则设置绑定次数生效时间为null，已绑定次数为0
			if(bind_status == null || bind_status == false) {
				epd_mod.setBind_start_time(null);	// 设置绑定次数已过期的未绑定状态的设备的绑定次数生效时间为null的原因是，因为该设备目前是未绑定状态，尚不清楚下次绑定会在什么时候，所以用null来表示该字段的值暂时是未知的
				epd_mod.setBind_count(0);	// 将绑定次数已过期的未绑定状态的设备的已绑定次数重置为0的原因是，该设备的绑定次数已过期，且目前处于未绑定状态，所以绑定次数自然要重新计算，从0开始
				epd_mod.setName(device_info.getName());
				epd_mod.setImei(imei);
				epd_mod.setEp_uid(device_info.getEp_uid());
				epd_mod.setBind_valid_time(bind_valid_time);
				epd_mod.setBind_status(bind_status);
				epd_mod.setPlatform(device_info.getPlatform());
				epd_mod.setMax_bound_times(max_bound_times);
				
				// 开始更新该设备的相关信息
				int mod_ret = epDeviceService.modifyEpDeviceByIdIncludingNull(epd_mod);
				if(mod_ret <= 0) {
					jsonObj.put("ret_code", -14);
					jsonObj.put("ret_message", "重置设备的绑定次数相关信息时出现异常，当前设备未绑定");
					jsonObj.put("times_left", null);	// 如果重置设备绑定次数相关信息的操作失败，则将返回的剩余可绑定次数设为null，表示出现了异常
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				// 如果重置设备绑定次数相关信息成功，则可以开始返回设备绑定个数和次数未超限制的正常提示信息
				jsonObj.put("ret_code", 2);
				jsonObj.put("ret_message", "重置设备的绑定次数后，绑定设备个数和设备绑定次数均未超过限制，当前设备未绑定");
				jsonObj.put("times_left", max_bound_times);	// 如果重置绑定次数相关的操作成功，那么该设备的已绑定次数就会从0开始，那么它的剩余可绑定次数自然就是它的最大可绑定次数了
//				jsonObj.put("bind_status", bind_status==null?false:bind_status);	// 返回的Json数据中加入设备当前的绑定状态，表示该设备是否已被绑定（null默认为未绑定，即false）
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			// 如果该设备当前的绑定状态不为null，且绑定状态为true时，即绑定状态为已绑定时，将该设备的已绑定次数重置为1，绑定次数生效时间重设为当前时间
			epd_mod.setBind_count(1);
			epd_mod.setBind_start_time(now_time);
			// 开始更新该设备的相关信息
			int mod_ret = epDeviceService.modifyEpDeviceById(epd_mod);
			if(mod_ret <= 0) {
				jsonObj.put("ret_code", -15);
				jsonObj.put("ret_message", "重置设备的绑定次数相关信息时出现异常，当前设备已绑定");
				jsonObj.put("times_left", null);	// 如果重置设备绑定次数相关信息的操作失败，则将返回的剩余可绑定次数设为null，表示出现了异常
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 如果重置设备绑定次数相关信息成功，则可以开始返回设备绑定个数和次数未超限制的正常提示信息
			jsonObj.put("times_left", max_bound_times-1);	// 如果重置设备绑定次数相关信息的操作成功，由于当前设备处于已绑定状态，所以它的剩余可绑定次数就要用最大可绑定次数max_bound_times减1
			if(max_bound_times == 1) {	// 如果当前设备最大绑定次数（有效时间内）为1，则即使已将设备的已绑定次数重置为了1次，此时该设备的已绑定次数仍已达到了该设备的最大值限制，所以还是要返回已达上限的错误信息
				jsonObj.put("ret_code", -16);
				jsonObj.put("ret_message", "重置设备的绑定次数后，由于该设备的最大绑定次数限制为一次，绑定次数已达最大值，当前设备已绑定");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 如果当前设备最大绑定次数（有效时间内）大于1，则此时在将设备的已绑定次数重置为了1的情况下，该设备的已绑定次数就一定是小于该设备的最大限制值的，所以可以返回正常的提示信息
			jsonObj.put("ret_code", 3);
			jsonObj.put("ret_message", "重置设备的绑定次数后，绑定设备个数和设备绑定次数均未超过限制，当前设备已绑定");
//			jsonObj.put("bind_status", bind_status==null?false:bind_status);	// 返回的Json数据中加入设备当前的绑定状态，表示该设备是否已被绑定（null默认为未绑定，即false）
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果当前时间早于或等于绑定次数失效时间，换言之就是绑定次数失效时间晚于或等于当前时间，则说明该设备的已绑定次数未过期，此时不需要进行绑定次数相关的重置操作
		if(bind_count >= max_bound_times) {	// 如果该设备的已绑定次数达到或超过了该设备的最大绑定次数，则直接返回错误提示信息
			jsonObj.put("ret_code", -17);
			jsonObj.put("ret_message", "该设备的已绑定次数达到或超过了该设备的最大绑定次数（有效时间内）");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		// 如果该设备的已绑定次数小于该设备的最大绑定次数，则说明该设备的已绑定次数还未达到该设备的最大值限制，此时返回正常提示信息
		jsonObj.put("ret_code", 4);
		jsonObj.put("ret_message", "该设备的已绑定次数尚未达到该设备绑定次数限制值（有效时间内）");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
//		return null;
	}
	
	
	/*
	 * 用户绑定设备（传入工号、设备IMEI、平台类型）
	 */
	@RequestMapping(params = "bindDevice_deprecated", produces = "application/json; charset=UTF-8")
	@ResponseBody
	@Deprecated
	public String bindDeviceJsonOld(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");	// 用户工号
		String imei = request.getParameter("imei");	// 设备唯一标识码（IMEI）
		String device_name = request.getParameter("device_name");	// 设备名称（非必填）
		String platform = request.getParameter("platform");	// 设备平台类型（目前只有Android和iOS两大平台）
		
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(imei) || StringUtils.isBlank(platform)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或设备唯一标识码或平台类型参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 首先验证容易验证的平台类型参数是否正确
		if(!"Android".equalsIgnoreCase(platform) && !"iOS".equalsIgnoreCase(platform)) {	// 如果既不是Android平台，也不是iOS平台，则直接返回错误信息
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "未知的平台类型");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果设备名称为空，则设置一个占位字符串“<NONE>”
		if(StringUtils.isBlank(device_name)) {
			device_name = "<NONE>";
		}
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer device_id = getDeviceIdByImei(imei);
		// 如果当前设备之前没有被任何人绑定过，数据库中是没有该设备IMEI的记录信息的，所以要考虑这种情况下，直接可以绑定新加入系统的设备！
		if(device_id == 0) {
			EpDevice epd_first_bind = new EpDevice();	// 新创建一个设备绑定信息对象，“first”表示这台设备是第一次绑定
			epd_first_bind.setName(device_name);
			epd_first_bind.setImei(imei);
			epd_first_bind.setEp_uid(ep_uid);
			epd_first_bind.setBind_start_time(new Date());
			epd_first_bind.setBind_count(1);
			epd_first_bind.setBind_valid_time(EpDevice.DEFAULT_BIND_VALID_TIME);
			epd_first_bind.setBind_status(true);
			epd_first_bind.setPlatform(platform);
			epd_first_bind.setMax_bound_times(EpDevice.DEFAULT_MAX_BOUND_TIMES);
			int add_ret = epDeviceService.addEpDevice(epd_first_bind);
			if(add_ret <= 0) {
				jsonObj.put("ret_code", -4);
				jsonObj.put("ret_message", "添加一条新的设备绑定信息时出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 如果添加新设备的绑定信息正常，则返回正常提示信息
			jsonObj.put("ret_code", 1);
			jsonObj.put("ret_message", "新设备绑定成功");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		if(device_id == null || device_id < 0) {
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "根据设备唯一标识码获取设备id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer ret_bind_status = getDeviceBoundStatusByEpUidAndDeviceId(ep_uid, device_id);
		if((ret_bind_status == null || ret_bind_status < 0) && ret_bind_status != -3) {	// ret_bind_status = -3 表示该设备的绑定状态的值为null，这种情况需要单独处理，默认认为null等价于false，即表示未绑定状态
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "根据用户id和设备id获取设备绑定状态异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		if(ret_bind_status == 0) {	// 如果设备为“未绑定”状态，则直接返回0
//			jsonObj.put("ret_code", 0);
//			jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备目前处于未绑定状态");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		if(ret_bind_status == 1) {	// 如果设备为“已绑定”状态，且为传入的工号对应的用户所绑定，则直接返回-6错误信息
			jsonObj.put("ret_code", -7);
			jsonObj.put("ret_message", "该设备已由用户自己绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 2) {	// 如果设备为“已绑定”状态，且已由其他用户所绑定，则直接返回-7错误信息
			jsonObj.put("ret_code", -8);
			jsonObj.put("ret_message", "该设备已由其他用户绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		jsonObj.put("ret_code", -5);
//		jsonObj.put("ret_message", "未知错误或异常");
//		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
		// 只有当该设备处于未绑定状态时（即ret_bind_status = 0 时），用户才可进行绑定操作。
		// 除了设备本身要处于未绑定状态这个条件以外，用户自身还要满足另外两个条件才能绑定该设备：
		//   1.用户当前已绑定的设备个数要小于他最多能同时绑定的设备个数，2.在有效时间内，他所要绑定的设备的已绑定次数不能超过该设备自身的最多绑定次数限制
		EpDevice epd_query = new EpDevice();
		epd_query.setEp_uid(ep_uid);
		int bound_count = epDeviceService.getDeviceBoundCount(epd_query);	// 根据用户id获取该用户已绑定的设备个数
		
		// 开始根据用户id获取该用户最多同时绑定的设备个数
		EpUser epu_info = epUserService.getEpUserById(ep_uid);	// 首先根据用户id获取用户信息
		if(epu_info == null) {	// 如果获取用户信息失败，则直接返回错误信息
			jsonObj.put("ret_code", -9);
			jsonObj.put("ret_message", "获取用户信息失败");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int max_device_num = epu_info.getMax_device_num();	// 从用户信息中取得该用户最多同时绑定的设备数
		
		if(max_device_num < 0) {	// 如果最多同时能绑定的设备个数为负数，则直接返回错误信息
			jsonObj.put("ret_code", -10);
			jsonObj.put("ret_message", "当前用户最多同时绑定的设备数异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(max_device_num == 0) {	// 如果最多同时能绑定的设备个数为0，则说明该用户已被禁止绑定设备，返回相应的提示信息
			jsonObj.put("ret_code", -11);
			jsonObj.put("ret_message", "当前用户已被禁止绑定设备");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 只有该用户最多同时能绑定的设备个数为正数时，该用户的设备绑定功能才是正常的
		if(bound_count == max_device_num) {	// 如果当前用户已绑定的设备个数已达到该用户最大能同时绑定的设备个数时，说明该用户已不能再继续绑定新设备
			jsonObj.put("ret_code", -12);
			jsonObj.put("ret_message", "当前用户已绑定的设备数量已达最大值");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(bound_count > max_device_num) {	// 如果当前用户已绑定的设备个数已超过该用户最大能同时绑定的设备个数时，则说明该用户已绑定的设备个数
										// 已出现异常（可能是因为之前已绑定过，然后又被减少了最多可绑定设备个数，甚至被减少到0个，即被禁止绑定设备的情况）
			jsonObj.put("ret_code", -13);
			jsonObj.put("ret_message", "当前用户已绑定的设备数量已超出最大值");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 下面开始属于当前用户已绑定的设备数量未超过该用户最大能同时绑定的设备个数的情况
		
		EpDevice device_info = epDeviceService.getEpDeviceById(device_id);	// 根据设备id获取设备信息
		if(device_info == null) {	// 如果获取设备信息失败，则直接返回错误信息
			jsonObj.put("ret_code", -14);
			jsonObj.put("ret_message", "获取设备信息时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Date bind_start_time = device_info.getBind_start_time();	// 取得该设备的绑定次数生效时间
		Integer bind_count = device_info.getBind_count();	// 取得该设备的已绑定次数
		Integer bind_valid_time = device_info.getBind_valid_time();	// 取得该设备的绑定次数有效时间（以天为单位，默认初值为一年，即365天）
		Boolean bind_status = device_info.getBind_status();	// 取得该设备的绑定状态
		Integer max_bound_times = device_info.getMax_bound_times();	// 取得该设备最多绑定次数
		
		// 如果上述3个关键信息有一个为空或数值字段的值为负数，则直接返回错误信息
		if(bind_start_time == null || bind_count == null || bind_valid_time == null || max_bound_times == null || bind_count < 0 || bind_valid_time < 0 || max_bound_times < 0) {
			jsonObj.put("ret_code", -15);
			jsonObj.put("ret_message", "当前设备的绑定次数相关信息异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		jsonObj.put("bind_status", bind_status==null?false:bind_status);	// 返回的Json数据中加入设备当前的绑定状态，表示该设备是否已被绑定（null默认为未绑定，即false）
		
		// 如果在有效时间内该设备最多绑定次数为0，则认为该设备已被禁止绑定，此时直接返回错误提示信息
		if(max_bound_times == 0) {
			jsonObj.put("ret_code", -16);
			jsonObj.put("ret_message", "当前设备已被禁止绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(bind_valid_time == 0) {	// 如果绑定次数有效时间的值为0，则特别约定该设备的绑定次数无有效时间限制，即该设备的已绑定次数终生有效不清零
			if(bind_count == max_bound_times) {	// 如果当前设备的已绑定次数已达到该设备最多绑定次数，则返回错误信息
				jsonObj.put("ret_code", -17);
				jsonObj.put("ret_message", "当前设备已绑定次数已达到该设备最多绑定次数");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			if(bind_count > max_bound_times) {	// 如果当前设备的已绑定次数已超过该设备最多绑定次数，则返回错误信息
				jsonObj.put("ret_code", -18);
				jsonObj.put("ret_message", "当前设备已绑定次数已超过该设备最多绑定次数");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 如果当前设备的已绑定次数小于该设备最多绑定次数，则说明当前设备还可以继续再绑定（至少一次）。
			// 同时此时也处于已绑定的设备个数小于最大值的条件下，所以可以开始进行绑定操作。。。
//			jsonObj.put("ret_code", 1);
//			jsonObj.put("ret_message", "用户已绑定的设备个数和设备已绑定的次数均未超过限制");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			EpDevice epd_bind_upd = new EpDevice();	// 新创建一个设备绑定信息对象，用于更新之前已有的设备绑定信息（“upd”即表示“update”，更新的意思）
			epd_bind_upd.setId(device_id);
			epd_bind_upd.setBind_count(bind_count+1);	// 将该设备的已绑定次数加一，表示该设备的已绑定次数自此又增加了一次
			epd_bind_upd.setEp_uid(ep_uid);
			epd_bind_upd.setBind_status(true);
			epd_bind_upd.setPlatform(platform);
			int upd_ret = epDeviceService.modifyEpDeviceById(epd_bind_upd);
			if(upd_ret <= 0) {
				jsonObj.put("ret_code", -19);	// 返回代码待修改。。。
				jsonObj.put("ret_message", "更新设备绑定相关信息时出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 如果更新设备绑定相关信息成功，则可以返回正常提示信息
			jsonObj.put("ret_code", 2);	// 返回代码待修改。。。
			jsonObj.put("ret_message", "更新设备绑定信息成功");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果绑定次数有效时间大于0，则首先要验证当前已绑定次数是否已过期（即判断“绑定次数生效时间+绑定次数有效时间”(即绑定次数失效时间)是否早于当前系统时间，如果早于则说明已过期，否则说明还未过期）
		Date bind_expired_time = DateUtil.calcXDaysAfterADate(bind_valid_time, bind_start_time);	// 根据绑定次数生效时间和绑定次数有效时间计算得到绑定次数过期时间
		if(bind_expired_time == null) {
			jsonObj.put("ret_code", -20);
			jsonObj.put("ret_message", "计算绑定次数失效时间时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Date now_time = new Date();	// 取得当前系统时间
		if(now_time.after(bind_expired_time)) {	// 如果当前时间已经晚于绑定次数失效时间，换句话说，就是绑定次数失效时间早于当前时间，则说明已绑定次数已过期，需要进行绑定次数相关的重置操作
			EpDevice epd_mod = new EpDevice();	// 准备用于重置修改设备已绑定次数相关的设备信息对象
			epd_mod.setId(device_id);	// 设置将要重置信息的设备id
			// 如果该设备当前的绑定状态为null时，则默认认为该设备处于未绑定状态，即如果为null时，则设置绑定状态为false
			// 此时，如果设备的绑定状态为false时，则设置绑定次数生效时间为null，已绑定次数为0
			if(bind_status == null || bind_status == false) {
				epd_mod.setEp_uid(ep_uid);
				epd_mod.setBind_start_time(now_time);
				epd_mod.setBind_count(1);
				epd_mod.setBind_status(true);
				epd_mod.setPlatform(platform);
				// 开始更新该设备的相关信息
				int mod_ret = epDeviceService.modifyEpDeviceById(epd_mod);
				if(mod_ret <= 0) {
					jsonObj.put("ret_code", -21);	// 返回代码待修改。。。
					jsonObj.put("ret_message", "修改设备绑定信息时出现异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				// 如果修改设备绑定相关信息成功，则可以开始返回正常提示信息
				jsonObj.put("ret_code", 3);	// 返回代码待修改。。。
				jsonObj.put("ret_message", "修改设备绑定信息成功");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			// // 下面的设备绑定状态为true的情况，按照前面的判断逻辑来走，应该是不会发生的，因为前面已经限定了只有绑定状态为null或false（即未绑定状态）的设备才允许进行绑定操作
			// 如果该设备当前的绑定状态不为null，且绑定状态为true时，即绑定状态为已绑定时，将该设备的已绑定次数重置为1，绑定次数生效时间重设为当前时间
//			epd_mod.setBind_count(1);
//			epd_mod.setBind_start_time(now_time);
//			// 开始更新该设备的相关信息
//			int mod_ret = epDeviceService.modifyEpDeviceById(epd_mod);
//			if(mod_ret <= 0) {
//				jsonObj.put("ret_code", -21);
//				jsonObj.put("ret_message", "重置设备的绑定次数相关信息时出现异常，当前设备已绑定");
//				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//			}
//			// 如果重置设备绑定次数相关信息成功，则可以开始返回设备绑定个数和次数未超限制的正常提示信息
//			if(max_bound_times == 1) {	// 如果当前设备最大绑定次数（有效时间内）为1，则即使已将设备的已绑定次数重置为了1次，此时该设备的已绑定次数仍已达到了该设备的最大值限制，所以还是要返回已达上限的错误信息
//				jsonObj.put("ret_code", -22);
//				jsonObj.put("ret_message", "重置设备的绑定次数后，由于该设备的最大绑定次数限制为一次，绑定次数已达最大值，当前设备已绑定");
//				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//			}
//			// 如果当前设备最大绑定次数（有效时间内）大于1，则此时在将设备的已绑定次数重置为了1的情况下，该设备的已绑定次数就一定是小于该设备的最大限制值的，所以可以返回正常的提示信息
//			jsonObj.put("ret_code", 3);
//			jsonObj.put("ret_message", "重置设备的绑定次数后，绑定设备个数和设备绑定次数均未超过限制，当前设备已绑定");
////			jsonObj.put("bind_status", bind_status==null?false:bind_status);	// 返回的Json数据中加入设备当前的绑定状态，表示该设备是否已被绑定（null默认为未绑定，即false）
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果当前时间早于或等于绑定次数失效时间，换言之就是绑定次数失效时间晚于或等于当前时间，则说明该设备的已绑定次数未过期，此时不需要进行绑定次数相关的重置操作
		if(bind_count >= max_bound_times) {	// 如果该设备的已绑定次数达到或超过了该设备的最大绑定次数，则直接返回错误提示信息
			jsonObj.put("ret_code", -22);
			jsonObj.put("ret_message", "该设备的已绑定次数达到或超过了该设备的最大绑定次数（有效时间内）");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		// 如果该设备的已绑定次数小于该设备的最大绑定次数，则说明该设备的已绑定次数还未达到该设备的最大值限制，此时可以开始进行绑定操作
		EpDevice epd_within_limit = new EpDevice();
		epd_within_limit.setId(device_id);
		epd_within_limit.setEp_uid(ep_uid);
		epd_within_limit.setBind_count(bind_count+1);
		epd_within_limit.setBind_status(true);
		epd_within_limit.setPlatform(platform);
		// 开始更新该设备的相关信息
		int mod_ret = epDeviceService.modifyEpDeviceById(epd_within_limit);
		if(mod_ret <= 0) {
			jsonObj.put("ret_code", -23);	// 返回代码待修改。。。
			jsonObj.put("ret_message", "修改设备绑定信息时出现异常（有效时间内）");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		// 如果修改设备绑定相关信息成功，则可以开始返回正常提示信息
		jsonObj.put("ret_code", 4);	// 返回代码待修改。。。
		jsonObj.put("ret_message", "修改设备绑定信息成功（有效时间内）");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
//		return null;
	}
	
	/*
	 * 用户解绑设备（传入工号和设备IMEI）
	 */
	@RequestMapping(params = "unbindDevice_deprecated", produces = "application/json; charset=UTF-8")
	@ResponseBody
	@Deprecated
	public String unbindDeviceJsonOld(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");
		String imei = request.getParameter("imei");
		
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(imei)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或设备唯一标识码参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer device_id = getDeviceIdByImei(imei);
		if(device_id == 0) {	// 如果得到的device_id=0，则说明该设备是一台新设备，此时不能进行解绑操作，直接返回错误提示信息
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "当前设备是一台新设备，不能进行解绑操作");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		if(device_id == null || device_id < 0) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "根据设备唯一标识码获取设备id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 下面开始属于获取到的设备id（device_id）为正数（正常值）的情况范围
		
		// 首先根据用户id和设备id查询该设备的状态是否绑定、由谁所绑定，然后分情况分别进行处理。。。
		Integer ret_bind_status = getDeviceBoundStatusByEpUidAndDeviceId(ep_uid, device_id);
		if((ret_bind_status == null || ret_bind_status < 0) && ret_bind_status != -3) {	// ret_bind_status = -3 表示该设备的绑定状态的值为null，这种情况需要单独处理，默认认为null等价于false，即表示未绑定状态
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "根据用户id和设备id获取设备绑定状态异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == -3 || ret_bind_status == 0) {	// 如果设备为null（默认也视为未绑定状态）或“未绑定”状态，则不能进行解绑操作，直接返回错误提示信息
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "该设备目前处于未绑定状态，不能进行解绑操作");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		if(ret_bind_status == 1) {	// 如果设备为“已绑定”状态，且为传入的工号对应的用户所绑定，则直接返回-6错误信息
//			jsonObj.put("ret_code", -7);
//			jsonObj.put("ret_message", "该设备已由用户自己绑定");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		if(ret_bind_status == 2) {	// 如果设备为“已绑定”状态，且已由其他用户所绑定，则直接返回-7错误信息
			jsonObj.put("ret_code", -7);
			jsonObj.put("ret_message", "该设备已由其他用户绑定，不能进行解绑操作");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果 ret_bind_status == 1，则说明该设备已由当前用户自己所绑定，可以进行解绑操作
		EpDevice unbind_epd = new EpDevice();
		unbind_epd.setId(device_id);
		unbind_epd.setBind_status(false);
		int unbind_ret = epDeviceService.modifyEpDeviceById(unbind_epd);
		if(unbind_ret <= 0) {
			jsonObj.put("ret_code", -8);
			jsonObj.put("ret_message", "解绑定设备操作失败");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		// unbind_ret>0时，解绑定设备成功，返回正常提示信息
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "解绑定设备成功");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
//		return null;
	}
	
	
	/*
	 * 查询当前已登录用户已绑定的设备列表（传入工号）
	 */
	@RequestMapping(params = "getDeviceBoundList", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getDeviceBoundListJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("devices_list", null);	// 已绑定的设备列表Json数据
		jsonObj.put("devices_count", null);	// 已绑定的设备个数
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");
//		String imei = request.getParameter("imei");
//		String udid = request.getParameter("udid");
		
		if(StringUtils.isBlank(work_no)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		EpDevice epd_query = new EpDevice();
		epd_query.setEp_uid(ep_uid);
		epd_query.setBind_status(true);
		List<EpDevice> bound_devices = epDeviceService.getEpDeviceByCriterion(epd_query);
		if(bound_devices == null) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "查询用户已绑定设备时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		JSONArray devices_arr = new JSONArray();	// 用来存放最后要返回的用户已绑定设备列表Json数据
		
		int devices_count = bound_devices.size();
		if(devices_count == 0) {
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "当前用户已绑定设备列表为空");
			jsonObj.put("devices_list", devices_arr);
			jsonObj.put("devices_count", 0);
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		for(EpDevice epd:bound_devices) {
			JSONObject bound_device = new JSONObject();
			bound_device.put("name", epd.getName());
			bound_device.put("udid", epd.getUdid());
			bound_device.put("platform", epd.getPlatform());
			bound_device.put("last_bind_time", DateUtil.formatDate(2, epd.getLast_bound_time()));	// 上一次绑定该设备的时间（格式化成“yyyy-MM-dd HH:mm:ss”日期格式）
			devices_arr.add(bound_device);
		}
		jsonObj.put("devices_list", devices_arr);
		jsonObj.put("devices_count", devices_count);
		
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "获取当前用户已绑定设备列表成功");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	
	/*
	 * 查询当前设备是否已绑定（传入工号、UDID）
	 * 
	 */
	/**
	 * 查询当前设备是否已绑定（传入工号、UDID）
	 * 
	 * 可能的情况：
	 * 1. 没有该设备的相关记录（即这台设备是一台新设备）
	 * 2. 当前设备处于未绑定状态，可以进行绑定操作
	 * 3. 当前设备已由当前用户自己所绑定，可以直接进入APP
	 * 4. 当前设备已由其他用户所绑定，不能进入APP（同一设备同一时间最多只能绑定一个用户）
	 * 
	 */
	@RequestMapping(params = "getIfDeviceBound", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getIfDeviceBoundJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");
		String udid = request.getParameter("udid");
		
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(udid)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或UDID参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer device_id = getDeviceIdByUdid(udid);
		if(device_id == 0) {	// 如果得到的device_id=0，则说明该设备是一台新设备，对于新设备而言，可以默认视之为未绑定的，所以可以直接返回正常提示信息
			jsonObj.put("ret_code", 3);
			jsonObj.put("ret_message", "当前设备是一台新设备，可以进行绑定操作");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		if(device_id == null || device_id < 0) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "根据设备唯一标识码获取设备id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 下面开始属于获取到的设备id（device_id）为正数（正常值）的情况范围
		
		Integer ret_bind_status = getDeviceBoundStatusByEpUidAndDeviceId(ep_uid, device_id);
		if(ret_bind_status == null || ret_bind_status < 0) {
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "根据用户id和设备id获取设备绑定状态异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 0) {	// 如果设备为“未绑定”状态，则直接返回0
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备目前处于未绑定状态");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 1) {	// 如果设备为“已绑定”状态，且为传入的工号对应的用户所绑定，则返回1
			jsonObj.put("ret_code", 1);
			jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备已由用户自己绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 2) {	// 如果设备为“已绑定”状态，且已由其他用户所绑定，则返回2
			jsonObj.put("ret_code", 2);
			jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备已由其他用户绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		jsonObj.put("ret_code", -6);
		jsonObj.put("ret_message", "未知错误或异常");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
//		return null;
	}
	
	
	/**
	 * 根据用户id获取用户已绑定设备个数或累计已绑定次数是否超过限制
	 * 
	 * @param uid 用户id
	 * @return 用户已绑定设备个数或累计已绑定次数是否超过了该用户的限制最大值</br>
	 * 		各返回值及对应含义如下：</br>
	 * 		null:根据用户id获取用户信息失败</br>
	 * 		  -1:传入的用户id为负数或0</br>
	 * 		  -2:初始化用户绑定设备个数或次数相关信息失败</br>
	 * 		  -3:当前用户绑定设备个数或次数相关信息异常</br>
	 * 		  -4:当前用户已被禁止绑定设备（即，最多同时能绑定的设备个数或在有效时间内累计最多的绑定次数为0）</br>
	 * 		  -5:当前用户已绑定的设备数量或累计已绑定次数已达到或超过最大值（无有效时间限制）</br>
	 * 		  -6:计算得到的绑定次数失效时间出现异常</br>
	 * 		  -7:重置用户已绑定设备个数或累计次数相关信息异常</br>
	 * 		  -8:当前用户已绑定的设备数量或累计已绑定次数已达到或超过最大值（有效时间内）</br>
	 * 		  -9:当前用户已绑定设备个数已达到或超过最大值（重置绑定次数后）</br>
	 * 		  -10:当前用户最多只能绑定1台设备，且已绑定一台或多台设备，但剩余可绑定次数没有超限（无有效时间限制）</br>
	 * 		  -11:当前用户最多只能绑定1台设备，且已绑定一台或多台设备，但剩余可绑定次数没有超限（重置绑定次数后）</br>
	 * 		  -12:当前用户最多只能绑定1台设备，且已绑定一台或多台设备，但剩余可绑定次数没有超限（有效时间内）</br>
	 * 		   1:剩余可绑定设备个数和可绑定次数都为正数（无有效时间限制）</br>
	 * 		   2:当前用户已绑定设备个数和累计已绑定次数均未超过限制（重置绑定次数后）</br>
	 * 		   3:当前用户已绑定设备个数和累计已绑定次数均未超过限制（有效时间内）</br>
	 * 
	 */
	private static Integer getIfExceedLimitByEpUid(int uid) {
		if(uid <= 0) {	// 如果传入的用户id为负数或0，则直接返回-1
			return -1;
		}
		
		EpDevice epd_query = new EpDevice();
		epd_query.setEp_uid(uid);
		int bound_count = epDeviceService.getDeviceBoundCount(epd_query);	// 根据用户id获取该用户已绑定的设备个数
		
		// 开始根据用户id获取该用户最多同时绑定的设备个数
		EpUser epu_info = epUserService.getEpUserById(uid);	// 首先根据用户id获取用户信息
		if(epu_info == null) {	// 如果获取用户信息失败，则直接返回null
//			jsonObj.put("ret_code", -4);
//			jsonObj.put("ret_message", "获取用户信息失败");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			return null;
		}
		
		Integer max_device_num = epu_info.getMax_device_num();	// 从用户信息中取得该用户最多同时绑定的设备数
		Integer max_bind_times = epu_info.getMax_bind_times();	// 从用户信息中取得该用户在有效时间内累计最多绑定次数
		Integer bind_times_count = epu_info.getBind_times_count();	// 取得该用户目前已累计绑定次数
		Date bind_start_time = epu_info.getBind_start_time();	// 累计绑定次数开始生效时间
		Integer bind_limit_time = epu_info.getBind_limit_time();	// 累计绑定次数有效时间（以天为单位）
		
		EpUser epu_init = new EpUser();	// 用于初始化当前用户的部分信息的对象
		epu_init.setId(uid);
		boolean do_init = false;	// 用于表示是否需要进行初始化操作的标志
		
		if(max_device_num == null) {	// 如果最多同时可绑定的设备个数为Null，则初始化该字段的值为默认值
			max_device_num = EpUser.DEFAULT_MAX_DEVICE_NUM;
			epu_init.setMax_device_num(max_device_num);
			do_init = true;
		}
		if(max_bind_times == null) {
			max_bind_times = EpUser.DEFAULT_MAX_BIND_TIMES*max_device_num;
			epu_init.setMax_bind_times(max_bind_times);
			do_init = true;
		}
		if(bind_times_count == null) {
			bind_times_count = 0;
			epu_init.setBind_times_count(0);
			do_init = true;
		}
		if(bind_limit_time == null) {
			bind_limit_time = EpUser.DEFAULT_BIND_LIMIT_TIME;
			epu_init.setBind_limit_time(bind_limit_time);
			do_init = true;
		}
		
		if(do_init) {
			int init_ret = epUserService.modifyEpUserById(epu_init);
			if(init_ret <= 0) {	// 如果初始化用户绑定设备个数或次数相关信息失败，则直接返回-2
//				jsonObj.put("ret_code", -5);
//				jsonObj.put("ret_message", "初始化用户默认绑定设备个数或次数相关信息异常");
//				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				return -2;
			}
		}
		
		if(max_device_num < 0 || max_bind_times < 0 || bind_times_count < 0 || bind_limit_time < 0) {	// 如果最多同时能绑定的设备个数或在有效时间内累计最多的绑定次数或绑定次数有效时间为负数，则直接返回-3
//			jsonObj.put("ret_code", -6);
//			jsonObj.put("ret_message", "当前用户绑定设备个数或次数相关信息异常");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			return -3;
		}
		
		if(max_device_num == 0 || max_bind_times == 0) {	// 如果最多同时能绑定的设备个数或在有效时间内累计最多的绑定次数为0，则说明该用户已被禁止绑定设备，直接返回-4
//			jsonObj.put("ret_code", -7);
//			jsonObj.put("ret_message", "当前用户已被禁止绑定设备");
//			jsonObj.put("num_left", 0);
//			jsonObj.put("times_left", 0);
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			return -4;
		}
		
//		if(max_device_num < 0) {	// 如果最多同时能绑定的设备个数为负数，则直接返回错误信息
//			jsonObj.put("ret_code", -5);
//			jsonObj.put("ret_message", "当前用户最多同时绑定的设备数异常");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
//		
//		if(max_device_num == 0) {	// 如果最多同时能绑定的设备个数为0，则说明该用户已被禁止绑定设备，返回相应的提示信息
//			jsonObj.put("ret_code", -6);
//			jsonObj.put("ret_message", "当前用户已被禁止绑定设备");
////			jsonObj.put("num_left", 0);
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		// // 只要当前用户没有被禁止绑定设备，他的剩余可绑定设备的个数都可以用 (max_device_num-bound_count) 这个表达式来计算得到。如果计算的结果是一个负数，则其绝对值表示的是已超出最大值可绑定的设备个数
		int num_left = max_device_num - bound_count;	// 注：计算得到剩余可绑定设备个数
		int times_left = max_bind_times - bind_times_count;	// 注：计算得到剩余可绑定次数
//		jsonObj.put("num_left", num_left);
//		jsonObj.put("times_left", times_left);
		
		// 只有该用户最多同时能绑定的设备个数为正数时，该用户的设备绑定功能才是正常的
//		if(bound_count == max_device_num) {	// 如果当前用户已绑定的设备个数已达到该用户最大能同时绑定的设备个数时，说明该用户已不能再继续绑定新设备
//			jsonObj.put("ret_code", -8);
//			jsonObj.put("ret_message", "当前用户已绑定的设备数量已达最大值");
////			jsonObj.put("num_left", 0);
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
//		if(bound_count > max_device_num) {	// 如果当前用户已绑定的设备个数已超过该用户最大能同时绑定的设备个数时，则说明该用户已绑定的设备个数已出现异常
//			jsonObj.put("ret_code", -9);
//			jsonObj.put("ret_message", "当前用户已绑定的设备数量已超出最大值");
////			jsonObj.put("num_left", max_device_num-bound_count);	// 注：这里超出最大可绑定设备个数后，剩余可绑定设备个数的值为一个负整数，该负整数的绝对值即表示已超出可绑定的设备个数
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		// 下面开始属于当前用户已绑定的设备数量未超过该用户最大能同时绑定的设备个数的范围
		
//		jsonObj.put("num_left", max_device_num-bound_count);	// 当前用户剩余可绑定设备个数，从这里开始就是一个正整数了，表示他还可以继续绑定多少个设备
		
		// 开始验证绑定累计次数有没有超过最大值
		// // 
		if(bind_limit_time == 0) {	// 如果累计绑定次数有效时间为0天，则特别约定绑定累计次数终生有效（即永不重置累计绑定次数）
			if(num_left <= 0 && max_device_num == 1 && times_left > 0) {
				return -10;
			}
			if(num_left <= 0 || times_left <= 0) {	// 如果剩余可绑定设备个数或次数为负数或0时，表示当前用户绑定设备的个数或累计绑定次数已超过限制，此时返回-5
//				jsonObj.put("ret_code", -8);
//				jsonObj.put("ret_message", "当前用户已绑定的设备数量或累计已绑定次数已达到或超过最大值（无有效时间限制）");
//				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				return -5;
			}
//			jsonObj.put("ret_code", 1);
//			jsonObj.put("ret_message", "当前用户已绑定设备个数和累计已绑定次数均未超过限制（无有效时间限制）");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			// 如果剩余可绑定设备个数和可绑定次数都为正数，则返回正常正数值
			return 1;
		}
		
		// 如果累计绑定次数有效时间为一个正数，则说明有绑定次数有效时间限制，需要首先验证绑定次数是否在有效时间内。如果绑定次数已过期，则需要重置绑定次数相关信息；如果未过期，则继续验证绑定次数是否超过限制
		// 如果绑定次数有效时间大于0，则首先要验证当前已绑定次数是否已过期（即判断“绑定次数生效时间+绑定次数有效时间”(即绑定次数失效时间)是否早于当前系统时间，如果早于则说明已过期，否则说明还未过期）
		Date bind_expire_time = DateUtil.calcXDaysAfterADate(bind_limit_time, bind_start_time);	// 根据绑定次数生效时间和绑定次数有效时间计算得到绑定次数过期时间
		if(bind_expire_time == null) {
//			jsonObj.put("ret_code", -9);
//			jsonObj.put("ret_message", "计算绑定次数失效时间出现异常");
//			jsonObj.put("times_left", null);	// 若此时出现异常，则剩余可绑定次数的值就是不准确的值了，所以将其值设为null
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			return -6;	// 如果计算得到的绑定次数失效时间出现异常，则直接返回-6
		}
		
		Date now_time = new Date();	// 取得当前系统时间
		if(now_time.after(bind_expire_time)) {	// 如果当前时间已经晚于绑定次数失效时间，换句话说，就是绑定次数失效时间早于当前时间，则说明已绑定次数已过期，需要进行绑定次数相关的重置操作
			EpUser epu_bind_upd = new EpUser(epu_info);	// 复制原用户对象的全部信息，并新建一个用户对象，用来更新用户信息
			epu_bind_upd.setId(uid);
			epu_bind_upd.setBind_times_count(0);	// 将累计已绑定次数重置为0次
			epu_bind_upd.setBind_start_time(null);	// 将累计绑定次数开始生效时间重置为null，因为已绑定次数过期后，尚不清楚下次绑定的时间是什么时候，所以用null来表示
			
			int bind_upd_ret = epUserService.modifyEpUserByIdIncludingNull(epu_bind_upd);
			if(bind_upd_ret <= 0) {
//				jsonObj.put("ret_code", -10);
//				jsonObj.put("ret_message", "重置用户已绑定设备个数或累计次数相关信息异常");
//				jsonObj.put("num_left", null);	// 若此时出现异常，则剩余可绑定设备个数的值就是不准确的值了，所以将其值设为null
//				jsonObj.put("times_left", null);	// 若此时出现异常，则剩余可绑定次数的值就是不准确的值了，所以将其值设为null
//				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				return -7;
			}
			
			if(num_left <= 0 && max_device_num == 1) {
				return -11;
			}
			
			if(num_left <= 0) {
				return -9;
			}
			
			// 如果重置绑定次数相关信息成功，则此时该用户剩余可绑定设备个数和次数就一定是没有超过限制的，可以直接返回正常提示信息
//			jsonObj.put("ret_code", 2);
//			jsonObj.put("ret_message", "当前用户已绑定设备个数和累计已绑定次数均未超过限制（重置绑定次数后）");
//			jsonObj.put("times_left", max_bind_times);	// 重置绑定次数后，剩余可绑定次数就等于该用户最大可绑定次数了
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			return 2;
		}
		
		if(num_left <= 0 && max_device_num == 1 && times_left > 0) {
			return -12;
		}
		
		// 如果绑定次数未过期。。。
		if(num_left <= 0 || times_left <= 0) {	// 如果剩余可绑定设备个数或次数为负数或0时，表示当前用户绑定设备的个数或累计绑定次数已超过限制，此时返回错误提示信息
//			jsonObj.put("ret_code", -11);
//			jsonObj.put("ret_message", "当前用户已绑定的设备数量或累计已绑定次数已达到或超过最大值（有效时间内）");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			return -8;
		}
//		jsonObj.put("ret_code", 3);
//		jsonObj.put("ret_message", "当前用户已绑定设备个数和累计已绑定次数均未超过限制（有效时间内）");
//		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		return 3;
	}
	
	
	/*
	 * 查询绑定设备个数或次数是否已超过限制（传入工号）
	 */
	/**
	 * 查询绑定设备个数或次数是否已超过限制（传入工号）
	 * 
	 * @param request
	 * @return 用户剩余可绑定设备个数（num_left）和剩余可绑定次数（times_left）
	 * 		注：不同情况下，返回的这两个字段的值可能为null、0或负数
	 */
	@RequestMapping(params = "getIfExceedLimit", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getIfExceedLimitJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("num_left", null);	// 在返回结果中加入当前用户剩余可绑定的设备个数，初值设为null
		jsonObj.put("times_left", null);	// 在返回结果中加入当前设备剩余可绑定的次数，初值设为null
		jsonObj.put("num_total", null);	// 在返回结果中加入该用户最多可绑定的设备个数，初值设为null
		jsonObj.put("times_total", null);	// 在返回结果中加入该用户最多可绑定的次数，初值设为null
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");
//		String udid = request.getParameter("udid");
		
		if(StringUtils.isBlank(work_no)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		/*
		Integer device_id = getDeviceIdByUdid(udid);
//		if(device_id == 0) {	// 如果当前设备是一台新设备，则
//			
//		}
		if(device_id == null || device_id < 0) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "根据设备唯一标识码获取设备id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		*/
		
		EpDevice epd_query = new EpDevice();
		epd_query.setEp_uid(ep_uid);
		int bound_count = epDeviceService.getDeviceBoundCount(epd_query);	// 根据用户id获取该用户已绑定的设备个数
		
		// 开始根据用户id获取该用户最多同时绑定的设备个数
		EpUser epu_info = epUserService.getEpUserById(ep_uid);	// 首先根据用户id获取用户信息
		if(epu_info == null) {	// 如果获取用户信息失败，则直接返回错误信息
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "获取用户信息失败");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer max_device_num = epu_info.getMax_device_num();	// 从用户信息中取得该用户最多同时绑定的设备数
		Integer max_bind_times = epu_info.getMax_bind_times();	// 从用户信息中取得该用户在有效时间内累计最多绑定次数
		Integer bind_times_count = epu_info.getBind_times_count();	// 取得该用户目前已累计绑定次数
		Date bind_start_time = epu_info.getBind_start_time();	// 累计绑定次数开始生效时间
		Integer bind_limit_time = epu_info.getBind_limit_time();	// 累计绑定次数有效时间（以天为单位）
		
		EpUser epu_init = new EpUser();	// 用于初始化当前用户的部分信息的对象
		epu_init.setId(ep_uid);
		boolean do_init = false;	// 用于表示是否需要进行初始化操作的标志
		
		if(max_device_num == null) {	// 如果最多同时可绑定的设备个数为Null，则初始化该字段的值为默认值
			max_device_num = EpUser.DEFAULT_MAX_DEVICE_NUM;
			epu_init.setMax_device_num(max_device_num);
			jsonObj.put("num_total", max_device_num);
			do_init = true;
		}
		if(max_bind_times == null) {
			max_bind_times = EpUser.DEFAULT_MAX_BIND_TIMES*max_device_num;
			epu_init.setMax_bind_times(max_bind_times);
			jsonObj.put("times_total", max_bind_times);
			do_init = true;
		}
		if(bind_times_count == null) {
			bind_times_count = 0;
			epu_init.setBind_times_count(0);
			do_init = true;
		}
		if(bind_limit_time == null) {
			bind_limit_time = EpUser.DEFAULT_BIND_LIMIT_TIME;
			epu_init.setBind_limit_time(bind_limit_time);
			do_init = true;
		}
		
		if(do_init) {
//			epu_init.setBind_start_time(new Date());	// TODO: 初始化新用户的绑定次数生效时间，在这里做合不合适？这里用户还没有进行绑定操作
			int init_ret = epUserService.modifyEpUserById(epu_init);
			if(init_ret <= 0) {
				jsonObj.put("ret_code", -5);
				jsonObj.put("ret_message", "初始化用户默认绑定设备个数或次数相关信息异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
		}
		
		if(max_device_num < 0 || max_bind_times < 0 || bind_times_count < 0 || bind_limit_time < 0) {	// 如果最多同时能绑定的设备个数或在有效时间内累计最多的绑定次数为负数，则直接返回错误信息
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "当前用户绑定设备个数或次数相关信息异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(max_device_num == 0 || max_bind_times == 0) {	// 如果最多同时能绑定的设备个数或在有效时间内累计最多的绑定次数为0，则说明该用户已被禁止绑定设备，返回相应的提示信息
			jsonObj.put("ret_code", -7);
			jsonObj.put("ret_message", "当前用户已被禁止绑定设备");
			jsonObj.put("num_left", 0);
			jsonObj.put("times_left", 0);
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		if(max_device_num < 0) {	// 如果最多同时能绑定的设备个数为负数，则直接返回错误信息
//			jsonObj.put("ret_code", -5);
//			jsonObj.put("ret_message", "当前用户最多同时绑定的设备数异常");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
//		
//		if(max_device_num == 0) {	// 如果最多同时能绑定的设备个数为0，则说明该用户已被禁止绑定设备，返回相应的提示信息
//			jsonObj.put("ret_code", -6);
//			jsonObj.put("ret_message", "当前用户已被禁止绑定设备");
////			jsonObj.put("num_left", 0);
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		// // 只要当前用户没有被禁止绑定设备，他的剩余可绑定设备的个数都可以用 (max_device_num-bound_count) 这个表达式来计算得到。如果计算的结果是一个负数，则其绝对值表示的是已超出最大值可绑定的设备个数
		int num_left = max_device_num - bound_count;	// 注：计算得到剩余可绑定设备个数
		int times_left = max_bind_times - bind_times_count;	// 注：计算得到剩余可绑定次数
		jsonObj.put("num_left", num_left);
		jsonObj.put("times_left", times_left);
		jsonObj.put("num_total", max_device_num);
		jsonObj.put("times_total", max_bind_times);
		
		// 只有该用户最多同时能绑定的设备个数为正数时，该用户的设备绑定功能才是正常的
//		if(bound_count == max_device_num) {	// 如果当前用户已绑定的设备个数已达到该用户最大能同时绑定的设备个数时，说明该用户已不能再继续绑定新设备
//			jsonObj.put("ret_code", -8);
//			jsonObj.put("ret_message", "当前用户已绑定的设备数量已达最大值");
////			jsonObj.put("num_left", 0);
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
//		if(bound_count > max_device_num) {	// 如果当前用户已绑定的设备个数已超过该用户最大能同时绑定的设备个数时，则说明该用户已绑定的设备个数已出现异常
//			jsonObj.put("ret_code", -9);
//			jsonObj.put("ret_message", "当前用户已绑定的设备数量已超出最大值");
////			jsonObj.put("num_left", max_device_num-bound_count);	// 注：这里超出最大可绑定设备个数后，剩余可绑定设备个数的值为一个负整数，该负整数的绝对值即表示已超出可绑定的设备个数
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		// 下面开始属于当前用户已绑定的设备数量未超过该用户最大能同时绑定的设备个数的范围
		
//		jsonObj.put("num_left", max_device_num-bound_count);	// 当前用户剩余可绑定设备个数，从这里开始就是一个正整数了，表示他还可以继续绑定多少个设备
		
		// 开始验证绑定累计次数有没有超过最大值
		// // 
		if(bind_limit_time == 0) {	// 如果累计绑定次数有效时间为0天，则特别约定绑定累计次数终生有效（即永不重置累计绑定次数）
			if(num_left <= 0 || times_left <= 0) {	// 如果剩余可绑定设备个数或次数为负数或0时，表示当前用户绑定设备的个数或累计绑定次数已超过限制，此时返回错误提示信息
				jsonObj.put("ret_code", -8);
				jsonObj.put("ret_message", "当前用户已绑定的设备数量或累计已绑定次数已达到或超过最大值（无有效时间限制）");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			jsonObj.put("ret_code", 1);
			jsonObj.put("ret_message", "当前用户已绑定设备个数和累计已绑定次数均未超过限制（无有效时间限制）");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果累计绑定次数有效时间为一个正数，则说明有绑定次数有效时间限制，需要首先验证绑定次数是否在有效时间内。如果绑定次数已过期，则需要重置绑定次数相关信息；如果未过期，则继续验证绑定次数是否超过限制
		// 如果绑定次数有效时间大于0，则首先要验证当前已绑定次数是否已过期（即判断“绑定次数生效时间+绑定次数有效时间”(即绑定次数失效时间)是否早于当前系统时间，如果早于则说明已过期，否则说明还未过期）
		Date bind_expire_time = null;
		Date now_time = new Date();	// 取得当前系统时间
		
		if(bind_start_time == null) {	// 如果绑定次数生效时间为null，则设置绑定次数失效时间为当前时间365天（即一年）之后的时间，也就是说对于一次绑定都没有的新用户来说，他的累计绑定次数永不过期
			bind_expire_time = DateUtil.calcXDaysAfterADate(365, now_time);
		} else {
			bind_expire_time = DateUtil.calcXDaysAfterADate(bind_limit_time, bind_start_time);	// 根据绑定次数生效时间和绑定次数有效时间计算得到绑定次数过期时间
		}
		if(bind_expire_time == null) {
			jsonObj.put("ret_code", -9);
			jsonObj.put("ret_message", "计算绑定次数失效时间出现异常");
			jsonObj.put("times_left", null);	// 若此时出现异常，则剩余可绑定次数的值就是不准确的值了，所以将其值设为null
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(now_time.after(bind_expire_time)) {	// 如果当前时间已经晚于绑定次数失效时间，换句话说，就是绑定次数失效时间早于当前时间，则说明已绑定次数已过期，需要进行绑定次数相关的重置操作
			EpUser epu_bind_upd = new EpUser();
			epu_bind_upd.setId(ep_uid);
			epu_bind_upd.setBind_times_count(0);	// 将累计已绑定次数重置为0次
			epu_bind_upd.setBind_start_time(now_time);	// 将累计绑定次数开始生效时间重置为当前系统时间
			
			int bind_upd_ret = epUserService.modifyEpUserById(epu_bind_upd);
			if(bind_upd_ret <= 0) {
				jsonObj.put("ret_code", -10);
				jsonObj.put("ret_message", "重置用户已绑定设备个数或累计次数相关信息异常");
				jsonObj.put("num_left", null);	// 若此时出现异常，则剩余可绑定设备个数的值就是不准确的值了，所以将其值设为null
				jsonObj.put("times_left", null);	// 若此时出现异常，则剩余可绑定次数的值就是不准确的值了，所以将其值设为null
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 如果重置绑定次数相关信息成功，则此时该用户剩余可绑定设备个数和次数就一定是没有超过限制的，可以直接返回正常提示信息
			jsonObj.put("ret_code", 2);
			jsonObj.put("ret_message", "当前用户已绑定设备个数和累计已绑定次数均未超过限制（重置绑定次数后）");
			jsonObj.put("times_left", max_bind_times);	// 重置绑定次数后，剩余可绑定次数就等于该用户最大可绑定次数了
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果绑定次数未过期。。。
		if(num_left <= 0 || times_left <= 0) {	// 如果剩余可绑定设备个数或次数为负数或0时，表示当前用户绑定设备的个数或累计绑定次数已超过限制，此时返回错误提示信息
			jsonObj.put("ret_code", -11);
			jsonObj.put("ret_message", "当前用户已绑定的设备数量或累计已绑定次数已达到或超过最大值（有效时间内）");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		jsonObj.put("ret_code", 3);
		jsonObj.put("ret_message", "当前用户已绑定设备个数和累计已绑定次数均未超过限制（有效时间内）");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
//		return null;
	}
	
	/**
	 * 绑定设备（传入工号、UDID、设备名称、平台类型）
	 * 
	 * 可能的情况：
	 * 1. 没有该设备的相关记录（即这台设备是一台新设备）
	 * 2. 当前设备处于未绑定状态，可以进行绑定操作
	 * 3. 当前设备已由当前用户自己所绑定，可以直接进入APP
	 * 4. 当前设备已由其他用户所绑定，不能进入APP（同一设备同一时间最多只能绑定一个用户）
	 * 
	 */
	@RequestMapping(params = "bindDevice", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String bindDeviceJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");
		String udid = request.getParameter("udid");
		String device_name = request.getParameter("device_name");	// 设备名称（非必填）
		String platform = request.getParameter("platform");	// 设备平台类型（必填，目前只有Android和iOS两大平台）
		
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(udid) || StringUtils.isBlank(platform)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或UDID或平台类型参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 首先验证容易验证的平台类型参数是否正确
		if(!"Android".equalsIgnoreCase(platform) && !"iOS".equalsIgnoreCase(platform)) {	// 如果既不是Android平台，也不是iOS平台，则直接返回错误信息
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "未知的平台类型");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果设备名称为空，则设置一个占位字符串“<NONE>”
		if(StringUtils.isBlank(device_name)) {
			device_name = "<NONE>";
		}
		
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer device_id = getDeviceIdByUdid(udid);
//		if(device_id == 0) {	// 如果得到的device_id=0，则说明该设备是一台新设备，对于新设备而言，可以默认视之为未绑定的，所以可以直接返回正常提示信息
//			jsonObj.put("ret_code", 3);
//			jsonObj.put("ret_message", "当前设备是一台新设备，可以进行绑定操作");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		if(device_id == null || device_id < 0) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "根据设备唯一标识码获取设备id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 下面开始属于获取到的设备id（device_id）为0（新设备）或正数（正常值）的情况范围
		
		
		// 首先检查当前用户已绑定设备个数或累计已绑定次数是否超过该用户最大值限制
		Integer if_exceed_ret = getIfExceedLimitByEpUid(ep_uid);
		
		// 如果当前用户已绑定设备个数或累计已绑定次数达到或超过了他的最大限制值，或出现了其他异常情况时，直接返回错误提示信息
		if((if_exceed_ret == null || if_exceed_ret <= 0) && if_exceed_ret != -6 && if_exceed_ret != -10 && if_exceed_ret != -11 && if_exceed_ret != -12) {
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "当前用户已绑定设备个数或累计次数已超过限制，或出现了其他异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 下面开始属于当前用户已绑定设备个数或累计已绑定次数未超过限制的情况范围
		
		EpUser epu_info = epUserService.getEpUserById(ep_uid);	// 根据用户id获取到用户信息
		Integer bind_times_count = epu_info.getBind_times_count();	// 取得用户当前累计已绑定次数
		Date bind_start_time = epu_info.getBind_start_time();
		Integer bind_limit_time = epu_info.getBind_limit_time();
		// 当用户累计已绑定次数字段的值为null或负数时，说明累计绑定次数字段的值出现了异常，直接返回错误提示信息
		if(bind_times_count == null || bind_limit_time == null || bind_times_count < 0 || bind_limit_time < 0) {
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "当前用户累计已绑定次数或绑定次数有效时间出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(if_exceed_ret == -6 && bind_start_time != null) {
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "当前用户已绑定设备个数或累计次数已超过限制，或出现了其他异常2");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Date now_time = new Date();	// 获取系统当前时间
		
		// 如果该用户最多只能绑定一台设备，且已绑定一台设备，但绑定次数未达到最大值限制，则默认先解绑之前已绑定的设备，然后再去绑定另一台设备，并更新用户已绑定次数，做好绑定历史记录
		if(if_exceed_ret == -10 || if_exceed_ret == -11 || if_exceed_ret == -12) {
			if(device_id != 0) {	// 如果当前设备不是一台新设备，则说明是之前有人绑定过的老设备，那就得先检查该设备的状态是不是已经被绑定
				// 首先查询当前这台设备的绑定状态，看是否已绑定，只有是未绑定状态才能继续下一步
				Integer bind_status = getDeviceBoundStatusByEpUidAndDeviceId(ep_uid, device_id);
				if(bind_status == null || bind_status < 0) {
					jsonObj.put("ret_code", -10);
					jsonObj.put("ret_message", "根据用户id和设备id获取设备绑定状态异常2");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				
//			if(bind_status == 0) {	// 如果设备为“未绑定”状态，则直接返回0
//				jsonObj.put("ret_code", 0);
//				jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备目前处于未绑定状态");
//				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//			}
				
				if(bind_status == 1) {	// 如果设备为“已绑定”状态，且为传入的工号对应的用户所绑定，则返回1
					jsonObj.put("ret_code", -11);
					jsonObj.put("ret_message", "该设备已由用户自己绑定，无须再绑定2");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				
				if(bind_status == 2) {	// 如果设备为“已绑定”状态，且已由其他用户所绑定，则返回2
					jsonObj.put("ret_code", -12);
					jsonObj.put("ret_message", "该设备已由其他用户绑定，不能绑定2");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				
//				if(bind_status == -2 && device_id != 0) {
//					jsonObj.put("ret_code", -33);
//					jsonObj.put("ret_message", "设备id为负数异常");
//					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//				}
				
				// 只有是未绑定状态（即 bind_status == 0 ）时才能继续下一步。。。
			}
			
			
			// 首先解绑该用户之前已绑定的设备
			// 先查出之前已绑的设备id
			EpDevice one_epd = new EpDevice();
			one_epd.setEp_uid(ep_uid);
			one_epd.setBind_status(true);
			List<EpDevice> ret_epds = epDeviceService.getEpDeviceByCriterion(one_epd);
			if(ret_epds == null || ret_epds.size() == 0 ) {
				jsonObj.put("ret_code", -17);
				jsonObj.put("ret_message", "查询当前用户已绑定设备数据出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 查出已绑定的设备（即ret_epds.size() >= 1 时），这时才取出已绑定的设备（一台或多台），即为将要默认进行解绑的设备
			
			JSONArray unbound_device_array = new JSONArray();	// 用来存放默认被系统解绑了的设备详细信息Json数据的Json数组
			
			// 开始解绑找到的之前已绑定的设备。。。
			for(EpDevice epd_iter:ret_epds) {
				Integer epd_iter_id = epd_iter.getId();
				EpDevice upd_one_epd = new EpDevice();
				upd_one_epd.setId(epd_iter_id);
				upd_one_epd.setBind_status(false);
				upd_one_epd.setLast_unbound_time(now_time);
				int upd_res = epDeviceService.modifyEpDeviceById(upd_one_epd);
				if(upd_res <= 0) {
					jsonObj.put("ret_code", -18);
					jsonObj.put("ret_message", "解绑当前用户已绑定设备出现异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				// 解绑用户已绑定设备成功后。。。
				
				// 在返回结果中加入默认被系统解绑的那台设备的信息：
				JSONObject unbound_device_json = new JSONObject();
				unbound_device_json.put("name", epd_iter.getName());	// 单设备已绑定用户默认被解绑的设备名称
				unbound_device_json.put("last_bind_time", DateUtil.formatDate(2, epd_iter.getLast_bound_time()));	// 默认被解绑的设备上一次绑定该设备的时间（格式化成“yyyy-MM-dd HH:mm:ss”日期格式）
				unbound_device_json.put("platform", epd_iter.getPlatform());	// 单设备已绑定用户默认被解绑的设备平台类型
				unbound_device_array.add(unbound_device_json);
				
				// 做好解绑设备的历史记录
				BindHistory bh_one = new BindHistory();
				bh_one.setEp_uid(ep_uid);
				bh_one.setDevice_id(epd_iter_id);
				bh_one.setType("unbind");
				bh_one.setTime(now_time);
				Integer[] res_rounds = calcTotalRoundsSameAndDiffDeviceValid(ep_uid, epd_iter_id, "unbind", bind_start_time, bind_limit_time);
				if(res_rounds == null || res_rounds[0] < 0) {
					jsonObj.put("ret_code", -19);
					jsonObj.put("ret_message", "计算历史解绑历史记录总次数时出现异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				bh_one.setWhich_round_same(res_rounds[0]);
				bh_one.setWhich_round_diff(res_rounds[1]);
				int ret_add_bh_one = bindHistoryService.addBindHistory(bh_one);
				if(ret_add_bh_one <= 0) {
					jsonObj.put("ret_code", -20);
					jsonObj.put("ret_message", "添加一条解绑设备历史记录时出现异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				// 添加一条解绑设备历史记录信息成功。。。
				
				/*
				// 在返回结果中加入默认被系统解绑的那台设备的信息：
				JSONObject ubound_device_json = new JSONObject();
				ubound_device_json.put("name", ret_one_epd.getName());	// 单设备已绑定用户默认被解绑的设备名称
				ubound_device_json.put("last_bind_time", DateUtil.formatDate(2, ret_one_epd.getLast_bound_time()));	// 默认被解绑的设备上一次绑定该设备的时间（格式化成“yyyy-MM-dd HH:mm:ss”日期格式）
				ubound_device_json.put("platform", ret_one_epd.getPlatform());	// 单设备已绑定用户默认被解绑的设备平台类型
				
				jsonObj.put("unbound_device", ubound_device_json);
				
				// 做好解绑设备的历史记录
				BindHistory bh_one = new BindHistory();
				bh_one.setEp_uid(ep_uid);
				bh_one.setDevice_id(one_epd_id);
				bh_one.setType("unbind");
				bh_one.setTime(now_time);
				Integer[] res_rounds = calcTotalRoundsSameAndDiffDeviceValid(ep_uid, one_epd_id, "unbind", bind_start_time, bind_limit_time);
				if(res_rounds == null || res_rounds[0] < 0) {
					jsonObj.put("ret_code", -19);
					jsonObj.put("ret_message", "计算历史解绑历史记录总次数时出现异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				bh_one.setWhich_round_same(res_rounds[0]);
				bh_one.setWhich_round_diff(res_rounds[1]);
				int ret_add_bh_one = bindHistoryService.addBindHistory(bh_one);
				if(ret_add_bh_one <= 0) {
					jsonObj.put("ret_code", -20);
					jsonObj.put("ret_message", "添加一条解绑设备历史记录时出现异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				// 添加一条解绑设备历史记录信息成功。。。
				*/
			}
			/*
			EpDevice ret_one_epd = ret_epds.get(0);
			Integer one_epd_id = ret_one_epd.getId();
			EpDevice upd_one_epd = new EpDevice();
			upd_one_epd.setId(one_epd_id);
			upd_one_epd.setBind_status(false);
			upd_one_epd.setLast_unbound_time(now_time);
			int upd_res = epDeviceService.modifyEpDeviceById(upd_one_epd);
			if(upd_res <= 0) {
				jsonObj.put("ret_code", -18);
				jsonObj.put("ret_message", "解绑当前用户唯一已绑定设备出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			*/
			jsonObj.put("unbound_device_list", unbound_device_array);
			// 解绑用户已绑定设备成功后。。。
			
			// 然后再去绑定当前设备
			
			Integer did_hist = null;	// 用于存放后面将要添加进绑定历史记录信息的设备id
			if(device_id == 0) {	// 如果当前设备是一台新设备（之前从未有人绑定过的设备，也即是在设备绑定信息表中没有相关记录的设备）
				// 此时需要添加一条新的设备绑定信息
				EpDevice new_dev = new EpDevice();
				new_dev.setEp_uid(ep_uid);
				new_dev.setName(device_name);
				new_dev.setUdid(udid);
				new_dev.setBind_status(true);
				new_dev.setPlatform(platform);
				new_dev.setLast_bound_time(now_time);
				int ret_new_dev = epDeviceService.addEpDevice(new_dev);
				if(ret_new_dev <= 0) {
					jsonObj.put("ret_code", -21);
					jsonObj.put("ret_message", "添加一条新设备绑定记录时出现异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				// 添加一条设备绑定记录成功。。。
				// 得到新添加的设备的id
				did_hist = new_dev.getId();
			} else {	// 如果当前设备是一台老设备（也即是之前有过绑定历史记录的设备），则只需要更新之前已有的绑定设备信息
				// 首先检查这台老设备的状态是不是“未绑定”的
				Integer ret_bind_status = getDeviceBoundStatusByEpUidAndDeviceId(ep_uid, device_id);
				if(ret_bind_status == null || ret_bind_status < 0) {
					jsonObj.put("ret_code", -26);
					jsonObj.put("ret_message", "根据用户id和设备id获取设备绑定状态异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				
//				if(ret_bind_status == 0) {	// 如果设备为“未绑定”状态，则直接返回0
//					jsonObj.put("ret_code", 0);
//					jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备目前处于未绑定状态");
//					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//				}
				
				if(ret_bind_status == 1) {	// 如果设备为“已绑定”状态，且为传入的工号对应的用户所绑定，则返回1
					jsonObj.put("ret_code", -27);
					jsonObj.put("ret_message", "该设备已由用户自己绑定，无须再绑定");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				
				if(ret_bind_status == 2) {	// 如果设备为“已绑定”状态，且已由其他用户所绑定，则返回2
					jsonObj.put("ret_code", -28);
					jsonObj.put("ret_message", "该设备已由其他用户绑定，不能绑定");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				
				if(ret_bind_status < 0) {
					jsonObj.put("ret_code", -29);
					jsonObj.put("ret_message", "未知错误异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				
				// 只有 ret_bind_status == 0 时，才表示当前设备是一台“未绑定”的老设备，才可以进行下面的绑定操作
				
				EpDevice upd_dev = new EpDevice();
				upd_dev.setId(device_id);
				upd_dev.setName(device_name);
				upd_dev.setEp_uid(ep_uid);
				upd_dev.setBind_status(true);
				upd_dev.setPlatform(platform);
				upd_dev.setLast_bound_time(now_time);
				int ret_upd_dev = epDeviceService.modifyEpDeviceById(upd_dev);
				if(ret_upd_dev <= 0) {
					jsonObj.put("ret_code", -22);
					jsonObj.put("ret_message", "更新一条老设备绑定记录时出现异常");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				// 更新一条老设备绑定信息成功。。。
				
				did_hist = device_id;
			}
			
			// 更新用户的已绑定次数（加1）
			Date the_start_date = bind_start_time;	// 存放后面准备用于计算历史累计是第几次绑定或解绑的有效时间的开始时间
			EpUser upd_epu = new EpUser();
			upd_epu.setId(ep_uid);
			upd_epu.setBind_times_count(bind_times_count+1);
			if(bind_start_time == null) {	// 如果绑定次数生效时间为null，说明该用户的绑定次数已过有效期，且还没绑定下一台设备，或者是新用户还没有绑定过设备，
				upd_epu.setBind_start_time(now_time);	// 那么这时在绑定设备的时候就把绑定次数生效时间设置为这一次绑定设备的时间，即从这一次绑定设备开始计算绑定次数生效时间
				upd_epu.setBind_times_count(1);
				the_start_date = now_time;
			}
			int ret_upd_epu = epUserService.modifyEpUserById(upd_epu);
			if(ret_upd_epu <= 0) {
				jsonObj.put("ret_code", -23);
				jsonObj.put("ret_message", "更新用户已绑定次数时出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 更新用户已绑定次数成功。。。
			
			// 做好绑定设备的历史记录
			BindHistory bh_two = new BindHistory();
			bh_two.setEp_uid(ep_uid);
			bh_two.setDevice_id(did_hist);
			bh_two.setType("bind");
			bh_two.setTime(now_time);
			Integer[] calc_result = calcTotalRoundsSameAndDiffDeviceValid(ep_uid, did_hist, "bind", the_start_date, bind_limit_time);
			if(calc_result == null || calc_result[0] < 0) {
				jsonObj.put("ret_code", -24);
				jsonObj.put("ret_message", "计算历史绑定历史记录总次数时出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 计算历史绑定历史记录总次数成功。。。
			bh_two.setWhich_round_same(calc_result[0]);
			bh_two.setWhich_round_diff(calc_result[1]);
			int add_bh_two_ret = bindHistoryService.addBindHistory(bh_two);
			if(add_bh_two_ret <= 0) {
				jsonObj.put("ret_code", -25);
				jsonObj.put("ret_message", "添加一条绑定设备历史记录时出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			// 添加一条绑定设备历史记录信息成功。。。
			jsonObj.put("ret_code", 3);
			jsonObj.put("ret_message", "解绑单设备用户已绑定设备后，绑定设备成功");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		
		// 如果device_id=0，即当前设备是一台新设备
		if(device_id == 0) {
			// 开始新增一条绑定设备信息记录
			EpDevice epd_new = new EpDevice();
			epd_new.setName(device_name);
			epd_new.setUdid(udid);
			epd_new.setEp_uid(ep_uid);
			epd_new.setBind_status(true);
			epd_new.setPlatform(platform);
			epd_new.setLast_bound_time(now_time);
			int new_bind_ret = epDeviceService.addEpDevice(epd_new);
			if(new_bind_ret <= 0) {
				jsonObj.put("ret_code", -7);
				jsonObj.put("ret_message", "新增一条新设备的绑定信息失败");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			// 如果新增一条新设备的绑定信息记录成功，然后开始更新当前用户的累计已绑定次数信息（已绑定次数要增加1）
			EpUser epu_upd = new EpUser();
			epu_upd.setId(ep_uid);
			epu_upd.setBind_times_count(bind_times_count+1);
			if(bind_start_time == null) {	// 如果绑定次数生效时间为null，说明该用户的绑定次数已过有效期，且还没绑定下一台设备，或者是新用户还没有绑定过设备，
				epu_upd.setBind_start_time(now_time);	// 那么这时在绑定设备的时候就把绑定次数生效时间设置为这一次绑定设备的时间，即从这一次绑定设备开始计算绑定次数生效时间
			}
			int mod_ret = epUserService.modifyEpUserById(epu_upd);
			if(mod_ret <= 0) {
				jsonObj.put("ret_code", -8);
				jsonObj.put("ret_message", "更新用户累计已绑定次数信息失败（绑定新设备）");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
//			calcTotalRoundsSameAndDiffDeviceValid(ep_uid, did, type, bind_start_time, range);
			
			// 如果更新用户累计已绑定次数信息成功，然后开始新增一条绑定历史信息记录
			BindHistory bh_new = new BindHistory();
			bh_new.setEp_uid(ep_uid);
			bh_new.setDevice_id(epd_new.getId());
			bh_new.setType("bind");
			bh_new.setTime(now_time);
			bh_new.setWhich_round_same(1);	// 对于绑定新设备，一定是第一次绑定
			bh_new.setWhich_round_diff(1);
			int ret_add_bh = bindHistoryService.addBindHistory(bh_new);
			if(ret_add_bh <= 0) {
				jsonObj.put("ret_code", -9);
				jsonObj.put("ret_message", "新增一条绑定历史记录信息失败（绑定新设备）");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			jsonObj.put("ret_code", 1);
			jsonObj.put("ret_message", "绑定新设备成功");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 如果device_id>0，即当前设备是一台之前有过绑定历史的旧设备
		
		Integer ret_bind_status = getDeviceBoundStatusByEpUidAndDeviceId(ep_uid, device_id);
		if(ret_bind_status == null || ret_bind_status < 0) {
			jsonObj.put("ret_code", -10);
			jsonObj.put("ret_message", "根据用户id和设备id获取设备绑定状态异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		if(ret_bind_status == 0) {	// 如果设备为“未绑定”状态，则直接返回0
//			jsonObj.put("ret_code", 0);
//			jsonObj.put("ret_message", "获取设备绑定状态成功，且该设备目前处于未绑定状态");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		if(ret_bind_status == 1) {	// 如果设备为“已绑定”状态，且为传入的工号对应的用户所绑定，则返回1
			jsonObj.put("ret_code", -11);
			jsonObj.put("ret_message", "该设备已由用户自己绑定，无须再绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 2) {	// 如果设备为“已绑定”状态，且已由其他用户所绑定，则返回2
			jsonObj.put("ret_code", -12);
			jsonObj.put("ret_message", "该设备已由其他用户绑定，不能绑定");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == 0) {	// 只有设备为“未绑定”状态时，才可以进行绑定操作
			EpDevice epd_old = epDeviceService.getEpDeviceById(device_id);	// 先取出已有的设备绑定信息
			// 开始更新绑定设备信息记录
			epd_old.setName(device_name);
			epd_old.setUdid(udid);
			epd_old.setEp_uid(ep_uid);
			epd_old.setBind_status(true);
			epd_old.setPlatform(platform);
			epd_old.setLast_bound_time(now_time);
			int old_bind_ret = epDeviceService.modifyEpDeviceById(epd_old);
			if(old_bind_ret <= 0) {
				jsonObj.put("ret_code", -13);
				jsonObj.put("ret_message", "更新已有设备绑定信息失败");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			// // 如果更新旧设备的绑定信息记录成功，然后开始更新当前用户的累计已绑定次数信息（已绑定次数要增加1）
			
			// 首先获取到该用户最后一次操作历史记录信息
			Date start_date = null;
			if(bind_start_time == null) {
				start_date = now_time;
			} else {
				start_date = bind_start_time;
			}
			BindHistory last_bh = bindHistoryService.getLastBindHistoryValidByEpUid(ep_uid, start_date, bind_limit_time);
			boolean count_flag = false;	// 表示是否要对已绑定次数进行加1累加的标志
			if(last_bh != null) {
				Integer last_bh_did = last_bh.getDevice_id();
				if(last_bh_did != null && last_bh_did != device_id) {
					count_flag = true;	// 只要该用户在有效时间内最后一条操作历史记录信息不是当前设备，就说明他在解绑了当前设备后，又去绑定了另外一台设备，这样就要对绑定次数进行累加；
										// 否则，说明他只是一直在绑定/解绑当前设备，这些操作都可以不计入绑定次数累计次数中去。
				}
			}
			
			EpUser epu_upd = new EpUser();
			epu_upd.setId(ep_uid);
			if(bind_start_time == null) {	// 如果绑定次数生效时间为null，说明该用户的绑定次数已过有效期，且还没绑定下一台设备，或者是新用户还没有绑定过设备，
				epu_upd.setBind_start_time(now_time);	// 那么这时在绑定设备的时候就把绑定次数生效时间设置为这一次绑定设备的时间，即从这一次绑定设备开始计算绑定次数生效时间
			}
			if(count_flag) {	// 只有累计绑定次数增加了才需要更新用户已绑定次数字段的信息
				epu_upd.setBind_times_count(bind_times_count+1);
				int mod_ret = epUserService.modifyEpUserById(epu_upd);
				if(mod_ret <= 0) {
					jsonObj.put("ret_code", -14);
					jsonObj.put("ret_message", "更新当前用户累计已绑定次数信息失败（绑定旧设备）");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
			}
			
			// // 如果更新用户累计已绑定次数信息成功，然后开始新增一条绑定历史信息记录
			
			// 首先计算得到该用户在有效时间内，对于同一设备和不同设备分别而言是第几次绑定的次数信息
			Integer[] res_rounds = calcTotalRoundsSameAndDiffDeviceValid(ep_uid, device_id, "bind", start_date, bind_limit_time);
			if(res_rounds == null || res_rounds[0] < 0) {
				jsonObj.put("ret_code", -15);
				jsonObj.put("ret_message", "计算当前用户历史绑定次数信息出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			BindHistory bh_new = new BindHistory();
			bh_new.setEp_uid(ep_uid);
			bh_new.setDevice_id(device_id);
			bh_new.setType("bind");
			bh_new.setTime(now_time);
			bh_new.setWhich_round_same(res_rounds[0]);	// 对于绑定旧设备，
			bh_new.setWhich_round_diff(res_rounds[1]);
			int ret_add_bh = bindHistoryService.addBindHistory(bh_new);
			if(ret_add_bh <= 0) {
				jsonObj.put("ret_code", -16);
				jsonObj.put("ret_message", "新增一条绑定历史记录信息失败（绑定旧设备）");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			jsonObj.put("ret_code", 2);
			jsonObj.put("ret_message", "绑定旧设备成功");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		jsonObj.put("ret_code", -17);
		jsonObj.put("ret_message", "未知错误或异常");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
//		return null;
	}
	
	
	/*
	 * 用户解绑设备（传入工号和设备UDID）
	 */
	/**
	 * 用户解绑设备（传入工号和设备UDID）
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "unbindDevice", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String unbindDeviceJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
		String work_no = request.getParameter("work_no");
		String udid = request.getParameter("udid");
		
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(udid)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或设备唯一标识码参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int ep_uid = EpAttenController.getEpUidByWorkNo(work_no);
		if(ep_uid <= 0) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer device_id = getDeviceIdByUdid(udid);
		if(device_id == 0) {	// 如果得到的device_id=0，则说明该设备是一台新设备，此时不能进行解绑操作，直接返回错误提示信息
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "当前设备是一台新设备，不能进行解绑操作");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		if(device_id == null || device_id < 0) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "根据设备唯一标识码获取设备id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 下面开始属于获取到的设备id（device_id）为正数（正常值）的情况范围
		
		// 首先根据用户id和设备id查询该设备的状态是否绑定、由谁所绑定，然后分情况分别进行处理。。。
		Integer ret_bind_status = getDeviceBoundStatusByEpUidAndDeviceId(ep_uid, device_id);
		if((ret_bind_status == null || ret_bind_status < 0) && ret_bind_status != -3) {	// ret_bind_status = -3 表示该设备的绑定状态的值为null，这种情况需要单独处理，默认认为null等价于false，即表示未绑定状态
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "根据用户id和设备id获取设备绑定状态异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(ret_bind_status == -3 || ret_bind_status == 0) {	// 如果设备绑定状态字段的值为null（默认也视为未绑定状态）或“未绑定”状态，则不能进行解绑操作，直接返回错误提示信息
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "该设备目前处于未绑定状态，不能进行解绑操作");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		if(ret_bind_status == 1) {	// 如果设备为“已绑定”状态，且为传入的工号对应的用户所绑定，则直接返回-6错误信息
//			jsonObj.put("ret_code", -7);
//			jsonObj.put("ret_message", "该设备已由用户自己绑定");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		if(ret_bind_status == 2) {	// 如果设备为“已绑定”状态，且已由其他用户所绑定，则直接返回-7错误信息
			jsonObj.put("ret_code", -7);
			jsonObj.put("ret_message", "该设备已由其他用户绑定，不能进行解绑操作");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 如果 ret_bind_status == 1，则说明该设备已由当前用户自己所绑定，可以进行解绑操作
		
		// 先取得当前系统时间
		Date now_date = new Date();
		// 更新设备绑定状态和上次解绑时间信息
		EpDevice unbind_epd = new EpDevice();
		unbind_epd.setId(device_id);
		unbind_epd.setBind_status(false);
		unbind_epd.setLast_unbound_time(now_date);	// 设置设备最后一次解绑时间为当前系统时间
		
		int unbind_ret = epDeviceService.modifyEpDeviceById(unbind_epd);
		if(unbind_ret <= 0) {
			jsonObj.put("ret_code", -8);
			jsonObj.put("ret_message", "解绑定设备操作失败");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // 如果更新设备绑定状态和上次解绑时间信息成功，则可以开始添加一条操作历史记录信息
		
		EpUser epu_info = epUserService.getEpUserById(ep_uid);	// 根据用户id获取到用户信息
//		Integer bind_times_count = epu_info.getBind_times_count();	// 取得用户当前累计已绑定次数
		Date bind_start_time = epu_info.getBind_start_time();
		Integer bind_limit_time = epu_info.getBind_limit_time();
		// 当用户绑定次数有效时间字段的值为null或负数时，说明绑定次数有效时间字段的值出现了异常，直接返回错误提示信息
		if(bind_limit_time == null || bind_limit_time < 0) {
			jsonObj.put("ret_code", -9);
			jsonObj.put("ret_message", "当前用户绑定次数有效时间数据异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(bind_start_time == null) {
			jsonObj.put("ret_code", -10);
			jsonObj.put("ret_message", "当前用户绑定次数生效时间数据异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 计算得到该用户在有效时间内，对于同一设备和不同设备分别而言是第几次解绑的次数信息
		// 获得绑定次数生效的时间（可能会因为绑定次数有效期已过而被重置）
		Date binding_start = getStartDateOfBindingStart(ep_uid, bind_start_time, bind_limit_time, now_date);
		
		if(binding_start == null) {
			jsonObj.put("ret_code", -11);
			jsonObj.put("ret_message", "获得或重置当前用户绑定次数生效的时间异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Integer[] res_rounds = calcTotalRoundsSameAndDiffDeviceValid(ep_uid, device_id, "unbind", binding_start, bind_limit_time);
		if(res_rounds == null || res_rounds[0] < 0) {
			jsonObj.put("ret_code", -12);
			jsonObj.put("ret_message", "计算当前用户历史解绑次数信息出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		BindHistory bh_add = new BindHistory();
		bh_add.setEp_uid(ep_uid);
		bh_add.setDevice_id(device_id);
		bh_add.setType("unbind");
		bh_add.setTime(now_date);
		bh_add.setWhich_round_same(res_rounds[0]);
		bh_add.setWhich_round_diff(res_rounds[1]);
		int ret_add_bh = bindHistoryService.addBindHistory(bh_add);
		if(ret_add_bh <= 0) {
			jsonObj.put("ret_code", -13);
			jsonObj.put("ret_message", "新添加一条解绑历史记录信息时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// unbind_ret>0时，解绑定设备成功，返回正常提示信息
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "解绑定设备成功");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		
//		return null;
	}
	
	
	/**
	 * 获得绑定次数生效的时间
	 * 
	 * @param ep_uid
	 * @param orign_start
	 * @param limit_time
	 * @param now_time
	 * @return
	 */
	private Date getStartDateOfBindingStart(int ep_uid, Date orign_start, int limit_time, Date now_time) {
		if(ep_uid <= 0 || limit_time < 0 || orign_start == null || now_time == null) {
			return null;
		}
		
		Date expire_time = DateUtil.calcXDaysAfterADate(limit_time, orign_start);	// 计算得到绑定次数失效时间
		if(expire_time == null) {
			return null;
		}
		
		if(expire_time.after(now_time)) {	// 如果绑定次数未过期，则绑定次数生效时间就是原时间orign_start
			return orign_start;
		}
		
		// 如果绑定次数已过期，则用当前时间作为绑定次数生效时间，并更新用户的绑定次数生效时间字段的信息
		EpUser epu_upd = new EpUser();
		epu_upd.setId(ep_uid);
		epu_upd.setBind_times_count(0);	// 这里可以看出，如果某个用户有绑定次数生效时间，但累计已绑定次数为0的时候，说明他的绑定次数已过期，并且已在解绑设备过程中更新了绑定次数生效时间
		epu_upd.setBind_start_time(now_time);
		int ret_upd = epUserService.modifyEpUserById(epu_upd);
		if(ret_upd <= 0) {
			return null;
		}
		
		return now_time;
		
//		return null;
	}
	
	
	/**
	 * 根据用户id、设备id查询并计算得到该用户“在有效时间内，同一设备已绑定/解绑了几次”
	 * 注：同一设备连续绑定/解绑的，只算一次
	 * 
	 * @param uid 用户id
	 * @param did 设备id
	 * @param type 操作类型：绑定（"bind"）或解绑（"unbind"）
	 * @return 总次数
	 */
	private Integer calcTotalRoundsSameDevice(int uid, int did, String type) {
		if(uid <= 0 || did <= 0 || StringUtils.isBlank(type)) {	// 如果用户id、设备id为负数或0，或者操作类型参数为空白字符串，则直接返回null
			return null;
		}
		
		if(!"bind".equalsIgnoreCase(type) && !"unbind".equalsIgnoreCase(type)) {	// 如果操作类型不是"bind"或"unbind"，则直接返回-1
			return -1;
		}
		
		BindHistory bh_query = new BindHistory();
		bh_query.setEp_uid(uid);
//		bh_query.setDevice_id(did);
		bh_query.setType(type);
		List<BindHistory> ret_bhs = bindHistoryService.getBindHistoryByCriterionOrderByTime(bh_query);
		if(ret_bhs == null) {
			return -2;
		}
		
		int bhs_count = ret_bhs.size();
		if(bhs_count == 0) {	// 如果该用户没有历史操作记录信息，直接返回0
			return -3;
		}
		
		// 如果该用户至少有一条操作记录信息，则可以开始计算同一设备已绑定/解绑的次数
		Integer total_rounds = 0;	// 用于计数的变量，初值设为0
		
//		if(bhs_count == 1) {	// 如果该用户的操作历史记录信息只有一条
//			BindHistory bh_one = ret_bhs.get(0);
//			Integer device_id = bh_one.getDevice_id();
//			if(device_id == null) {
//				return -4;
//			}
//			if(device_id == did) {
//				return 1;
//			}
//			return 0;
//		}
		
		// 如果该用户的操作历史记录信息至少有2条（查询到的操作历史记录信息中，已经排除了设备id为null的数据，对应SQL语句中“and device_id is not null”的筛选条件）
		Integer last_did = null;
		for(int i=0; i<bhs_count; i++) {
			BindHistory bhim1 = null;
			if(i>0) {
				bhim1 = ret_bhs.get(i-1);
				last_did = bhim1.getDevice_id();	// 上一条（即当前遍历到的第i条）操作历史记录信息中的设备id
			}
			BindHistory bhi = ret_bhs.get(i);
			Integer bhi_did = bhi.getDevice_id();	// 当前遍历到的第(i+1)条操作历史记录信息中的设备id
//			if(bhi_did == null) {
//				continue;	// 跳过设备id是null的操作历史记录信息
//			}
			if(bhi_did == did) {
				if(i == 0 && last_did == null) {
					total_rounds += 1;
					continue;
				}
				if(last_did == did) {
					continue;
				}
				total_rounds += 1;
			}
		}
//		for(BindHistory bh:ret_bhs) {
//			
//		}
		
		return total_rounds;
	}
	
	/**
	 * 根据用户id查询并计算得到该用户“在有效时间内，不同设备已绑定/解绑了几次”
	 * 注：同一设备连续绑定/解绑的，只算一次
	 * 
	 * @param uid 用户id
	 * @param type 操作类型：绑定（"bind"）或解绑（"unbind"）
	 * @return 总次数
	 */
	private Integer calcTotalRoundsDiffDevice(int uid, String type) {
		if(uid <= 0 || StringUtils.isBlank(type)) {	// 如果用户id为负数或0，或者操作类型参数为空白字符串，则直接返回null
			return null;
		}
		
		if(!"bind".equalsIgnoreCase(type) && !"unbind".equalsIgnoreCase(type)) {	// 如果操作类型不是"bind"或"unbind"，则直接返回-1
			return -1;
		}
		
		BindHistory bh_query = new BindHistory();
		bh_query.setEp_uid(uid);
//		bh_query.setDevice_id(did);
		bh_query.setType(type);
		List<BindHistory> ret_bhs = bindHistoryService.getBindHistoryByCriterionOrderByTime(bh_query);
		if(ret_bhs == null) {
			return -2;
		}
		
		int bhs_count = ret_bhs.size();
		if(bhs_count == 0) {	// 如果该用户没有历史操作记录信息，直接返回0
			return -3;
		}
		
		// 如果该用户至少有一条操作记录信息，则可以开始计算同一设备已绑定/解绑的次数
		Integer total_rounds = 0;	// 用于计数的变量，初值设为0
		
		Integer last_did = null;
		for(int i=0; i<bhs_count; i++) {
			BindHistory bhim1 = null;
			if(i>0) {
				bhim1 = ret_bhs.get(i-1);
				last_did = bhim1.getDevice_id();	// 上一条（即当前遍历到的第i条）操作历史记录信息中的设备id
			}
			BindHistory bhi = ret_bhs.get(i);
			Integer bhi_did = bhi.getDevice_id();	// 当前遍历到的第(i+1)条操作历史记录信息中的设备id
//			if(bhi_did == null) {
//				continue;	// 跳过设备id是null的操作历史记录信息
//			}
			
//			if(last_did == null) {
//				total_rounds += 1;
//			} else if(last_did != bhi_did) {
//				total_rounds += 1;
//			}
			
			if(last_did == null || last_did != bhi_did) {
				total_rounds += 1;
			}
			
//			if(bhi_did == did) {
//				if(i == 0 && last_did == null) {
//					total_rounds += 1;
//					continue;
//				}
//				if(last_did == did) {
//					continue;
//				}
//				total_rounds += 1;
//			}
		}
		
		return total_rounds;
	}
	
	
	/**
	 * 根据用户id、设备id查询并计算得到该用户“在有效时间内，同一设备已绑定/解绑了几次”以及“在有效时间内，不同设备已绑定/解绑了几次”
	 * 注：同一设备连续绑定/解绑的，只算一次
	 * 
	 * @param uid 用户id
	 * @param did 设备id
	 * @param type 操作类型：绑定（"bind"）或解绑（"unbind"）
	 * @return 总次数的二元数组
	 */
	private Integer[] calcTotalRoundsSameAndDiffDevice(int uid, int did, String type) {
		if(uid <= 0 || did <= 0 || StringUtils.isBlank(type)) {	// 如果用户id、设备id为负数或0，或者操作类型参数为空白字符串，则直接返回null
			return null;
		}
		
		if(!"bind".equalsIgnoreCase(type) && !"unbind".equalsIgnoreCase(type)) {	// 如果操作类型不是"bind"或"unbind"，则直接返回-1
			return new Integer[]{-1, -1};
		}
		
		BindHistory bh_query = new BindHistory();
		bh_query.setEp_uid(uid);
//		bh_query.setDevice_id(did);
		bh_query.setType(type);
		List<BindHistory> ret_bhs = bindHistoryService.getBindHistoryByCriterionOrderByTime(bh_query);
		if(ret_bhs == null) {
			return new Integer[]{-2, -2};
		}
		
		int bhs_count = ret_bhs.size();
		if(bhs_count == 0) {	// 如果该用户没有历史操作记录信息，直接返回0
			return new Integer[]{-3, -3};
		}
		
		// 如果该用户至少有一条操作记录信息，则可以开始计算同一设备已绑定/解绑的次数
		Integer total_rounds_same = 0;	// 用于计数的变量，初值设为0（同一设备）
		Integer total_rounds_diff = 0;	// 用于计数的变量，初值设为0（不同设备）
		
//		if(bhs_count == 1) {	// 如果该用户的操作历史记录信息只有一条
//			BindHistory bh_one = ret_bhs.get(0);
//			Integer device_id = bh_one.getDevice_id();
//			if(device_id == null) {
//				return -4;
//			}
//			if(device_id == did) {
//				return 1;
//			}
//			return 0;
//		}
		
		// 如果该用户的操作历史记录信息至少有2条（查询到的操作历史记录信息中，已经排除了设备id为null的数据，对应SQL语句中“and device_id is not null”的筛选条件）
		Integer last_did = null;
		for(int i=0; i<bhs_count; i++) {
			BindHistory bhim1 = null;
			if(i>0) {
				bhim1 = ret_bhs.get(i-1);
				last_did = bhim1.getDevice_id();	// 上一条（即当前遍历到的第i条）操作历史记录信息中的设备id
			}
			BindHistory bhi = ret_bhs.get(i);
			Integer bhi_did = bhi.getDevice_id();	// 当前遍历到的第(i+1)条操作历史记录信息中的设备id
//			if(bhi_did == null) {
//				continue;	// 跳过设备id是null的操作历史记录信息
//			}
			
			if(last_did == null || last_did != bhi_did) {
				total_rounds_diff += 1;
			}
			
			if(bhi_did == did) {
				if(i == 0 && last_did == null) {
					total_rounds_same += 1;
					continue;
				}
				if(last_did == did) {
					continue;
				}
				total_rounds_same += 1;
			}
		}
//		for(BindHistory bh:ret_bhs) {
//			
//		}
		
		return new Integer[]{total_rounds_same, total_rounds_diff};
	}
	
	/**
	 * 根据用户id、设备id查询并计算得到该用户“在有效时间内，同一设备已绑定/解绑了几次”以及“在有效时间内，不同设备已绑定/解绑了几次”
	 * 注：同一设备连续绑定/解绑的，只算一次
	 * 
	 * @param uid 用户id
	 * @param did 设备id
	 * @param type 操作类型：绑定（"bind"）或解绑（"unbind"）
	 * @param start 绑定次数生效的开始时间
	 * @param range 绑定次数有效时长（天数）
	 * @return 总次数的二元数组
	 */
	private Integer[] calcTotalRoundsSameAndDiffDeviceValid(int uid, int did, String type, Date start, int range) {
		if(uid <= 0 || did <= 0 || range < 0 || start == null || StringUtils.isBlank(type)) {	// 如果用户id、设备id为负数或0，或者操作类型参数为空白字符串，则直接返回null
			return null;
		}
		
		if(!"bind".equalsIgnoreCase(type) && !"unbind".equalsIgnoreCase(type)) {	// 如果操作类型不是"bind"或"unbind"，则直接返回-1
			return new Integer[]{-1, -1};
		}
		
		BindHistory bh_query = new BindHistory();
		bh_query.setEp_uid(uid);
//		bh_query.setDevice_id(did);
		bh_query.setType(type);
		List<BindHistory> ret_bhs = bindHistoryService.getBindHistoryByCriterionValidOrderByTime(bh_query, start, range);
		if(ret_bhs == null) {
			return new Integer[]{-2, -2};
		}
		
		int bhs_count = ret_bhs.size();
		if(bhs_count == 0) {	// 如果该用户没有历史操作记录信息，直接返回0
			return new Integer[]{1, 1};
		}
		
		// 如果该用户至少有一条操作记录信息，则可以开始计算同一设备已绑定/解绑的次数
		Integer total_rounds_same = 0;	// 用于计数的变量，初值设为0（同一设备）
		Integer total_rounds_diff = 0;	// 用于计数的变量，初值设为0（不同设备）
		
//		if(bhs_count == 1) {	// 如果该用户的操作历史记录信息只有一条
//			BindHistory bh_one = ret_bhs.get(0);
//			Integer device_id = bh_one.getDevice_id();
//			if(device_id == null) {
//				return -4;
//			}
//			if(device_id == did) {
//				return 1;
//			}
//			return 0;
//		}
		
		// 如果该用户的操作历史记录信息至少有2条（查询到的操作历史记录信息中，已经排除了设备id为null的数据，对应SQL语句中“and device_id is not null”的筛选条件）
		Integer last_did = null;
		for(int i=0; i<bhs_count; i++) {
			BindHistory bhim1 = null;
			if(i>0) {
				bhim1 = ret_bhs.get(i-1);
				last_did = bhim1.getDevice_id();	// 上一条（即当前遍历到的第i条）操作历史记录信息中的设备id
			}
			BindHistory bhi = ret_bhs.get(i);
			Integer bhi_did = bhi.getDevice_id();	// 当前遍历到的第(i+1)条操作历史记录信息中的设备id
//			if(bhi_did == null) {
//				continue;	// 跳过设备id是null的操作历史记录信息
//			}
			
			if(last_did == null || last_did != bhi_did) {
				total_rounds_diff += 1;
			}
			
			if(bhi_did == did) {
				if(i == 0 && last_did == null) {
					total_rounds_same += 1;
					continue;
				}
				if(last_did == did) {
					continue;
				}
				total_rounds_same += 1;
			}
		}
		
		BindHistory the_final_bh = ret_bhs.get(bhs_count-1);	// 取出最后一条操作历史记录
		Integer final_bh_did = the_final_bh.getDevice_id();	// 取出最后一条操作历史记录的设备id
		if(final_bh_did == did) {
			return new Integer[]{total_rounds_same, total_rounds_diff};
		}
		
		return new Integer[]{total_rounds_same+1, total_rounds_diff+1};
	}
	
	
	public static void main(String[] args) {
		
		List<Integer> list = new ArrayList<Integer>();
//		int[] a = {5, 1, 2, 3, 1, 2, 1, 1, 2, 2, 1, 4, 3, 1, 1, 1, 2, 2, 4};
		int[] a = {};
		for(int b:a) {
			list.add(b);
		}
		int count = list.size();
		Integer total_same = 0;
		Integer total_diff = 0;
		int target = 5;
		
		Integer last = null;
		for(int i=0; i<count; i++) {
			if(i>0) {
				last = list.get(i-1);
			}
			Integer current = list.get(i);
			
			if(last == null) {
				total_diff += 1;
			} else if(last != current) {
				total_diff += 1;
			}
			
			if(last == null) {
				total_diff += 1;
			} else if(last != current) {
				total_diff += 1;
			}
			
			if(current == target) {
				if(i == 0 && last == null) {
					total_same += 1;
					continue;
				}
				if(last == target) {
					continue;
				}
				total_same += 1;
			}
		}
		
		System.out.println("total_same = " + total_same);
		System.out.println("total_diff = " + total_diff);
		
		
	}
	
}


package com.cn.eplat.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.b510b.excel.ReadExcel;
import com.cn.eplat.model.EpAtten;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.service.IAttenExceptionService;
import com.cn.eplat.service.IEpAttenService;
import com.cn.eplat.service.IEpUserService;
import com.cn.eplat.service.IMachineInfoService;
import com.cn.eplat.service.IRestCalendarService;
import com.cn.eplat.utils.DateUtil;
import com.cn.eplat.utils.LocationUtil;
import com.cn.eplat.utils.db2excel.DbToExcelUtil;
import com.cn.eplat.utils.epattenutil.EpAttenUtil;

@Controller
@RequestMapping("/epAttenController")
public class EpAttenController {
	private static Logger logger = Logger.getLogger(EpAttenController.class);
	
	private static IEpUserService epUserService;
	@Resource
	private IEpAttenService epAttenService;
	
	@Resource
	private IAttenExceptionService attenExceptionService;
	
	@Resource
	private IRestCalendarService restCalendarService;
	
	@Resource
	private IMachineInfoService machineInfoService;
	
	public IEpUserService getEpUserService() {
		return epUserService;
	}
	@Resource
	public void setEpUserService(IEpUserService epUserService) {
		EpAttenController.epUserService = epUserService;
	}

	/*
	 * 打卡接口（工号）
	 */
	@RequestMapping(params = "punchCard", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String punchCardWorkNoJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
//		String userid_str = request.getParameter("userid");
		String work_no = request.getParameter("work_no");
		String type = request.getParameter("type");
		String latitude = request.getParameter("latitude");	// 纬度
		String longtitude = request.getParameter("longitude");	// 经度
		String wifimac = request.getParameter("wifimac");
		String wifiname = request.getParameter("wifiname");
		String platform = request.getParameter("platform");
		
		// 对传入参数进行最基本的校验（如非空校验）
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(type) || StringUtils.isBlank(platform)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号(work_no)或打卡方式(type)或平台(platform)参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(!"GPS".equals(type) && !"WiFi".equals(type)) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "打卡方式参数(type)错误");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(!"Android".equalsIgnoreCase(platform) && !"iOS".equalsIgnoreCase(platform)) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "平台参数(platform)错误");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		EpUser epu_query = new EpUser();
		epu_query.setWork_no(work_no);
		
		// 查询工号对应的用户信息
		List<EpUser> epus = epUserService.getEpUserByCriteria(epu_query);
		
		int epu_id = 0;
		
		if(epus != null) {
			int epus_count = epus.size();
			if(epus_count != 0) {
				if(epus_count == 1) {
					EpUser epu_one = epus.get(0);
					epu_id = epu_one.getId();
				} else {
					jsonObj.put("ret_code", -6);
					jsonObj.put("ret_message", "根据工号查询到多个用户信息");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
			} else {
				jsonObj.put("ret_code", -5);
				jsonObj.put("ret_message", "根据工号未查询到匹配的用户信息");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
		} else {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "查询工号时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		
		EpAtten epa = new EpAtten();
		epa.setEp_uid(epu_id);
		int last_count = epAttenService.getLastPunchCardCountByEpUid(epa);
		epa.setCount(last_count+1);	// 将最后一次打卡记录的次数加一，得到当前这一次打卡的次数
		
		boolean is_success = false;	// 用来表示当前这次打卡是否成功（成功表示即地理位置在有效范围内，或者WiFi MAC地址是有效的）
		
		if("GPS".equals(type.trim())) {	// 如果是GPS定位方式，则取经纬度参数（latitude和longtitude）
			jsonObj.put("type", "GPS");
			// 首先还是对经纬度参数做最基本的校验
			if(StringUtils.isBlank(latitude) || StringUtils.isBlank(longtitude)) {
				jsonObj.put("ret_code", -7);
				jsonObj.put("ret_message", "GPS定位方式下，经度或纬度参数为空");
//				return jsonObj.toJSONString();
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			try {
				double latitude_val = Double.parseDouble(latitude);
				double longtitude_val = Double.parseDouble(longtitude);
				boolean is_location_valid = false;
				/*
				if("Android".equalsIgnoreCase(platform)) {
//					is_location_valid = LocationUtil.isGPSLocationValidAndroid(latitude_val, longtitude_val);	// 安卓平台定位方式下判断打卡是否有效
					is_location_valid = LocationUtil.isGPSLocationValidCommon(latitude_val, longtitude_val, platform);	// 安卓平台定位方式下判断打卡是否有效
					epa.setPlatform(platform);
				} else {
//					is_location_valid = LocationUtil.isGPSLocationValid(latitude_val, longtitude_val);	// iOS平台定位方式下判断打卡是否有效
					is_location_valid = LocationUtil.isGPSLocationValidCommon(latitude_val, longtitude_val, platform);	// iOS平台定位方式下判断打卡是否有效
					epa.setPlatform(platform);
				}
				*/
				
				epa.setPlatform(platform);	// 设置打卡时用户所使用的平台（目前主要是iOS和Android两大平台）
//				is_location_valid = LocationUtil.isGPSLocationValidCommon(latitude_val, longtitude_val, platform);	// iOS或安卓平台GPS定位方式下判断打卡是否有效
				
				StringBuffer valid_addr_sb = new StringBuffer();
//				is_location_valid = LocationUtil.isGPSLocationValidCommons(latitude_val, longtitude_val, platform, valid_addr_sb);	// 【多地区多中心点】iOS或安卓平台GPS定位方式下判断打卡是否有效
				
				// 【多地区多中心点】iOS或安卓平台GPS定位方式下判断打卡是否有效，返回结果使用JSONObject封装
				JSONObject loc_valid_json = LocationUtil.isGPSLocationValidCommonsWithDistance(latitude_val, longtitude_val, platform, valid_addr_sb);
				is_location_valid = loc_valid_json.getBooleanValue("is_gps_location_valid");
				Double gps_nearest_distance = loc_valid_json.getDouble("nearest_distance");
				
//				int userid = Integer.parseInt(userid_str);
//				EpAtten epa = new EpAtten();
//				epa.setEp_uid(userid);
//				int last_count = epAttenService.getLastPunchCardCountByEpUid(epa);
//				epa.setCount(last_count+1);	// 将最后一次打卡记录的次数加一，得到当前这一次打卡的次数
				epa.setType("GPS");
				epa.setLatitude(latitude_val);
				epa.setLongtitude(longtitude_val);
				epa.setGps_distance(gps_nearest_distance);
				epa.setIs_valid(is_location_valid);
//				epa.setTime(new Date());
				
				/*
				int ret = epAttenService.addEpAtten(epa);	// 添加一条打卡记录
				if(ret > 0) {
					jsonObj.put("ret_code", 1);
					jsonObj.put("ret_message", "打卡成功");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				} else {
					jsonObj.put("ret_code", -7);
					jsonObj.put("ret_message", "添加打卡记录失败");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
				*/
//				jsonObj.put("is_valid", is_location_valid);
				is_success = is_location_valid;
				if(is_location_valid) {	// 如果GPS打卡有效，则从预先设定的几个有效的地理位置信息中随机取出一个，作为该次GPS打卡对应的地理位置信息
//					epa.setGps_addr(LocationUtil.getRandomGPSAddr());
					epa.setGps_addr(valid_addr_sb.toString());
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				jsonObj.put("ret_code", -8);
				jsonObj.put("ret_message", "经纬度参数转换出错");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
		} else if("WiFi".equals(type.trim())) {	// 如果是WiFi打卡方式，则取WiFi MAC地址参数（wifimac）
			jsonObj.put("type", "WiFi");
			if(StringUtils.isBlank(wifimac)) {
				jsonObj.put("ret_code", -9);
				jsonObj.put("ret_message", "WiFi打卡方式下，WiFi MAC地址参数为空");
//				return jsonObj.toJSONString();
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			boolean is_wifi_valid = LocationUtil.isWiFiMACValid(wifimac.trim());
			
			if(StringUtils.isNotBlank(wifiname)) {
				epa.setWifi_name(wifiname.trim());	// 如果WiFi名字不为空白字符串，则将其先去掉首尾的空白字符后，再存入数据库中
			} else {
				epa.setWifi_name("<EMPTY>");	// 如果WiFi名字是空白字符串，则用占位字符串“<EMPTY>”代替
			}
			
			epa.setType("WiFi");
			epa.setWifi_mac(wifimac.trim());
			
			epa.setPlatform(platform);	// 设置打卡时用户所使用的平台（目前主要是iOS和Android两大平台）
			epa.setIs_valid(is_wifi_valid);
//			jsonObj.put("is_valid", is_wifi_valid);
			is_success = is_wifi_valid;
		} else { 	// 如果传入的打卡方式既不是“GPS”，也不是“WiFi”，则直接返回参数错误信息（打卡方式参数值只能是“GPS”或“WiFi”二选一）
			jsonObj.put("ret_code", -10);
			jsonObj.put("ret_message", "未知的打卡方式参数值");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int ret = epAttenService.addEpAtten(epa);	// 添加一条打卡记录
		if(ret > 0) {
			// 如果添加打卡记录成功，则存放打卡数据的变量epa中就会有一个有效的打卡记录的主键id值
			Long punch_id = epa.getId();
			
			Date punch_time = null;	// 用来存放刚刚的打卡记录的时间
			EpAtten epa_just_now = epAttenService.getEpAttenById(punch_id);	// 获取刚刚的打卡记录信息
			if(epa_just_now != null) {
				punch_time = epa_just_now.getTime();	// 获取到打卡时间
			} else {	// 如果从数据库中查询刚刚的打卡记录信息失败，则返回服务器系统当前时间，作为打卡时间
				punch_time = new Date();
//				jsonObj.put("ret_code", -12);
//				jsonObj.put("ret_message", "根据id查询刚刚的打卡记录失败");
//				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			if(punch_time != null) {	// 如果打卡时间不为空
				jsonObj.put("time", DateUtil.formatDate(2, punch_time));
			} else {	// 如果打卡时间为空，则直接将time字段的值设置为null
				jsonObj.put("time", null);
			}
			
			if(is_success) {	// 如果打卡成功
				jsonObj.put("ret_code", 1);
				jsonObj.put("ret_message", "打卡成功");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			} else {
				jsonObj.put("ret_code", 0);
				jsonObj.put("ret_message", "打卡失败");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
		} else {
			jsonObj.put("ret_code", -11);
			jsonObj.put("ret_message", "添加打卡记录失败");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	/**
	 * 根据工号获取用户信息
	 * 
	 * @param work_no 工号
	 * @return 这个工号对应的用户id（若不存在，则返回0；若存在多个，则返回-2；若有且仅有一个，则返回该用户的id(一个正整数)）
	 */
	public static EpUser getEpUserByWorkNo(String work_no) {
		
		if(StringUtils.isBlank(work_no)) {
			return null;	// 如果工号为空白字符串，则直接返回0
		}
		
		EpUser epu_query = new EpUser();
		epu_query.setWork_no(work_no);
		
		List<EpUser> epus = epUserService.getEpUserByCriteria(epu_query);
		
		if(epus != null) {
			if(epus.size() == 1) {	// 如果有且仅有唯一一个用户信息与该工号相匹配，则该用户即为所需要查找的用户
				EpUser epu_one = epus.get(0);	// 取出唯一的这个用户信息
				int epu_id = epu_one.getId();
				if(epu_id > 0) {	// 只有当用户的id是正数时，才返回正确的用户id
					return epu_one;	// 返回该用户的id（即userid）
				}
//				return null;	// 如果用户的id不是正数，则返回-3
				epu_query.setNotes("query_result=用户id不是正数");
			} else if(epus.size() == 0) {
//				return 0;	// 如果查询到的匹配的用户数量为0，则直接返回0
				epu_query.setNotes("query_result=匹配该工号的用户数量为0");
			} else {
//				return -2;	// 如果查询到有多个匹配的用户，则直接返回-2
				epu_query.setNotes("query_result=查询到有多个匹配该工号的用户");
			}
		} else {
//			return -1;	// 如果查询数据库失败，则直接返回-1
			epu_query.setNotes("query_result=查询数据库失败");
		}
		
		return epu_query;
	}
	
	/**
	 * 根据工号获取用户id
	 * 
	 * @param work_no 工号
	 * @return 这个工号对应的用户id（若不存在，则返回0；若存在多个，则返回-2；若有且仅有一个，则返回该用户的id(一个正整数)）
	 */
	public static int getEpUidByWorkNo(String work_no) {
		
		if(StringUtils.isBlank(work_no)) {
			return 0;	// 如果工号为空白字符串，则直接返回0
		}
		
		EpUser epu_query = new EpUser();
		epu_query.setWork_no(work_no);
		
		List<EpUser> epus = epUserService.getEpUserByCriteria(epu_query);
		
		if(epus != null) {
			if(epus.size() == 1) {	// 如果有且仅有唯一一个用户信息与该工号相匹配，则该用户即为所需要查找的用户
				EpUser epu_one = epus.get(0);	// 取出唯一的这个用户信息
				int epu_id = epu_one.getId();
				if(epu_id > 0) {	// 只有当用户的id是正数时，才返回正确的用户id
					return epu_id;	// 返回该用户的id（即userid）
				}
				return -3;	// 如果用户的id不是正数，则返回-3
				
			} else if(epus.size() == 0) {
				return 0;	// 如果查询到的匹配的用户数量为0，则直接返回0
			} else {
				return -2;	// 如果查询到有多个匹配的用户，则直接返回-2
			}
		} else {
			return -1;	// 如果查询数据库失败，则直接返回-1
		}
		
//		return 0;
	}
	
	/**
	 * 将传入的工号补足到len位（前面补0）
	 * 
	 * @param work_no
	 * @return
	 */
	public static String makeUpWorkNo(String work_no, int len) {
		if(StringUtils.isBlank(work_no) || len <= 0) {
			return null;
		}
		
		int wn_len = work_no.length();
		String work_no_mu = work_no;
		
		if(wn_len < len) {
			for(int i=0; i<len-wn_len; i++) {
				work_no_mu = "0" + work_no_mu;
			}
		}
		
		return work_no_mu;
	}
	
	
	/**
	 * 根据工号获取用户信息（针对可能出现的工号前面没有0的工号）
	 * 
	 * @param work_no 工号
	 * @return 这个工号对应的用户id（若不存在，则返回0；若存在多个，则返回-2；若有且仅有一个，则返回该用户的id(一个正整数)）
	 */
	public static EpUser getEpUserByWorkNoMach(String work_no) {
		
		if(StringUtils.isBlank(work_no)) {
			return null;	// 如果工号为空白字符串，则直接返回0
		}
		
		int work_no_len = work_no.length();
		if(work_no_len < 4) {
			String work_no_4 = makeUpWorkNo(work_no, 4);
			EpUser epu_4 = getEpUserByWorkNo(work_no_4);
			if(epu_4 != null) {
				return epu_4;
			} else {
				String work_no_5 = makeUpWorkNo(work_no, 5);
				EpUser epu_5 = getEpUserByWorkNo(work_no_5);
				if(epu_5 != null) {
					return epu_5;
				} else {
					String work_no_6 = makeUpWorkNo(work_no, 6);
					EpUser epu_6 = getEpUserByWorkNo(work_no_6);
					return epu_6;
				}
			}
		}
		
		return getEpUserByWorkNo(work_no);
	}
	
	
	
	/**
	 * 根据工号获取用户id（针对打卡机传过来的工号前面没有0的工号）
	 * 
	 * @param work_no 工号
	 * @return 这个工号对应的用户id（若不存在，则返回0；若存在多个，则返回-2；若有且仅有一个，则返回该用户的id(一个正整数)）
	 */
	public static int getEpUidByWorkNoMach(String work_no) {
		
		if(StringUtils.isBlank(work_no)) {
			return 0;	// 如果工号为空白字符串，则直接返回0
		}
		
		int work_no_len = work_no.length();
		if(work_no_len < 4) {
			String work_no_4 = makeUpWorkNo(work_no, 4);
			int epuid_4 = getEpUidByWorkNo(work_no_4);
			if(epuid_4 > 0) {
				return epuid_4;
			} else {
				String work_no_5 = makeUpWorkNo(work_no, 5);
				int epuid_5 = getEpUidByWorkNo(work_no_5);
				if(epuid_5 > 0) {
					return epuid_5;
				} else {
					String work_no_6 = makeUpWorkNo(work_no, 6);
					int epuid_6 = getEpUidByWorkNo(work_no_6);
					return epuid_6;
				}
			}
		}
		
		return getEpUidByWorkNo(work_no);
		
		/*
		EpUser epu_query = new EpUser();
		epu_query.setWork_no(work_no);
		
		List<EpUser> epus = epUserService.getEpUserByCriteria(epu_query);
		
		if(epus != null) {
			if(epus.size() == 1) {	// 如果有且仅有唯一一个用户信息与该工号相匹配，则该用户即为所需要查找的用户
				EpUser epu_one = epus.get(0);	// 取出唯一的这个用户信息
				int epu_id = epu_one.getId();
				if(epu_id > 0) {	// 只有当用户的id是正数时，才返回正确的用户id
					return epu_id;	// 返回该用户的id（即userid）
				}
				return -3;	// 如果用户的id不是正数，则返回-3
				
			} else if(epus.size() == 0) {
				return 0;	// 如果查询到的匹配的用户数量为0，则直接返回0
			} else {
				return -2;	// 如果查询到有多个匹配的用户，则直接返回-2
			}
		} else {
			return -1;	// 如果查询数据库失败，则直接返回-1
		}
		*/
	}
	
	/*
	 * 查询打卡记录接口（自定义日期范围）
	 * 注：只返回有效的打卡记录（即GPS位置在有效范围内，或WiFi MAC地址有效），无效的打卡记录不返回（不过，在数据库中不论是有效还是无效的打卡，都存有相应的记录，方便以后有需要时查看历史打卡记录）
	 */
	@RequestMapping(params = "getPunchCardRecordsCustom", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getPunchCardRecordsCustomJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
//		String userid_str = request.getParameter("userid");
		String work_no = request.getParameter("work_no");
		String date_range_start = request.getParameter("start");	// 开始日期
		String date_range_end = request.getParameter("end");	// 结束日期
		
		// 对传入参数进行最基本的校验（如非空校验）
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(date_range_start) || StringUtils.isBlank(date_range_end)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或日期范围（开始、结束）参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		int userid = 0;
//		int ret_uid = getEpUidByWorkNo(work_no);	// 根据工号获取用户的id值（相当于userid）
		EpUser ep_user = epUserService.getEpUserByWorkNum(work_no);
		if(ep_user.getId() <= 0) {	// 如果获取到的用户id值是异常的0或负整数值，则直接返回错误信息
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "根据工号获取用户id出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		String start_date_str_trim = date_range_start.trim();	// 去掉首尾空白字符后的开始日期字符串
		String end_date_str_trim = date_range_end.trim();	// 去掉首尾空白字符后的结束日期字符串
		Date start_date = DateUtil.parse2date(1, start_date_str_trim);	// 将开始日期字符串转换成开始日期（yyyy-MM-dd 格式）
		Date end_date = DateUtil.parse2date(1, end_date_str_trim);	// 将结束日期字符串转换成结束日期（yyyy-MM-dd 格式）
		
		if(start_date == null || end_date == null) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "开始日期或结束日期转换出错");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 根据获取到的用户id，以及所提供的开始日期、结束日期时间范围，得到该用户全部有效的打卡记录信息
		List<EpAtten> epas = epAttenService.getValidEpAttenByEpUidAndStartDateAndEndDate(ep_user.getId(), start_date, end_date);
		
		JSONArray jarr_empty_records = new JSONArray();	// 用来存放空Json数据的Json数组
		
		// 根据开始日期和结束日期得到这两个日期之间的所有日期（包含开始日期和结束日期）对应的的日期字符串组成的字符串数组
		String[] all_dates_arr = DateUtil.getDateArrBy2Dates(start_date, end_date);
		
		if(all_dates_arr == null) {
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "根据开始日期和结束日期获取这两个日期之间的所有日期字符串数组时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int all_dates_count = all_dates_arr.length;	// 得到开始日期和结束日期之间的所有日期（包含开始和结束）字符串的个数
		
		//根据用户所对应的类型获取开始日期，结束日期之间的所有休息日期
		List<Date> restDates = restCalendarService.getDatesBetweenStartAndEndByType(start_date, end_date,ep_user.getType());
		
		if(epas == null) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "根据用户id和开始日期、结束日期查询用户的打卡记录出现异常");
			
			for(int i=0; i<all_dates_count; i++) {
				JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
				empty_record.put("on_duty", null);
				empty_record.put("off_duty", null);
				empty_record.put("others", null);
				empty_record.put("has_abnormal", !EpAttenUtil.isRestday(all_dates_arr[i], restDates));
				empty_record.put("date", all_dates_arr[i]);
				jarr_empty_records.add(empty_record);
			}
			jsonObj.put("records", jarr_empty_records);
			
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		if(all_dates_arr == null) {
//			jsonObj.put("ret_code", -4);
//			jsonObj.put("ret_message", "根据用户id和开始日期、结束日期查询用户的打卡记录出现异常");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
//		if(all_dates_count == 0) {	// 如果日期字符串数组的长度为0（即开始和结束日期是同一天）
//			
//		}
		
		int epas_count = epas.size();	// 得到打卡记录的总条数
		if(epas_count == 0) {	// 如果打卡记录条数为0，即按指定的日期范围查询得到的打卡记录为空时，将按这个日期范围内每天的日期填充空的打卡记录信息
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "该用户在指定的时间范围内的打卡记录信息为空");
			
			for(int i=0; i<all_dates_count; i++) {
				JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
				empty_record.put("on_duty", null);
				empty_record.put("off_duty", null);
				empty_record.put("others", null);
				empty_record.put("has_abnormal",  !EpAttenUtil.isRestday(all_dates_arr[i], restDates));
				empty_record.put("date", all_dates_arr[i]);
				jarr_empty_records.add(empty_record);
			}
			jsonObj.put("records", jarr_empty_records);
			
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 如果打卡记录的总条数大于0（即至少有一条打卡记录信息）
		
		Comparator<EpAtten> epa_cmp = new Comparator<EpAtten>() {	// 构造一个比较器，用于比较打卡记录信息中的时间字段，使打卡记录按时间先后顺序排序（暂定为按时间从早到晚的顺序）
			@Override
			public int compare(EpAtten o1, EpAtten o2) {
				Date time1 = o1.getTime();
				Date time2 = o2.getTime();
				return time1.compareTo(time2);
			}
		};
		Collections.sort(epas, epa_cmp);
		
		SimpleDateFormat sdf = DateUtil.getSdf_hms();	// “年-月-日 时:分:秒”格式的日期格式
//		SimpleDateFormat sdf_ymd = DateUtil.getSdf_ymd();	// “年-月-日”格式的日期格式
		
		Date now_date = new Date();	// 当前查询打卡记录的时间
//		String now_date_str = sdf.format(now_date);
//		int indexOfspace = now_date_str.indexOf(' ');	// 找到“日期 时间”字符串中，中间的空格字符所在的下标位置
//		String today_ymd_str = now_date_str.substring(0, indexOfspace);	// 截取空格之前的“年-月-日”字符串
		String today_ymd_str = DateUtil.formatDate(1, now_date);	// 得到今天日期的“年-月-日”格式化的日期字符串
		
//		try {
////			ymd_18oclock = sdf.parse(today_ymd_str + " 18:00:00");
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
		Date ymd_18oclock = null;
		ymd_18oclock = DateUtil.parse2date(2, today_ymd_str + " 18:00:00");
		if(ymd_18oclock == null) {
			logger.error("转换当天时间18:00时出现错误");
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "转换当天时间18:00时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Date ymd_830clock = null;
		ymd_830clock = DateUtil.parse2date(2, today_ymd_str + " 08:30:00");
		if(ymd_830clock == null) {
			logger.error("转换当天时间08:30时出现错误");
			jsonObj.put("ret_code", -7);
			jsonObj.put("ret_message", "转换当天时间08:30时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
//		try {
//			ymd_830clock = sdf.parse(today_ymd_str + " 08:30:00");	// 拼凑出当天上午08:30的时间字符串，并将其转换成日期对象
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
		// 构造一个以日期字符串作为键（key），以这个日期下对应的打卡记录信息列表为值（value）的映射（TreeMap），用来按天分组存放全部有效的打卡记录信息
		TreeMap<String, ArrayList<EpAtten>> epas_map = new TreeMap<String, ArrayList<EpAtten>>();
		
		// 新创建一个对象数组，准备在后面用来存放全部以天为单位分组的打卡记录信息的数组列表对象的引用
//		Object[] arr_refs = new Object[all_dates_count];
		
		// 初始化映射TreeMap对象epas_map的key值为全部的日期字符串，value值初始化为新的EpAtten对象的数组列表对象
		for(int i=0; i<all_dates_count; i++) {
			ArrayList<EpAtten> epa_arri = new ArrayList<EpAtten>();
//			arr_refs[i] = epa_arri;	// 将第(i+1)个打卡记录信息分组列表对象的引用存进前面创建好的对象数组arr_refs中对应下标位置中
			epas_map.put(all_dates_arr[i], epa_arri);
		}
		
		// 开始循环遍历每一条打卡记录信息，将它们按天分组存入上面新声明好的映射epas_map中
		for(int i=0; i<epas_count; i++) {
			EpAtten epai = epas.get(i);	// 取出第(i+1)条打卡记录信息
			Date epai_time = epai.getTime();	// 得到这条打卡记录的打卡时间
			String epai_time_ymd = DateUtil.formatDate(1, epai_time);	// 将这条打卡记录的打卡时间格式化成“年-月-日”格式的日期字符串
			if(epai_time_ymd == null) {	// 如果将打卡时间格式化时出现异常，则丢弃这条打卡记录，继续下一条
				continue;
			}
			if(epas_map.containsKey(epai_time_ymd)) {	// 如果映射epas_map中包含格式化后的打卡时间日期字符串对应的key值，则将这条打卡记录存进这个日期key值对应的EpAtten数组列表对象中
				ArrayList<EpAtten> epas_val = epas_map.get(epai_time_ymd);
				epas_val.add(epai);
			}
		}
		
		JSONArray records_arr = new JSONArray();	// 准备用来存放全部按天分组的打卡记录信息Json对象的Json数组
		
		// 开始按天循环遍历每一天里的打卡记录信息，并按规则筛选出每天的打卡记录中哪些是上班卡、下班卡和其他打卡
		for(int i=0; i<all_dates_count; i++) {
			ArrayList<EpAtten> epas_arri = epas_map.get(all_dates_arr[i]);	// 取出以第(i+1)个日期字符串为key，对应日期的打卡记录列表数组对象
			if(epas_arri == null) {
				JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
				empty_record.put("on_duty", null);
				empty_record.put("off_duty", null);
				empty_record.put("others", null);
				empty_record.put("has_abnormal",  !EpAttenUtil.isRestday(all_dates_arr[i], restDates));
				empty_record.put("date", all_dates_arr[i]);
				records_arr.add(empty_record);
				continue;
			}
			int epas_arri_count = epas_arri.size();	// 取出第(i+1)个数组列表对象中包含的打卡记录信息的条数
			if(epas_arri_count == 0) {	// 如果这个日期下对应的有效的打卡记录信息条数为0，说明该日期下没有有效的打卡记录，则先用含有空记录的Json对象进行填充这一天的打卡记录信息，然后直接进入下一个日期的循环遍历
				JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
				empty_record.put("on_duty", null);
				empty_record.put("off_duty", null);
				empty_record.put("others", null);
				empty_record.put("has_abnormal",  !EpAttenUtil.isRestday(all_dates_arr[i], restDates));
				empty_record.put("date", all_dates_arr[i]);
				records_arr.add(empty_record);
				continue;
			}
			
			JSONObject day_record = new JSONObject();	// 用来存放按上班卡、下班卡和其他卡分类整理后每天的打卡记录信息的Json数据对象
			
			// 如果一个日期下对应的有效的打卡记录信息条数大于0（即至少有一条有效的打卡记录信息）
			EpAtten epa_on_duty = epas_arri.get(0);	// 取出这一天中第一条打卡记录作为上班卡
			
			boolean has_abnormal = false;	// 用来表示这一天中是否有异常打卡的标志
			
			Date hms_830clock = null;
			hms_830clock = DateUtil.parse2date(2, all_dates_arr[i] + " 08:30:00");
			if(hms_830clock == null) {
				logger.error("转换日期[" + all_dates_arr[i] + "]这一天的时间08:30时出现错误");
				jsonObj.put("ret_code", -8);
				jsonObj.put("ret_message", "转换日期[" + all_dates_arr[i] + "]这一天的时间08:30时出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			Date hms_18oclock = null;
			hms_18oclock = DateUtil.parse2date(2, all_dates_arr[i] + " 18:00:00");
			if(hms_18oclock == null) {
				logger.error("转换日期[" + all_dates_arr[i] + "]这一天的时间18:00时出现错误");
				jsonObj.put("ret_code", -9);
				jsonObj.put("ret_message", "转换日期[" + all_dates_arr[i] + "]这一天的时间18:00时出现异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			JSONObject on_duty_json = new JSONObject();	// 用来存放上班卡的Json数据
			
			Date on_duty_time = epa_on_duty.getTime();	// 取出上班卡时间
			on_duty_json.put("time", sdf.format(on_duty_time));	// 上班卡打卡时间（格式化成“yyyy-MM-dd HH:mm:ss”时间格式）
			String punch_type_on = epa_on_duty.getType();	// 取出上班卡打卡方式
			if("GPS".equals(punch_type_on)) {	// 如果是GPS定位方式打卡，则取经纬度的值返回
				on_duty_json.put("type", punch_type_on);	// 打卡方式（GPS）
				on_duty_json.put("latitude", epa_on_duty.getLatitude());	// 纬度值
				on_duty_json.put("longitude", epa_on_duty.getLongtitude());	// 经度值
				on_duty_json.put("gpsaddr", epa_on_duty.getGps_addr());	// GPS 对应的地理位置地址
			} else if("WiFi".equals(punch_type_on)) {	// 如果是WiFi方式打卡，则取MAC地址的值返回
				on_duty_json.put("type", punch_type_on);	// 打卡方式（WiFi）
				on_duty_json.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
				on_duty_json.put("wifiname", epa_on_duty.getWifi_name());	// 打卡时WiFi的名字
			} else if("IC_Card".equals(punch_type_on) || "fingerprint".equals(punch_type_on)){//打卡方式是打卡机打卡（目前ic打卡，指纹打卡2种）
				on_duty_json.put("type", punch_type_on);	// 打卡方式（打卡机打卡）
				String mach_sn = epa_on_duty.getMach_sn();
				String locationName = machineInfoService.getNameBySN(mach_sn);
				if(TextUtils.isEmpty(locationName)){
					on_duty_json.put("locationName", "未知");
				}else{
					on_duty_json.put("locationName", locationName);
				}
			}
			
			if(on_duty_time != null) {
				if(on_duty_time.compareTo(hms_830clock) <= 0) {	// 如果上班卡打卡时间在当天08:30之前（包含踩点08:30:00整），则视为正常上班卡
					on_duty_json.put("is_normal", true);	// 当前上班打卡正常
				} else {	// 如果上班卡打卡时间在当天08:30之后，则视为异常上班卡（即上班迟到）
					on_duty_json.put("is_normal", false);	// 当前上班打卡异常（上班卡异常即表示上班迟到）
					has_abnormal = true;	// 只要上班卡有异常，当天打卡有异常标志就设为true
				}
			} else {
				on_duty_json.put("is_normal", null);	// 当前上班打卡时间为空（null），无法判断上班卡是否正常
			}
			
			// 将上班卡相关的Json数据放进最后要返回的Json数据中
//			today_json.put("on_duty", on_duty_json);
			
			if(epas_arri_count == 1) {	// 如果一个日期下对应的有效的打卡记录信息条数为1（即只有一条有效的打卡记录信息），则这唯一的一条打卡记录作为上班卡，而无下班卡
				
				JSONObject one_record = new JSONObject();	// 用来存放那天打卡记录的Json数据对象
				one_record.put("on_duty", on_duty_json);
				one_record.put("off_duty", null);
				one_record.put("others", null);
				one_record.put("has_abnormal",  !EpAttenUtil.isRestday(all_dates_arr[i], restDates));
				one_record.put("date", all_dates_arr[i]);
				records_arr.add(one_record);
				continue;
			}
			
			// 如果这一天中有效的打卡记录条数大于1，则把第一条作为上班卡(on_duty)，把最后一条作为下班卡(off_duty)，其它打卡记录作为其它打卡(others)
			// 取出这一天内最后一条打卡记录信息作为下班卡记录
			EpAtten epa_off_duty = epas_arri.get(epas_arri_count-1);
			
			JSONObject off_duty_json = new JSONObject();	// 用来存放下班卡的Json数据
			
			Date off_duty_time = epa_off_duty.getTime();	// 取出下班卡时间
			off_duty_json.put("time", sdf.format(off_duty_time));
			String punch_type_off = epa_off_duty.getType();	// 取出下班卡打卡方式
			if("GPS".equals(punch_type_off)) {
				off_duty_json.put("type", punch_type_off);
				off_duty_json.put("latitude", epa_off_duty.getLatitude());
				off_duty_json.put("longitude", epa_off_duty.getLongtitude());
				off_duty_json.put("gpsaddr", epa_off_duty.getGps_addr());	// GPS 对应的地理位置地址
			} else if("WiFi".equals(punch_type_off)) {
				off_duty_json.put("type", punch_type_off);
				off_duty_json.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
				off_duty_json.put("wifiname", epa_off_duty.getWifi_name());	// 打卡时WiFi的名字
			} else if("IC_Card".equals(punch_type_off) || "fingerprint".equals(punch_type_off)){//打卡方式是打卡机打卡（目前ic打卡，指纹打卡2种）
				off_duty_json.put("type", punch_type_off);	// 打卡方式（打卡机打卡）
				String mach_sn = epa_off_duty.getMach_sn();
				String locationName = machineInfoService.getNameBySN(mach_sn);
				if(TextUtils.isEmpty(locationName)){
					off_duty_json.put("locationName", "未知");
				}else{
					off_duty_json.put("locationName", locationName);
				}
			}
			
			if(off_duty_time != null) {
				if(off_duty_time.compareTo(hms_18oclock) < 0) {	// 如果下班卡时间早于18:00，则将当前下班卡视为异常（即下班早退）
					off_duty_json.put("is_normal", false);
					has_abnormal = true;	// 只要下班卡有异常，当天打卡有异常标志就设为true
				} else {	// 如果下班卡时间晚于18:00（包含踩点18:00:00整），则将当前下班卡视为正常
					off_duty_json.put("is_normal", true);
				}
			} else {
				off_duty_json.put("is_normal", null);	// 当前下班卡时间为空（null），无法判断下班卡是否正常
			}
			
			// 除了上班卡、下班卡以外的其他的打卡记录都归为其它打卡
			ArrayList<EpAtten> epas_other = new ArrayList<EpAtten>();	// 用来存放其它打卡的数组
			// 开始往其他打卡数组里放入打卡记录
			for(int j = 1; j < epas_arri_count - 1; j++) {	// 从有效的打卡记录数组中，取出除第一条（数组下标为0）和最后一条（数组下标为valid_count-1）以外的打卡记录，作为其它打卡记录
				epas_other.add(epas_arri.get(j));
			}
			
			
			JSONArray others_json_arr = new JSONArray();	// 用来存放其它打卡记录的Json数组
			
			if(epas_other.size() > 0) {	// 如果其它打卡记录信息条数至少有一条时
				for(EpAtten epa:epas_other) {
					JSONObject json_tmp = new JSONObject();
					Date epa_time = epa.getTime();
					json_tmp.put("time", sdf.format(epa_time));
					String epa_type = epa.getType();
					if("GPS".equals(epa_type)) {
						json_tmp.put("type", epa_type);	// GPS打卡方式
						json_tmp.put("latitude", epa.getLatitude());
						json_tmp.put("longitude", epa.getLongtitude());
						json_tmp.put("gpsaddr", epa.getGps_addr());	// GPS 对应的地理位置地址
					} else if("WiFi".equals(epa_type)) {
						json_tmp.put("type", epa_type);	// WiFi打卡方式
						json_tmp.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
						json_tmp.put("wifiname", epa.getWifi_name());	// 打卡时WiFi的名字
					} else if("IC_Card".equals(epa_type) || "fingerprint".equals(epa_type)){//打卡方式是打卡机打卡（目前ic打卡，指纹打卡2种）
						json_tmp.put("type", epa_type);	// 打卡方式（打卡机打卡）
						String mach_sn = epa.getMach_sn();
						String locationName = machineInfoService.getNameBySN(mach_sn);
						if(TextUtils.isEmpty(locationName)){
							json_tmp.put("locationName", "未知");
						}else{
							json_tmp.put("locationName", locationName);
						}
					}
					others_json_arr.add(json_tmp);	// 将其他打卡Json数据一个一个放进Json数组others_json_arr中
				}
			} else {	// 如果其它打卡记录信息条数为0（即没有其它打卡记录）时，返回Json数组设置为空
//				others_json_arr.add(null);
			}
			
			// 开始把一天内按上班卡、下班卡和其他打卡分类好的打卡记录信息放进这一天对应的Json数据对象中
			day_record.put("on_duty", on_duty_json);
			day_record.put("off_duty", off_duty_json);
			day_record.put("others", others_json_arr);
			day_record.put("date", all_dates_arr[i]);	// 这一天对应的日期字符串
			day_record.put("has_abnormal", EpAttenUtil.isRestday(all_dates_arr[i], restDates) ? false : has_abnormal);	// 这一天内是否有异常打卡记录的标志
			
			
			// 按上班卡、下班卡、其它打卡分类处理完一天内的所有打卡记录后，将分类结果存进以天为单位分组的打卡记录Json数组records_arr中
			records_arr.add(day_record);
		}
		
		jsonObj.put("records", records_arr);
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "按自定义日期范围查询打卡记录成功");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	/*
	 * 查询打卡记录接口（工号）
	 * 注：只返回有效的打卡记录（即GPS位置在有效范围内，或WiFi MAC地址有效），无效的打卡记录不返回（不过，在数据库中不论是有效还是无效的打卡，都存有相应的记录，方便以后有需要时查看历史打卡记录）
	 */
	@RequestMapping(params = "getPunchCardRecords", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getPunchCardRecordsWorkNoJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		// 从接收到的请求中获得传入参数
//		String userid_str = request.getParameter("userid");
		String work_no = request.getParameter("work_no");
		String day_range_str = request.getParameter("range");
		
		// 对传入参数进行最基本的校验（如非空校验）
		if(StringUtils.isBlank(work_no) || StringUtils.isBlank(day_range_str)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "工号或时间范围参数为空");
//					return jsonObj.toJSONString();
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		int userid = 0;
//		try {
//			userid = Integer.parseInt(userid_str);
//		} catch (NumberFormatException e1) {
//			e1.printStackTrace();
//			jsonObj.put("ret_code", -2);
//			jsonObj.put("ret_message", "用户id参数转换出错");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		int day_range = 0;	// 存放用于查询打卡记录的时间范围的参数，以天为单位，要求必须是正整数，否则返回错误提示信息
		try {
			day_range = Integer.parseInt(day_range_str);
			if(day_range <= 0) {	// 如果时间范围参数值小于等于0，则直接返回错误信息
				jsonObj.put("ret_code", -3);
				jsonObj.put("ret_message", "时间范围参数必须是正整数");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "时间范围参数转换出错");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// 验证userid是否在数据库中真实对应一个用户，如果不存在用户与该userid相对应，则返回错误提示信息
//		if(epUserService.getEpUserById(userid) == null) {
//			jsonObj.put("ret_code", -5);
//			jsonObj.put("ret_message", "数据库中未找到该用户id对应的用户信息");
//			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
//		}
		
		EpUser epu_query = new EpUser();
		
		epu_query.setWork_no(work_no);
		
		// 查询工号对应的用户信息
		List<EpUser> epus = epUserService.getEpUserByCriteria(epu_query);
		
		int epu_id = 0;
		
		if(epus != null) {
			int epus_count = epus.size();
			if(epus_count != 0) {
				if(epus_count == 1) {
					EpUser epu_one = epus.get(0);
					epu_id = epu_one.getId();
				} else {
					jsonObj.put("ret_code", -6);
					jsonObj.put("ret_message", "根据工号查询到多个用户信息");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
			} else {
				jsonObj.put("ret_code", -5);
				jsonObj.put("ret_message", "根据工号未查询到匹配的用户信息");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
		} else {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "查询工号时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		List<EpAtten> epas = epAttenService.getEpAttenByUidAndDayRange(epu_id, day_range);
		
//		JSONObject empty_day_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
//		empty_day_record.put("date", null);
//		empty_day_record.put("on_duty", null);
//		empty_day_record.put("off_duty", null);
//		empty_day_record.put("others", null);
//		empty_day_record.put("has_abnormal", true);
		
		JSONArray jarr_empty_records = new JSONArray();	// 用来存放空Json数据的Json数组
		
		if(epas != null) {
			if(epas.size() == 0) {	// 如果查询到的打卡记录条数为0（即在给定时间范围内未找到该用户的打卡记录信息）时，直接返回相应的提示信息
				jsonObj.put("ret_code", 0);
				jsonObj.put("ret_message", "该用户在指定的时间范围内的打卡记录信息为空");
				
				for(int i=0; i<day_range; i++) {
					JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
//					empty_record.put("date", null);
					empty_record.put("on_duty", null);
					empty_record.put("off_duty", null);
					empty_record.put("others", null);
					empty_record.put("has_abnormal", true);
					empty_record.put("date", DateUtil.getDateXDaysAgo(i));
					jarr_empty_records.add(empty_record);
				}
				jsonObj.put("records", jarr_empty_records);
				
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
		} else {
			jsonObj.put("ret_code", -7);
			jsonObj.put("ret_message", "查询用户打卡记录信息时出现null错误");
//			jsonObj.put("records", null);
			
			for(int i=0; i<day_range; i++) {
				JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
//				empty_record.put("date", null);
				empty_record.put("on_duty", null);
				empty_record.put("off_duty", null);
				empty_record.put("others", null);
				empty_record.put("has_abnormal", true);
				empty_record.put("date", DateUtil.getDateXDaysAgo(i));
				jarr_empty_records.add(empty_record);
			}
			jsonObj.put("records", jarr_empty_records);
			
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		// // // 先对查到的打卡记录进行初步的删选和处理
		ArrayList<EpAtten> epas_no_null_time = new ArrayList<EpAtten>();	// 用来存放时间字段不为空（null）的打卡记录信息的数组
		for(EpAtten epa:epas) {
			if(epa.getTime() != null) {	// 找出时间字段的值不为空的所有打卡记录，并将其放进新的数组epas_no_null_time中
				epas_no_null_time.add(epa);
			}
		}
		if(epas_no_null_time.size() == 0) {	// 如果时间字段不为空的打卡记录信息条数为0（即当天所有打卡记录的时间字段都为空），则直接返回错误提示信息
			jsonObj.put("ret_code", -8);
			jsonObj.put("ret_message", "所有打卡记录信息中的打卡时间全为空");
//			jsonObj.put("records", null);
			
			for(int i=0; i<day_range; i++) {
				JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
//				empty_record.put("date", null);
				empty_record.put("on_duty", null);
				empty_record.put("off_duty", null);
				empty_record.put("others", null);
				empty_record.put("has_abnormal", true);
				empty_record.put("date", DateUtil.getDateXDaysAgo(i));
				jarr_empty_records.add(empty_record);
			}
			jsonObj.put("records", jarr_empty_records);
			
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		ArrayList<EpAtten> epas_valid = new ArrayList<EpAtten>();	// 用来存放有效的打卡记录（即is_valid字段的值为true的打卡记录）信息的数组
		for(EpAtten epa:epas_no_null_time) {
			if(epa.getIs_valid() != null && epa.getIs_valid().equals(Boolean.TRUE)) {
				epas_valid.add(epa);	// 只有有效的打卡记录信息才会加入到数组epas_valid中来，其余无效的打卡记录信息将会被舍弃
			}
		}
		int valid_count = epas_valid.size();	// 得到有效的打卡记录条数
		if(valid_count == 0) {	// 如果有效的打卡记录信息条数为0（即没有有效的打卡记录信息），则直接返回错误提示信息
			jsonObj.put("ret_code", -9);
			jsonObj.put("ret_message", "没有有效的打卡记录信息");
//			jsonObj.put("records", null);
			
			for(int i=0; i<day_range; i++) {
				JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
//				empty_record.put("date", null);
				empty_record.put("on_duty", null);
				empty_record.put("off_duty", null);
				empty_record.put("others", null);
				empty_record.put("has_abnormal", true);
				empty_record.put("date", DateUtil.getDateXDaysAgo(i));
				jarr_empty_records.add(empty_record);
			}
			jsonObj.put("records", jarr_empty_records);
			
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Comparator<EpAtten> epa_cmp = new Comparator<EpAtten>() {	// 构造一个比较器，用于比较打卡记录信息中的时间字段，使打卡记录按时间先后顺序排序（暂定为按时间从早到晚的顺序）
			@Override
			public int compare(EpAtten o1, EpAtten o2) {
				Date time1 = o1.getTime();
				Date time2 = o2.getTime();
//					time1.compareTo(time2);
				return time1.compareTo(time2);
			}
		};
		Collections.sort(epas_valid, epa_cmp);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");	// “年-月-日”格式的日期格式
		
//			DateFormat sdfi = SimpleDateFormat.getInstance();
//			sdfi.parse("")
		Date now_date = new Date();	// 当前查询打卡记录的时间
//			now_date.
		String now_date_str = sdf.format(now_date);
		int indexOfspace = now_date_str.indexOf(' ');	// 找到“日期 时间”字符串中，中间的空格字符所在的下标位置
		String today_ymd_str = now_date_str.substring(0, indexOfspace);	// 截取空格之前的“年-月-日”字符串
		
		Date ymd_18oclock = null;
		try {
			ymd_18oclock = sdf.parse(today_ymd_str + " 18:00:00");
		} catch (ParseException e1) {
			e1.printStackTrace();
			logger.error("转换当天时间18:00时出现错误");
			jsonObj.put("ret_code", -10);
			jsonObj.put("ret_message", "转换当天时间18:00时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		Date ymd_830clock = null;
		try {
			ymd_830clock = sdf.parse(today_ymd_str + " 08:30:00");	// 拼凑出当天上午08:30的时间字符串，并将其转换成日期对象
//			if(on_duty_time != null) {
//				if(on_duty_time.compareTo(ymd_830clock) <= 0) {	// 如果上班卡打卡时间在当天08:30之前（包含踩点08:30:00整），则视为正常上班卡
//					on_duty_json.put("is_normal", true);	// 当前上班打卡正常
//				} else {	// 如果上班卡打卡时间在当天08:30之后，则视为异常上班卡（即上班迟到）
//					on_duty_json.put("is_normal", false);	// 当前上班打卡异常（上班卡异常即表示上班迟到）
//				}
//			} else {
//				on_duty_json.put("is_normal", null);	// 当前上班打卡时间为空（null）
//			}
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("转换当天时间08:30时出现错误");
			jsonObj.put("ret_code", -11);
			jsonObj.put("ret_message", "转换当天时间08:30时出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		
		if(day_range == 1) {	// 如果时间范围只有1天（即只查看当天的打卡记录）
			boolean has_abnormal = false;	// 用来标志当天打卡记录中是否有异常情况（包括：只有上班卡记录(忘打下班卡)、上班卡异常(上班迟到)、下班卡异常(下班早退)）；
											// 对于无打卡记录(即旷工)的情况，以返回的当天的打卡记录Json数据值为null来判断
			
//			ArrayList<EpAtten> epas_on_duty = new ArrayList<EpAtten>();	// 用来存放上班卡的数组
			EpAtten epa_on_duty = null;	// 用来存放上班卡记录
//			ArrayList<EpAtten> epas_off_duty = new ArrayList<EpAtten>();	// 用来存放下班卡的数组
			EpAtten epa_off_duty = null;	// 用来存放下班卡记录
			ArrayList<EpAtten> epas_other = new ArrayList<EpAtten>();	// 用来存放其它打卡的数组
			
//			int valid_count = epas_valid.size();	// 得到有效的打卡记录条数
//			if(valid_count == 1) {	// 如果只有一条有效的打卡记录，则视为上班卡记录（无论这条打卡记录是在8:30之前还是之后）；从而，当天就没有下班卡记录和其它打卡记录了。
//			}
//			epas_on_duty.add(epas_valid.get(0));	// 取出第一条有效的打卡记录作为上班卡记录，放进上班卡的数组（因为程序运行到这里时，至少有一条有效的打卡记录了，只要有有效的打卡记录，就把第一条当作上班卡记录）
			epa_on_duty = epas_valid.get(0);
			
			if(valid_count > 1) {	// 如果当天有不止一条有效的打卡记录，则可以取到除第一次以外的最后一条有效的打卡记录
				EpAtten last_valid_epa = epas_valid.get(valid_count-1);	// 取出当天最后一条有效的打卡记录
//				epa_off_duty = last_valid_epa;
				Date last_valid_epa_time = last_valid_epa.getTime();
				if(last_valid_epa_time != null) {
					
					epa_off_duty = last_valid_epa;	// 不论现在有没有到下午18:00，都把最后一条有效的/成功的打卡记录作为下班卡
					
					// 开始往其他打卡数组里放入打卡记录
					for(int i = 1; i < valid_count - 1; i++) {	// 从有效的打卡记录数组中，取出除第一条（数组下标为0）和最后一条（数组下标为valid_count-1）以外的打卡记录，作为其它打卡记录
						epas_other.add(epas_valid.get(i));
					}
					
				}
			}
			
			// 开始准备返回Json数据
			jsonObj.put("ret_code", 1);
			jsonObj.put("ret_message", "查询当天打卡记录成功");
			
			JSONObject today_json = new JSONObject();
			today_json.put("date", today_ymd_str);	// 查询日期（yyyy-MM-dd 格式）
//			JSONObject today_record = new JSONObject();
			JSONArray today_record_arr = new JSONArray();
			
			// 下面是与组织上班卡相关的Json数据相关的代码
			if(epa_on_duty != null) {
				JSONObject on_duty_json = new JSONObject();	// 用来存放上班卡的Json数据
				
//				on_duty_json.put("date", today_ymd_str);	// 打卡记录的日期
				Date on_duty_time = epa_on_duty.getTime();	// 取出上班卡时间
				on_duty_json.put("time", sdf.format(on_duty_time));	// 上班卡打卡时间（已格式化成“yyyy-MM-dd HH:mm:ss”时间格式）
				String punch_type = epa_on_duty.getType();
				if("GPS".equals(punch_type)) {	// 如果是GPS定位方式打卡，则取经纬度的值返回
					on_duty_json.put("type", punch_type);	// 打卡方式（GPS）
					on_duty_json.put("latitude", epa_on_duty.getLatitude());	// 纬度值
					on_duty_json.put("longitude", epa_on_duty.getLongtitude());	// 经度值
					on_duty_json.put("gpsaddr", epa_on_duty.getGps_addr());	// GPS 对应的地理位置地址
				} else if("WiFi".equals(punch_type)) {	// 如果是WiFi方式打卡，则取MAC地址的值返回
					on_duty_json.put("type", punch_type);	// 打卡方式（WiFi）
//					on_duty_json.put("wifimac", epa_on_duty.getWifi_mac());	// WiFi MAC地址
					on_duty_json.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
					on_duty_json.put("wifiname", epa_on_duty.getWifi_name());	// 打卡时WiFi的名字
				}
				
//				on_duty_json.put("is_valid", epa_on_duty.getIs_valid());	// 是否有效
				
				if(on_duty_time != null) {
					if(on_duty_time.compareTo(ymd_830clock) <= 0) {	// 如果上班卡打卡时间在当天08:30之前（包含踩点08:30:00整），则视为正常上班卡
						on_duty_json.put("is_normal", true);	// 当前上班打卡正常
					} else {	// 如果上班卡打卡时间在当天08:30之后，则视为异常上班卡（即上班迟到）
						on_duty_json.put("is_normal", false);	// 当前上班打卡异常（上班卡异常即表示上班迟到）
						has_abnormal = true;	// 只要上班卡有异常，当天打卡有异常标志就设为true
					}
				} else {
					on_duty_json.put("is_normal", null);	// 当前上班打卡时间为空（null）
				}
				
				// 将上班卡相关的Json数据放进最后要返回的Json数据中
				today_json.put("on_duty", on_duty_json);
			} else {	// 如果上班卡为空，则设置上班卡的值为null
				today_json.put("on_duty", null);
			}
			
			// 下面是与组织下班卡相关的Json数据相关的代码
			if(epa_off_duty != null) {
				JSONObject off_duty_json = new JSONObject();	// 用来存放下班卡的Json数据
				
				Date off_duty_time = epa_off_duty.getTime();	// 取出下班卡时间
				off_duty_json.put("time", sdf.format(off_duty_time));
				String punch_type = epa_off_duty.getType();
				if("GPS".equals(punch_type)) {
					off_duty_json.put("type", punch_type);
					off_duty_json.put("latitude", epa_off_duty.getLatitude());
					off_duty_json.put("longitude", epa_off_duty.getLongtitude());
					off_duty_json.put("gpsaddr", epa_off_duty.getGps_addr());	// GPS 对应的地理位置地址
				} else if("WiFi".equals(punch_type)) {
					off_duty_json.put("type", punch_type);
//					off_duty_json.put("wifimac", epa_off_duty.getWifi_mac());
					off_duty_json.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
					off_duty_json.put("wifiname", epa_off_duty.getWifi_name());	// 打卡时WiFi的名字
				}
				
				if(off_duty_time != null) {
					if(off_duty_time.compareTo(ymd_18oclock) < 0) {	// 如果下班卡时间早于18:00，则将当前下班卡视为异常（即下班早退）
						off_duty_json.put("is_normal", false);
						has_abnormal = true;	// 只要下班卡有异常，当天打卡有异常标志就设为true
					} else {	// 如果下班卡时间晚于18:00（包含踩点18:00:00整），则将当前下班卡视为正常
						off_duty_json.put("is_normal", true);
					}
				} else {
					off_duty_json.put("is_normal", null);	// 当前下班卡时间为空（null）
				}
				
				// 将下班卡相关的Json数据放进最后要返回的Json数据中
				today_json.put("off_duty", off_duty_json);
			} else {	// 如果下班卡为空，则设置下班卡的值为null
				today_json.put("off_duty", null);
			}
			
			// 下面是与组织其它打卡相关的Json数据相关的代码
			if(epas_other != null) {
				JSONArray others_json_arr = new JSONArray();	// 用来存放其它打卡记录的Json数组
				
				if(epas_other.size() > 0) {	// 如果其它打卡记录信息条数至少有一条时
					for(EpAtten epa:epas_other) {
						JSONObject json_tmp = new JSONObject();
						Date epa_time = epa.getTime();
						json_tmp.put("time", sdf.format(epa_time));
						String epa_type = epa.getType();
						if("GPS".equals(epa_type)) {
							json_tmp.put("type", epa_type);	// GPS打卡方式
							json_tmp.put("latitude", epa.getLatitude());
							json_tmp.put("longitude", epa.getLongtitude());
							json_tmp.put("gpsaddr", epa.getGps_addr());	// GPS 对应的地理位置地址
						} else if("WiFi".equals(epa_type)) {
							json_tmp.put("type", epa_type);	// WiFi打卡方式
//							json_tmp.put("wifimac", epa.getWifi_mac());
							json_tmp.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
							json_tmp.put("wifiname", epa.getWifi_name());	// 打卡时WiFi的名字
						}
						others_json_arr.add(json_tmp);	// 将其他打卡Json数据一个一个放进Json数组others_json_arr中
					}
				} else {	// 如果其它打卡记录信息条数为0（即没有其它打卡记录）时，返回Json数组设置为空
//					others_json_arr.add(null);
				}
				today_json.put("others", others_json_arr);
			}
			today_json.put("has_abnormal", has_abnormal);	// 将标志当天是否有异常打卡的布尔变量放在Json数据的has_abnormal字段中
			
			// 所有数据都组织完后，就可以返回最终的Json数据了
//			JSONArray jarr_tmp = new JSONArray();
//			jarr_tmp.add(today_json);
//			jsonObj.put(today_ymd_str, jarr_tmp);	// 将当天的打卡记录Json数据对象封装进一个Json数组中后再返回，以便与多天打卡记录查询的结果在数据结构上保持一致
			
//			JSONObject today_record = new JSONObject();
//			today_record.put(today_ymd_str, today_json);
			
			today_record_arr.add(today_json);
			
			jsonObj.put("records", today_record_arr);
//			jsonObj.put("has_abnormal", has_abnormal);
			
			
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		} else {	// 如果时间范围大于1天（即要查看当天的和当天之前历史的打卡记录）
			// 先将所有有效的打卡记录以天为单位进行分组，放进一个TreeMap中存放
			TreeMap<String, ArrayList<EpAtten>> epas_map = new TreeMap<String, ArrayList<EpAtten>>();	// 该TreeMap的key值为String类型，将用来存放日期“年-月-日”字符串
			// 声明day_range个数组，用来分别存放对应日期下的打卡记录信息
			ArrayList<ArrayList<EpAtten>> epa_date_arr = new ArrayList<ArrayList<EpAtten>>();
			
			String[] date_keys = new String[day_range];	// 用来存放上面的TreeMap的日期key值的字符串数组
			TreeSet<String> date_keys_set = new TreeSet<String>();	// 用来存放上面的TreeMap的日期key值的字符串集合
			
			for(int i=0; i<day_range; i++) {
				epa_date_arr.add(new ArrayList<EpAtten>());	// 将day_range个数组的引用初始化为新的ArrayList<EpAtten>对象
				String date_idays_ago_str = DateUtil.getDateXDaysAgo(i);
				date_keys[i] = date_idays_ago_str;	// 日期key值字符串数组中存放的日期，是按日期从晚到早的顺序排列的（即第一个是当天的日期，最后一个是时间范围最早的一个）
				date_keys_set.add(date_idays_ago_str);	// 将日期key值字符串存进字符串集合date_keys_set里（方便后面查询日期key值是否存在）
				epas_map.put(date_idays_ago_str, null);	// 将每个日期对应的打卡记录数组的引用初始化为null
			}
//			now_date.
//			epas_valid.size();	// valid_count
			for(int i=0; i<valid_count; i++) {	// 遍历每一条打卡记录信息
				EpAtten epa_i = epas_valid.get(i);	// 取出第(i+1)个打卡记录
				Date epai_time = epa_i.getTime();	// 取出第(i+1)个打卡记录的打卡时间
				if(epai_time != null) {
//					" ".indexOf(ch)
					String epai_time_ymd_str = sdf_ymd.format(epai_time);	// 取出打卡时间的“年-月-日”格式化后的字符串
					if(date_keys_set.contains(epai_time_ymd_str)) {	// 如果当前打卡时间“年-月-日”在日期key值字符串集合里存在
//						for(int j=0; i<day_range; i++) {	// 遍历日期key值数组，以确定是在数组中的第几个
////							date_keys[j]
//						}
						int indexOfymd_str = getIndexOfAStr(date_keys, epai_time_ymd_str);	// 得到打卡时间“年-月-日”在日期key的数组中的下标位置
						if(indexOfymd_str >= 0) {	// 如果得到的位置下标为非负值（即为一个有效的下标值）
//							epa_date_arr.set(indexOfymd_str, epa_i);
							epa_date_arr.get(indexOfymd_str).add(epa_i);	// 往对应日期下的打卡记录信息数组中添加打卡记录
						}
					}
				}
			}
			
			for(int i=0; i<day_range; i++) {
				epas_map.put(date_keys[i], epa_date_arr.get(i));	// 将i天前的打卡记录数组放入对应的<日期key, 该日期下的打卡记录数组> Key-Value映射map中
			}
			
			
			// 声明一个Json数组，用于存放以天为单位分组的打卡记录
			JSONArray ja_tmp = new JSONArray();
			// 声明一个Json对象，用于存放以天为单位分组的打卡记录信息
//			JSONObject jo_tmp = new JSONObject();
			
			
//			JSONObject empty_day_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
//			empty_day_record.put("on_duty", null);
//			empty_day_record.put("off_duty", null);
//			empty_day_record.put("others", null);
//			empty_day_record.put("has_abnormal", true);
			
			
			// 开始以天为单位开始循环遍历每一条打卡记录，组织好Json数据
			for(int i=0; i<day_range; i++) {
				boolean has_abnormal = false;	// 用来标志当天打卡记录中是否有异常情况（包括：只有上班卡记录(忘打下班卡)、上班卡异常(上班迟到)、下班卡异常(下班早退)）；
												// 对于无打卡记录(即旷工)的情况，以返回的当天的打卡记录Json数据值为null来判断
				JSONObject iday_ago_json = new JSONObject();	// 用来存放i天前那一天内的打卡记录信息的Json对象
				
				if(i == 0) {	// 如果是0天前的打卡记录（即表示是当天的打卡记录信息），就按当天的打卡判断标准进行处理
					ArrayList<EpAtten> today_punch_arr = epas_map.get(date_keys[0]);	// 取出当天的打卡记录数组
					if(today_punch_arr != null) {
						int today_punch_count = today_punch_arr.size();
						if(today_punch_count != 0) {
							EpAtten epa_on_duty = null;	// 用来存放上班卡记录
							EpAtten epa_off_duty = null;	// 用来存放下班卡记录
							ArrayList<EpAtten> epas_other = new ArrayList<EpAtten>();	// 用来存放其它打卡的数组
							
							epa_on_duty = today_punch_arr.get(0);
							
							if(today_punch_count > 1) {
								EpAtten last_valid_epa = today_punch_arr.get(today_punch_count-1);	// 取出当天最后一条有效的打卡记录
								Date last_valid_epa_time = last_valid_epa.getTime();
								if(last_valid_epa_time != null) {
									/*
									if(now_date.before(ymd_18oclock)) {	// 如果当前查询打卡记录的时间在当天18:00之前，则暂时无法判断下班卡记录，即下班卡为空
										// 此处留空，保持epa_off_duty为null
									} else {	// 如果当前查询打卡记录的时间在当天18:00之后（包含踩点18:00:00整），则以当天最后一次打卡记录作为下班卡
										epa_off_duty = last_valid_epa;
									}
									*/
									epa_off_duty = last_valid_epa;	// 不论现在有没有到下午18:00，都把最后一条有效的/成功的打卡记录作为下班卡
									
									// 开始往其他打卡数组里放入打卡记录
									for(int j = 1; j < today_punch_count - 1; j++) {	// 从有效的打卡记录数组中，取出除第一条（数组下标为0）和最后一条（数组下标为valid_count-1）以外的打卡记录，作为其它打卡记录
										epas_other.add(today_punch_arr.get(j));
									}
									
								}
							}
							
							// 开始准备返回Json数据
//							jsonObj.put("ret_code", 1);
//							jsonObj.put("ret_message", "查询当天打卡记录成功");
							
							JSONObject today_json = new JSONObject();	// 用来存放当天的打卡记录Json数据的Json对象
							
							today_json.put("date", date_keys[0]);	// 打卡记录的日期
							
							// 下面是与组织上班卡相关的Json数据相关的代码
							if(epa_on_duty != null) {
								JSONObject on_duty_json = new JSONObject();	// 用来存放上班卡的Json数据
								
								Date on_duty_time = epa_on_duty.getTime();	// 取出上班卡时间
								on_duty_json.put("time", sdf.format(on_duty_time));	// 上班卡打卡时间（已格式化成“yyyy-MM-dd HH:mm:ss”时间格式）
								String punch_type = epa_on_duty.getType();
								if("GPS".equals(punch_type)) {	// 如果是GPS定位方式打卡，则取经纬度的值返回
									on_duty_json.put("type", punch_type);	// 打卡方式（GPS）
									on_duty_json.put("latitude", epa_on_duty.getLatitude());	// 纬度值
									on_duty_json.put("longitude", epa_on_duty.getLongtitude());	// 经度值
									on_duty_json.put("gpsaddr", epa_on_duty.getGps_addr());	// GPS 对应的地理位置地址
								} else if("WiFi".equals(punch_type)) {	// 如果是WiFi方式打卡，则取MAC地址的值返回
									on_duty_json.put("type", punch_type);	// 打卡方式（WiFi）
//									on_duty_json.put("wifimac", epa_on_duty.getWifi_mac());	// WiFi MAC地址
									on_duty_json.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
									on_duty_json.put("wifiname", epa_on_duty.getWifi_name());	// 打卡时WiFi的名字
								}
								
								if(on_duty_time != null) {
									if(on_duty_time.compareTo(ymd_830clock) <= 0) {	// 如果上班卡打卡时间在当天08:30之前（包含踩点08:30:00整），则视为正常上班卡
										on_duty_json.put("is_normal", true);	// 当前上班打卡正常
									} else {	// 如果上班卡打卡时间在当天08:30之后，则视为异常上班卡（即上班迟到）
										on_duty_json.put("is_normal", false);	// 当前上班打卡异常（上班卡异常即表示上班迟到）
										has_abnormal = true;
									}
								} else {
									on_duty_json.put("is_normal", null);	// 当前上班打卡时间为空（null）
								}
								
								// 将上班卡相关的Json数据放进最后要返回的Json数据中
								today_json.put("on_duty", on_duty_json);
							} else {	// 如果上班卡为空，则设置上班卡的值为null
								today_json.put("on_duty", null);
							}
							
							// 下面是与组织下班卡相关的Json数据相关的代码
							if(epa_off_duty != null) {
								JSONObject off_duty_json = new JSONObject();	// 用来存放下班卡的Json数据
								
								Date off_duty_time = epa_off_duty.getTime();	// 取出下班卡时间
								off_duty_json.put("time", sdf.format(off_duty_time));
								String punch_type = epa_off_duty.getType();
								if("GPS".equals(punch_type)) {
									off_duty_json.put("type", punch_type);
									off_duty_json.put("latitude", epa_off_duty.getLatitude());
									off_duty_json.put("longitude", epa_off_duty.getLongtitude());
									off_duty_json.put("gpsaddr", epa_off_duty.getGps_addr());	// GPS 对应的地理位置地址
								} else if("WiFi".equals(punch_type)) {
									off_duty_json.put("type", punch_type);
//									off_duty_json.put("wifimac", epa_off_duty.getWifi_mac());
									off_duty_json.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
									off_duty_json.put("wifiname", epa_off_duty.getWifi_name());	// 打卡时WiFi的名字
								}
								
								if(off_duty_time != null) {
									if(off_duty_time.compareTo(ymd_18oclock) < 0) {	// 如果下班卡时间早于18:00，则将当前下班卡视为异常（即下班早退）
										off_duty_json.put("is_normal", false);
										has_abnormal = true;
									} else {	// 如果下班卡时间晚于18:00（包含踩点18:00:00整），则将当前下班卡视为正常
										off_duty_json.put("is_normal", true);
									}
								} else {
									off_duty_json.put("is_normal", null);	// 当前下班卡时间为空（null）
								}
								
								// 将下班卡相关的Json数据放进最后要返回的Json数据中
								today_json.put("off_duty", off_duty_json);
							} else {	// 如果下班卡为空，则设置下班卡的值为null
								today_json.put("off_duty", null);
							}
							
							// 下面是与组织其它打卡相关的Json数据相关的代码
							if(epas_other != null) {
								JSONArray others_json_arr = new JSONArray();	// 用来存放其它打卡记录的Json数组
								
								if(epas_other.size() > 0) {	// 如果其它打卡记录信息条数至少有一条时
									for(EpAtten epa:epas_other) {
										JSONObject json_tmp = new JSONObject();
										Date epa_time = epa.getTime();
										json_tmp.put("time", sdf.format(epa_time));
										String epa_type = epa.getType();
										if("GPS".equals(epa_type)) {
											json_tmp.put("type", epa_type);	// GPS打卡方式
											json_tmp.put("latitude", epa.getLatitude());
											json_tmp.put("longitude", epa.getLongtitude());
											json_tmp.put("gpsaddr", epa.getGps_addr());	// GPS 对应的地理位置地址
										} else if("WiFi".equals(epa_type)) {
											json_tmp.put("type", epa_type);	// WiFi打卡方式
//											json_tmp.put("wifimac", epa.getWifi_mac());
											json_tmp.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
											json_tmp.put("wifiname", epa.getWifi_name());	// 打卡时WiFi的名字
										}
										others_json_arr.add(json_tmp);	// 将其他打卡Json数据一个一个放进Json数组others_json_arr中
									}
								} else {	// 如果其它打卡记录信息条数为0（即没有其它打卡记录）时，返回Json数组设置为空
//									others_json_arr.add(null);
								}
								today_json.put("others", others_json_arr);
							}
							today_json.put("has_abnormal", has_abnormal);
							
							// 所有数据都组织完后，就可以返回最终的Json数据了
//							jsonObj.put(today_ymd_str, today_json);
//							iday_ago_json.put(date_keys[0], today_json);
//							jo_tmp.put(date_keys[0], today_json);
							iday_ago_json = today_json;
							
						} else {	// 如果当天的打卡记录条数为0（即没有打卡记录）时，则当天的返回Json数据设置为空（null）
//							iday_ago_json.put(date_keys[0], null);
							JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
//							empty_record.put("date", null);
							empty_record.put("on_duty", null);
							empty_record.put("off_duty", null);
							empty_record.put("others", null);
							empty_record.put("has_abnormal", true);
							empty_record.put("date", date_keys[0]);	// 设置i天前的空数据的日期为那天的日期
							iday_ago_json = empty_record;
//							jo_tmp.put(date_keys[0], null);
						}
					}
//					jsonObj.put(date_keys[0], iday_ago_json);	// 将组织好后的当天的打卡记录Json数据放进最终要返回的Json对象jsonObj中
//					ja_tmp.add(iday_ago_json);	// 将组织好后的每天的打卡记录Json数据存进前面声明的临时Json数组里
				} else {	// 如果是大于0天前的打卡记录（即表示是历史的打卡记录信息），就按历史大卡判断标准进行处理
					ArrayList<EpAtten> iday_ago_punch_arr = epas_map.get(date_keys[i]);	// 取出i天前的打卡记录数组(i>0，即i=1,2,3,...时)
					if(iday_ago_punch_arr != null) {
						int iday_ago_punch_count = iday_ago_punch_arr.size();
						if(iday_ago_punch_count != 0) {
							EpAtten epa_on_duty = null;	// 用来存放上班卡记录
							EpAtten epa_off_duty = null;	// 用来存放下班卡记录
							ArrayList<EpAtten> epas_other = new ArrayList<EpAtten>();	// 用来存放其它打卡的数组
							
							epa_on_duty = iday_ago_punch_arr.get(0);
							
							if(iday_ago_punch_count > 1) {
								EpAtten last_valid_epa = iday_ago_punch_arr.get(iday_ago_punch_count-1);	// 取出当天最后一条有效的打卡记录
								Date last_valid_epa_time = last_valid_epa.getTime();
								if(last_valid_epa_time != null) {
									/*
									if(now_date.before(ymd_18oclock)) {	// 如果当前查询打卡记录的时间在当天18:00之前，则暂时无法判断下班卡记录，即下班卡为空
										// 此处留空，保持epa_off_duty为null
									} else {	// 如果当前查询打卡记录的时间在当天18:00之后（包含踩点18:00:00整），则以当天最后一次打卡记录作为下班卡
										epa_off_duty = last_valid_epa;
									}
									*/
									
									epa_off_duty = last_valid_epa;	// 对于历史打卡记录，如果打卡记录条数大于1，那么就把那天最后一条打卡记录作为下班卡打卡记录
									
									// 开始往其他打卡数组里放入打卡记录
									for(int j = 1; j < iday_ago_punch_count - 1; j++) {	// 从有效的打卡记录数组中，取出除第一条（数组下标为0）和最后一条（数组下标为valid_count-1）以外的打卡记录，作为其它打卡记录
										epas_other.add(iday_ago_punch_arr.get(j));
									}
									
								}
							}
							
							JSONObject that_day_json = new JSONObject();	// 用来存放i天前那天的打卡记录Json数据的Json对象
							
							that_day_json.put("date", date_keys[i]);	// 打卡日期
							
							// 下面是与组织上班卡相关的Json数据相关的代码
							if(epa_on_duty != null) {
								JSONObject on_duty_json = new JSONObject();	// 用来存放上班卡的Json数据
								
								Date on_duty_time = epa_on_duty.getTime();	// 取出上班卡时间
								on_duty_json.put("time", sdf.format(on_duty_time));	// 上班卡打卡时间（已格式化成“yyyy-MM-dd HH:mm:ss”时间格式）
								String punch_type = epa_on_duty.getType();
								if("GPS".equals(punch_type)) {	// 如果是GPS定位方式打卡，则取经纬度的值返回
									on_duty_json.put("type", punch_type);	// 打卡方式（GPS）
									on_duty_json.put("latitude", epa_on_duty.getLatitude());	// 纬度值
									on_duty_json.put("longitude", epa_on_duty.getLongtitude());	// 经度值
									on_duty_json.put("gpsaddr", epa_on_duty.getGps_addr());	// GPS 对应的地理位置地址
								} else if("WiFi".equals(punch_type)) {	// 如果是WiFi方式打卡，则取MAC地址的值返回
									on_duty_json.put("type", punch_type);	// 打卡方式（WiFi）
//									on_duty_json.put("wifimac", epa_on_duty.getWifi_mac());	// WiFi MAC地址
									on_duty_json.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
									on_duty_json.put("wifiname", epa_on_duty.getWifi_name());	// 打卡时WiFi的名字
								}
								
								if(on_duty_time != null) {
									if(on_duty_time.compareTo(ymd_830clock) <= 0) {	// 如果上班卡打卡时间在当天08:30之前（包含踩点08:30:00整），则视为正常上班卡
										on_duty_json.put("is_normal", true);	// 当前上班打卡正常
									} else {	// 如果上班卡打卡时间在当天08:30之后，则视为异常上班卡（即上班迟到）
										on_duty_json.put("is_normal", false);	// 当前上班打卡异常（上班卡异常即表示上班迟到）
										has_abnormal = true;
									}
								} else {
									on_duty_json.put("is_normal", null);	// 当前上班打卡时间为空（null）
								}
								
								// 将上班卡相关的Json数据放进最后要返回的Json数据中
								that_day_json.put("on_duty", on_duty_json);
							} else {	// 如果上班卡为空，则设置上班卡的值为null
								that_day_json.put("on_duty", null);
							}
							
							// 下面是与组织下班卡相关的Json数据相关的代码
							if(epa_off_duty != null) {
								JSONObject off_duty_json = new JSONObject();	// 用来存放下班卡的Json数据
								
								Date off_duty_time = epa_off_duty.getTime();	// 取出下班卡时间
								off_duty_json.put("time", sdf.format(off_duty_time));
								String punch_type = epa_off_duty.getType();
								if("GPS".equals(punch_type)) {
									off_duty_json.put("type", punch_type);
									off_duty_json.put("latitude", epa_off_duty.getLatitude());
									off_duty_json.put("longitude", epa_off_duty.getLongtitude());
									off_duty_json.put("gpsaddr", epa_off_duty.getGps_addr());	// GPS 对应的地理位置地址
								} else if("WiFi".equals(punch_type)) {
									off_duty_json.put("type", punch_type);
//									off_duty_json.put("wifimac", epa_off_duty.getWifi_mac());
									off_duty_json.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
									off_duty_json.put("wifiname", epa_off_duty.getWifi_name());	// 打卡时WiFi的名字
								}
								
								if(off_duty_time != null) {
									if(off_duty_time.compareTo(ymd_18oclock) < 0) {	// 如果下班卡时间早于18:00，则将当前下班卡视为异常（即下班早退）
										off_duty_json.put("is_normal", false);
										has_abnormal = true;
									} else {	// 如果下班卡时间晚于18:00（包含踩点18:00:00整），则将当前下班卡视为正常
										off_duty_json.put("is_normal", true);
									}
								} else {
									off_duty_json.put("is_normal", null);	// 当前下班卡时间为空（null）
								}
								
								// 将下班卡相关的Json数据放进最后要返回的Json数据中
								that_day_json.put("off_duty", off_duty_json);
							} else {	// 如果下班卡为空，则设置下班卡的值为null
								that_day_json.put("off_duty", null);
							}
							
							// 下面是与组织其它打卡相关的Json数据相关的代码
							if(epas_other != null) {
								JSONArray others_json_arr = new JSONArray();	// 用来存放其它打卡记录的Json数组
								
								if(epas_other.size() > 0) {	// 如果其它打卡记录信息条数至少有一条时
									for(EpAtten epa:epas_other) {
										JSONObject json_tmp = new JSONObject();
										Date epa_time = epa.getTime();
										json_tmp.put("time", sdf.format(epa_time));
										String epa_type = epa.getType();
										if("GPS".equals(epa_type)) {
											json_tmp.put("type", epa_type);	// GPS打卡方式
											json_tmp.put("latitude", epa.getLatitude());
											json_tmp.put("longitude", epa.getLongtitude());
											json_tmp.put("gpsaddr", epa.getGps_addr());	// GPS 对应的地理位置地址
										} else if("WiFi".equals(epa_type)) {
											json_tmp.put("type", epa_type);	// WiFi打卡方式
//											json_tmp.put("wifimac", epa.getWifi_mac());
											json_tmp.put("wifiaddr", LocationUtil.getEP_WIFI_ADDR());	// WiFi 对应的地理位置地址
											json_tmp.put("wifiname", epa.getWifi_name());	// 打卡时WiFi的名字
										}
										others_json_arr.add(json_tmp);	// 将其他打卡Json数据一个一个放进Json数组others_json_arr中
									}
								} else {	// 如果其它打卡记录信息条数为0（即没有其它打卡记录）时，返回Json数组设置为空
//									others_json_arr.add(null);
								}
								that_day_json.put("others", others_json_arr);
							}
							that_day_json.put("has_abnormal", has_abnormal);
							
							// 所有数据都组织完后，就可以返回最终的Json数据了
//							jsonObj.put(today_ymd_str, that_day_json);
//							iday_ago_json.put(date_keys[i], that_day_json);
							iday_ago_json = that_day_json;
//							jo_tmp.put(date_keys[i], that_day_json);
						} else {
//							iday_ago_json.put(date_keys[i], null);
							JSONObject empty_record = new JSONObject();	// 用来存放那天打卡记录为空的Json数据对象
//							empty_record.put("date", null);
							empty_record.put("on_duty", null);
							empty_record.put("off_duty", null);
							empty_record.put("others", null);
							empty_record.put("has_abnormal", true);
							empty_record.put("date", date_keys[i]);	// 设置i天前的空数据的日期为那天的日期
							iday_ago_json = empty_record;
//							jo_tmp.put(date_keys[i], null);
						}
					}
//					jsonObj.put(date_keys[i], iday_ago_json);	// 将组织好后的i天前的打卡记录Json数据放进最终要返回的Json对象jsonObj中
//					ja_tmp.add(iday_ago_json);	// 将组织好后的每天的打卡记录Json数据存进前面声明的临时Json数组里
				}
				ja_tmp.add(iday_ago_json);	// 将组织好后的每天的打卡记录Json数据存进前面声明的临时Json数组里
			}
			
			// 将全部按天分组组织好的打卡数据Json对象放进最终要返回的Json对象jsonObj里
//			jsonObj.put("records", ja_tmp);
			jsonObj.put("records", ja_tmp);
			
			// 开始准备返回最终的Json数据
			jsonObj.put("ret_code", 2);
			jsonObj.put("ret_message", "查询 " + day_range + " 天内打卡记录成功");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	private int getIndexOfAStr(String[] source, String target) {
		if(source == null || target == null) {
			return -1;
		}
		
		if(source.length == 0) {
			return -2;
		}
		
		for(int i=0; i<source.length; i++) {
			if(target.equals(source[i])) {
				return i;	// 返回目标字符串在源字符串数组中的下标位置i
			}
		}
		
		return -3;
	}
	
	public static void main(String[] args) {
		
		
		
		String work_no = "81";
		int len = 6;
		
		EpAttenController epac = new EpAttenController();
		
		String muwn = epac.makeUpWorkNo(work_no, len);
		System.out.println("maked up work_no = " + muwn);
		
		
		
		
		/*
		JSONObject pm_user1 = new JSONObject();
		pm_user1.put("USERID", 111);
		pm_user1.put("Badgenumber", "0961");
		pm_user1.put("Name", "零零碎碎我");
		
		JSONObject pm_user2 = new JSONObject();
		pm_user2.put("USERID", 123);
		pm_user2.put("Badgenumber", "0808");
		pm_user2.put("Name", "444$$$");
		
		JSONObject pm_user3 = new JSONObject();
		pm_user3.put("USERID", 124);
		pm_user3.put("Badgenumber", "1012");
		pm_user3.put("Name", "刴没多久诶看");
		
		JSONObject pm_user4 = new JSONObject();
		pm_user4.put("USERID", 113);
		pm_user4.put("Badgenumber", "0809");
		pm_user4.put("Name", "ppmFddf");
		
		JSONObject pm_user5 = new JSONObject();
		pm_user5.put("USERID", 65);
		pm_user5.put("Badgenumber", "1377");
		pm_user5.put("Name", "可没有");
		
		JSONArray pm_users = new JSONArray();
		pm_users.add(pm_user1);
		pm_users.add(pm_user2);
		pm_users.add(pm_user3);
		pm_users.add(pm_user4);
		pm_users.add(pm_user5);
		
		System.out.println(pm_users);
		*/
		
		
		
		/*
		Date now_time = new Date();
		
		// USERID	CHECKTIME	CHECKTYPE	VERIFYCODE	SENSORID	Memoinfo	WorkCode	sn	UserExtFmt
		JSONObject pcm_data1 = new JSONObject();
		pcm_data1.put("USERID", 102);
		pcm_data1.put("Badgenumber", "0796");
		pcm_data1.put("CHECKTIME", now_time);
		pcm_data1.put("CHECKTYPE", "I");
		pcm_data1.put("VERIFYCODE", 1);
		pcm_data1.put("SENSORID", "456456");
		pcm_data1.put("Memoinfo", "jkjkjllserer23454");
		pcm_data1.put("WorkCode", "pppmmgt56");
		pcm_data1.put("sn", "kkmmttbboopppmmgt56");
		pcm_data1.put("UserExtFmt", 6656);
		
		JSONObject pcm_data2 = new JSONObject();
		pcm_data2.put("USERID", 103);
		pcm_data2.put("Badgenumber", "0795");
		pcm_data2.put("CHECKTIME", now_time);
		pcm_data2.put("CHECKTYPE", "II");
		pcm_data2.put("VERIFYCODE", 1);
		pcm_data2.put("SENSORID", "456456");
		pcm_data2.put("Memoinfo", "jkjkjllserer23454");
		pcm_data2.put("WorkCode", "pppmmgt56");
		pcm_data2.put("sn", "gggrrrttee");
		pcm_data2.put("UserExtFmt", 6656);
		
		JSONObject pcm_data3 = new JSONObject();
		pcm_data3.put("USERID", 101);
		pcm_data3.put("Badgenumber", "0806");
		pcm_data3.put("CHECKTIME", now_time);
		pcm_data3.put("CHECKTYPE", "I");
		pcm_data3.put("VERIFYCODE", 2);
		pcm_data3.put("SENSORID", "456456");
		pcm_data3.put("Memoinfo", "jkjkjllserer23454");
		pcm_data3.put("WorkCode", "pppmmgt56");
		pcm_data3.put("sn", "kkmmttbboopppmmgt56");
		pcm_data3.put("UserExtFmt", 445);
		
		JSONObject pcm_data4 = new JSONObject();
		pcm_data4.put("USERID", 56);
		pcm_data4.put("Badgenumber", "0805");
		pcm_data4.put("CHECKTIME", DateUtil.calcXDaysAfterADate(1, now_time));
		pcm_data4.put("CHECKTYPE", "I");
		pcm_data4.put("VERIFYCODE", 0);
		pcm_data4.put("SENSORID", "323232ff");
		pcm_data4.put("Memoinfo", "lllmm解决");
		pcm_data4.put("WorkCode", "pppmmgt56");
		pcm_data4.put("sn", "2222223333000");
		pcm_data4.put("UserExtFmt", 7788920);
		
		JSONObject pcm_data5 = new JSONObject();
		pcm_data5.put("USERID", 56);
		pcm_data5.put("Badgenumber", "4222");
		pcm_data5.put("CHECKTIME", DateUtil.calcXDaysAfterADate(1, now_time));
		pcm_data5.put("CHECKTYPE", "I");
		pcm_data5.put("VERIFYCODE", 0);
		pcm_data5.put("SENSORID", "323232ff");
		pcm_data5.put("Memoinfo", "lllmm解决");
		pcm_data5.put("WorkCode", "pppmmgt56");
		pcm_data5.put("sn", "66666669999");
		pcm_data5.put("UserExtFmt", 7788920);
		
		JSONArray jarr = new JSONArray();
		jarr.add(pcm_data1);
		jarr.add(pcm_data2);
		jarr.add(pcm_data3);
		jarr.add(pcm_data4);
		jarr.add(pcm_data5);
		
		System.out.println(jarr);
		*/
		
		
	}
	
	@RequestMapping(params = "gotoExportAttendDatas")
	public String gotoExportAttendsData2ExcelPage(HttpServletRequest request) {
		return "exportData/ExportAttendDatas2Excel";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "exportAttendDatas")
	@ResponseBody
	public void getAllEpAttenExportDatas(HttpServletRequest request,HttpServletResponse response,@RequestParam("importFile") MultipartFile multipartFile) {
		try {
			String startDateString = request.getParameter("startdate");
			String endDateString = request.getParameter("enddate");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = format.parse(startDateString);
			Date endDate = format.parse(endDateString);
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
			String excelPath = DbToExcelUtil.saveFileToServer(request,multipartFile, "\\filesOut\\Upload");

			List<String> emails = new ReadExcel().readEmailsFromExcel(excelPath);
			if (emails != null && !emails.isEmpty()) {
				String serverPath = epAttenService.getAllEpAttenExportDatas(emails, startDate, endDate);
				if (!StringUtils.isEmpty(serverPath)) {
					DbToExcelUtil.downLoadFile(request, response, serverPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据工号获取所有工作日内的未处理的异常打卡信息
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUnHandledExceptionAttenDatas", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getUnHandledExceptionAttenDatasByWorkNum(HttpServletRequest request){
		String workNum = request.getParameter("work_no");
		JSONObject obj = new JSONObject();
		if(StringUtils.isEmpty(workNum)){
			obj.put("ret_code", -1);
			obj.put("ret_messge", "工号为空");
			return JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue);
		}

		EpUser ep_user = epUserService.getEpUserByWorkNum(workNum);
		if (ep_user == null) {
			obj.put("ret_code", -2);
			obj.put("ret_message", "不存在该用户！");
			return JSONObject.toJSONString(obj,SerializerFeature.WriteMapNullValue);
		}

		JSONArray jsonArray = new JSONArray();
		Date startDate = attenExceptionService.getLastTimeByUid(ep_user.getId());
		Date endDate = new Date();
		if(startDate == null){
			startDate = DateUtil.parse2date(2, EpAttenUtil.DEFAULT_START_DATE);
		}else{
			attenExceptionService.deleteAttenExceptionByUidAndDate(ep_user.getId(), startDate);//先把开始这天的数据删除，避免重复
			
			List<HashMap<String, Date>> allDatas = attenExceptionService.getAllAttenExceptionsByUid(ep_user.getId());
			if(allDatas != null && !allDatas.isEmpty()){
				for (HashMap<String, Date> hashMap : allDatas) {
					JSONObject object = new JSONObject();
					object.put("date", DateUtil.formatDate(1, hashMap.get("date")));
					object.put("on_time", DateUtil.formatDate(2, hashMap.get("on_time")));
					object.put("on_normal", EpAttenUtil.isOnTimeNormal(hashMap.get("on_time")));
					object.put("off_time", DateUtil.formatDate(2, hashMap.get("off_time")));
					object.put("off_normal", EpAttenUtil.isOffTimeNormal(hashMap.get("off_time")));
					jsonArray.add(object);
				}
			}
		}
		
		// 根据获取到的用户id，以及所提供的开始日期、结束日期时间范围，得到该用户全部有效的打卡记录信息
		List<EpAtten> epas = epAttenService.getValidEpAttenByEpUidAndStartDateAndEndDate(ep_user.getId(), startDate, endDate);
		
		//从所有数据的数据源里筛选出每天只有2条打卡数据
		TreeMap<String, List<EpAtten>> validAttendDatas = EpAttenUtil.getValidAttendDatas(epas);
		
		//获取开始日期，结束日期之间的所有休息日期
		List<Date> restDates = restCalendarService.getDatesBetweenStartAndEndByType(startDate, endDate,ep_user.getType());
		
		//获取开始，结束日期之间所有的工作日期
		List<Date> allWorkDates = DateUtil.getAllWorkDates(startDate, endDate, restDates);
		
		List<HashMap<String,Object>> exceptionAttens = new ArrayList<>();
		for(Date date : allWorkDates){
			String string = DateUtil.formatDate(1, date).trim();
			HashMap<String,Object> map = new HashMap<String, Object>();
			JSONObject object = new JSONObject();
			
			if(string.equalsIgnoreCase(DateUtil.formatDate(1, endDate).trim())){//刚好是今天
				if(( string + EpAttenUtil.ON_DUTY_HH_MM).compareToIgnoreCase(DateUtil.formatDate(2, endDate)) < 0){
					if(validAttendDatas.containsKey(string)){
						if(( string + EpAttenUtil.ON_DUTY_HH_MM).compareToIgnoreCase(DateUtil.formatDate(2, validAttendDatas.get(string).get(0).getTime())) < 0){
							List<EpAtten> list = validAttendDatas.get(string);
							map.put("ep_uid", ep_user.getId());
							map.put("date",date);
							map.put("on_time", list.get(0).getTime());
							
							object.put("date", string);
							object.put("on_time", DateUtil.formatDate(2, list.get(0).getTime()));
							object.put("on_normal", EpAttenUtil.isOnTimeNormal(list.get(0).getTime()));
							
							if(list.size() == 1){
								object.put("off_time", null);
								object.put("off_normal", false);
								map.put("off_time", null);
							}else{
								object.put("off_time", DateUtil.formatDate(2, list.get(1).getTime()));
								object.put("off_normal", EpAttenUtil.isOffTimeNormal(list.get(1).getTime()));
								map.put("off_time", list.get(1).getTime());
							}
							attenExceptionService.deleteAttenExceptionByUidAndDate(ep_user.getId(), date);
							exceptionAttens.add(map);
							jsonArray.add(object);
						}
					}else{
						map.put("ep_uid", ep_user.getId());
						map.put("date",date);
						map.put("on_time", null);
						map.put("off_time", null);
						
						object.put("date", string);
						object.put("on_time", null);
						object.put("on_normal", false);
						object.put("off_time", null);
						object.put("off_normal", false);
						
						attenExceptionService.deleteAttenExceptionByUidAndDate(ep_user.getId(), date);
						exceptionAttens.add(map);
						jsonArray.add(object);
					}
				}
			}else{
				if(validAttendDatas.containsKey(string)){
					List<EpAtten> list = validAttendDatas.get(string);
					boolean isNormal = EpAttenUtil.isNormalAttend(string,list);
					if(!isNormal){
						map.put("ep_uid", ep_user.getId());
						map.put("date",date);
						map.put("on_time", list.get(0).getTime());
						
						object.put("date", string);
						object.put("on_time", DateUtil.formatDate(2, list.get(0).getTime()));
						object.put("on_normal", EpAttenUtil.isOnTimeNormal(list.get(0).getTime()));
						if(list.size() == 1){
							object.put("off_time", null);
							object.put("off_normal", false);
							map.put("off_time", null);
						}else{
							object.put("off_time", DateUtil.formatDate(2, list.get(1).getTime()));
							object.put("off_normal", EpAttenUtil.isOffTimeNormal(list.get(1).getTime()));
							map.put("off_time", list.get(1).getTime());
						}
						exceptionAttens.add(map);
						jsonArray.add(object);
					}
				}else{
					map.put("ep_uid", ep_user.getId());
					map.put("date",date);
					map.put("on_time", null);
					map.put("off_time", null);
					
					object.put("date", string);
					object.put("on_time", null);
					object.put("on_normal", false);
					object.put("off_time", null);
					object.put("off_normal", false);
					exceptionAttens.add(map);
					jsonArray.add(object);
				}
			}
		}
		obj.put("datas", jsonArray);
		obj.put("ret_code", 1);
		obj.put("ret_message", "获取数据成功！");
		
		if(!exceptionAttens.isEmpty()){
			attenExceptionService.insertAttenExceptions(exceptionAttens);
		}
		
		return JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue);
	}

	/**
	 * 根据工号获取所有工作日的已处理的异常打卡信息
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getHandledExceptionAttenDatas", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getHandledExceptionAttenDatasByWorkNum(HttpServletRequest request){
		String workNum = request.getParameter("work_no");
		JSONObject obj = new JSONObject();
		if(StringUtils.isEmpty(workNum)){
			obj.put("ret_code", -1);
			obj.put("ret_messge", "工号为空");
			return JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue);
		}

		EpUser ep_user = epUserService.getEpUserByWorkNum(workNum);
		if (ep_user == null) {
			obj.put("ret_code", -2);
			obj.put("ret_message", "不存在该用户！");
			return JSONObject.toJSONString(obj,SerializerFeature.WriteMapNullValue);
		}
		
		JSONArray jsonArray = new JSONArray();
		List<HashMap<String, Date>> allDatas = attenExceptionService.getAllHandledAttenExceptionsByUid(ep_user.getId());
		if(allDatas != null && !allDatas.isEmpty()){
			for (HashMap<String, Date> hashMap : allDatas) {
				JSONObject object = new JSONObject();
				object.put("date", DateUtil.formatDate(1, hashMap.get("date")));
				object.put("on_time", DateUtil.formatDate(2, hashMap.get("on_time")));
				object.put("on_normal", EpAttenUtil.isOnTimeNormal(hashMap.get("on_time")));
				object.put("off_time", DateUtil.formatDate(2, hashMap.get("off_time")));
				object.put("off_normal", EpAttenUtil.isOffTimeNormal(hashMap.get("off_time")));
				jsonArray.add(object);
			}
		}else{
			obj.put("ret_code", 0);
			obj.put("ret_message", "无数据！");
			return JSONObject.toJSONString(obj,SerializerFeature.WriteMapNullValue);
		}
		
		obj.put("datas", jsonArray);
		obj.put("ret_code", 1);
		obj.put("ret_message", "获取数据成功！");
		return JSONObject.toJSONString(obj,SerializerFeature.WriteMapNullValue);
	}
	
	
	/**
	 * 【批量】
	 * 接收远程打卡机的打卡数据，并合并到移动打卡记录表中
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "pushMachPunchDataCHECKINOUT", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String fetchRemoteMachPunchDataCHECKINOUTJson(HttpServletRequest request, @RequestBody(required = false) JSONObject jo) {
		JSONObject jsonObj = new JSONObject();
		
		String punch_datas = request.getParameter("punch_datas");	// 用来接收批量打卡机数据
		
		if(jo != null) {
			Object pdo = jo.get("punch_datas");
			punch_datas = pdo.toString();
		}
		
		/*
		String mach_userid = request.getParameter("USERID");	// 打卡机上的用户id
		String check_time = request.getParameter("CHECKTIME");
		String check_type = request.getParameter("CHECKTYPE");
		String verify_code = request.getParameter("VERIFYCODE");
		String sensor_id = request.getParameter("SENSORID");
		String memo_info = request.getParameter("Memoinfo");
		String work_code = request.getParameter("WorkCode");
		String mach_sn = request.getParameter("sn");	// 打卡机序列号
		String user_ext_fm = request.getParameter("UserExtFm");
		
		if(StringUtils.isBlank(mach_userid) || StringUtils.isBlank(check_time) || StringUtils.isBlank(check_type) || StringUtils.isBlank(verify_code) || StringUtils.isBlank(mach_sn)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		*/
		
		JSONArray ja_params = null;	// 用来准备存放最终要进行处理的JSON数组参数
		
		if(StringUtils.isBlank(punch_datas)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "必填参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		} else {
			String user_infos_trim = punch_datas.trim();
			JSONArray parse_ja = null;
			try {
				parse_ja = JSONArray.parseArray(user_infos_trim);
			} catch (Exception e) {
				logger.error("转换打卡机打卡数据的JSON数组字符串时出现异常");
				jsonObj.put("ret_code", -2);
				jsonObj.put("ret_message", "转换打卡机打卡数据的JSON数组字符串时出现了异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			ja_params = parse_ja;
		}
		
		Date fetch_time = new Date();	// 用于设定远程推送过来的每条打卡机打卡记录的接收时间
		ArrayList<EpAtten> valid_epas = new ArrayList<EpAtten>();
		TreeSet<String> invalid_badgenumbers = new TreeSet<String>();	// 用于存放无效的/数据库中不存在的/其他异常的工号（注：这里用Set而不用List主要是为了避免存入重复的工号）
		
		for(Object obj : ja_params) {
			if(obj instanceof JSONObject) {
				JSONObject mcio_json = (JSONObject) obj;
				
				Integer mach_userid = null;	// 打卡机上的用户id
				String badgenumber = null;	// 打卡机用户工号信息
				Date check_time = null;
				String check_type = null;
				Integer verify_code = null;
				String sensor_id = null;
				String memo_info = null;
				String work_code = null;
				String mach_sn = null;	// 打卡机序列号
				Integer user_ext_fm = null;
				
				try {
					mach_userid = (Integer) mcio_json.get("userid");
					badgenumber = mcio_json.getString("badge_number");
					check_time = mcio_json.getDate("check_time");
					check_type = mcio_json.getString("check_type");
					verify_code = mcio_json.getInteger("verify_code");
					sensor_id = mcio_json.getString("sensor_id");
					memo_info = mcio_json.getString("memo_info");
					work_code = mcio_json.getString("work_code");
					mach_sn = mcio_json.getString("sn");
					user_ext_fm = mcio_json.getInteger("user_ext_fm");
					
				} catch (Exception e) {
					logger.error("提取打卡机打卡数据Json数据时出现异常");
					e.printStackTrace();
					continue;
				}
				
				if(mach_userid == null || StringUtils.isBlank(badgenumber) || check_time == null || StringUtils.isBlank(check_type) || verify_code == null || StringUtils.isBlank(mach_sn)) {
					logger.error("打卡机打卡数据中有部分必要字段数据为空或null异常：mcio_json = " + mcio_json);
					continue;
				}
				
//				if(!"I".equals(check_type)) {	// 假设，如果考勤状态字段(CHECKTYPE)的值不是“I”，则认为当前这条打卡机上的打卡记录数据异常，直接跳过，继续下一条
//					continue;
//				}
				
				/*
				EpUser epu_query = new EpUser();
				epu_query.setMach_userid(mach_userid);
				List<EpUser> epus_ret = epUserService.getEpUserByCriteria(epu_query);
				
				if(epus_ret == null || epus_ret.size() == 0) {
					logger.error("根据打卡机用户userid（" + mach_userid + "）获取公司用户id出现异常，或未找到匹配的用户信息");
					continue;
				}
				
				if(epus_ret.size() > 1) {
					logger.error("根据打卡机用户userid获取公司用户id，找到多个匹配的用户信息，无法确认对应哪个公司用户id。 + epus_ret.size() = " + epus_ret.size());
					logger.error("所有已找到匹配该打卡机用户userid（" + mach_userid + "）的公司用户信息：epus_ret = " + epus_ret);
					continue;
				}
				
				EpUser epu_one = epus_ret.get(0);
				Integer epu_one_id = epu_one.getId();
				*/
				
				int ret_epuid = getEpUidByWorkNoMach(badgenumber);
				if(ret_epuid <= 0) {
					logger.error("根据工号获取公司用户id出现异常，ret_epuid = " + ret_epuid + "，工号：" + badgenumber);
					invalid_badgenumbers.add(badgenumber);
					continue;
				}
				
				EpAtten epa_tmp = new EpAtten();
				
				epa_tmp.setFetch_time(fetch_time);
				epa_tmp.setEp_uid(ret_epuid);
				if(verify_code == 1) {	// TODO: 假设，验证方式参数verify_code等于1时，表示是用的IC卡打卡
//					epa_tmp.setType("IC_Card");
					epa_tmp.setType("fingerprint");
				} else if(verify_code == 2) {	// TODO: 假设，验证方式参数verify_code等于2时，表示是用的指纹打卡
//					epa_tmp.setType("fingerprint");
					epa_tmp.setType("IC_Card");
				} else {
					// TODO: 暂时未知的打卡机打卡方式。。。
					epa_tmp.setType("<UNKNOWN>VERIFYCODE=" + verify_code + "");
				}
				if("I".equals(check_type)) {	// TODO: 假设，如果考勤状态字段(CHECKTYPE)的值是“I”，则认为当前这条打卡机上的打卡记录数据正常/有效，否则，认为异常/无效。
					epa_tmp.setIs_valid(true);
				} else {
					epa_tmp.setIs_valid(false);
				}
				epa_tmp.setTime(check_time);
				epa_tmp.setPlatform("PunchCardMachine");	// 设置打卡记录平台类型为"PunchCardMachine"（即“打卡机”类型）
				epa_tmp.setMach_sn(mach_sn);
				
				valid_epas.add(epa_tmp);
				
			} else {
				logger.error("出现非JSONObject的打卡机打卡数据异常");
				continue;
			}
		}
		
		JSONArray ja_invalid_bns = new JSONArray();	// 将无效的/异常的工号返回给远端推送方
		ja_invalid_bns.addAll(invalid_badgenumbers);
		jsonObj.put("invalid_badgenumbers", ja_invalid_bns);
		
		if(valid_epas.size() == 0) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "远程推送的打卡数据中没有有效的打卡数据");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int batch_add_ret = epAttenService.batchAddEpAttens(valid_epas);
		
		if(batch_add_ret <= 0) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("batch_add_ret", batch_add_ret);
			jsonObj.put("ret_message", "远程推送打卡数据失败 + batch_add_ret = " + batch_add_ret);
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		jsonObj.put("ret_code", 1);
		jsonObj.put("batch_add_ret", batch_add_ret);
		jsonObj.put("ret_message", "远程推送打卡数据成功 + batch_add_ret = " + batch_add_ret);
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	
	/**
	 * 【批量】
	 * 接收远程打卡机的用户信息，并对应到公司用户表中
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "pushMachUserInfo", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String fetchRemoteMachUserInfoJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		String user_infos = request.getParameter("user_infos");	// 用来接收批量打卡机用户信息数据
		
//		String mach_userid = request.getParameter("USERID");	// 打卡机上的用户id
//		String check_time = request.getParameter("Name");
		
		JSONArray ja_params = null;	// 用来准备存放最终要进行处理的JSON数组参数
		
		if(StringUtils.isBlank(user_infos)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		} else {
			String user_infos_trim = user_infos.trim();
			JSONArray parse_ja = null;
			try {
				parse_ja = JSONArray.parseArray(user_infos_trim);
			} catch (Exception e) {
				logger.error("转换打卡机用户信息的JSON数组字符串时出现异常");
				jsonObj.put("ret_code", -2);
				jsonObj.put("ret_message", "转换打卡机用户信息的JSON数组字符串时出现了异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			ja_params = parse_ja;
		}
		
		ArrayList<JSONObject> user_info_arr = new ArrayList<JSONObject>();
		ArrayList<EpUser> valid_epu_upds = new ArrayList<EpUser>();
		
		for(Object obj : ja_params) {
			if(obj instanceof JSONObject) {
				JSONObject user_info_json = (JSONObject) obj;
				user_info_arr.add(user_info_json);
				
				Integer mach_userid = null;
				String badgenumber = null;	// 打卡机用户工号信息
				String mach_name = null;
				
				try {
					mach_userid = (Integer) user_info_json.get("USERID");
					badgenumber = (String) user_info_json.get("Badgenumber");
					mach_name = (String) user_info_json.get("Name");
				} catch (Exception e) {
					logger.error("提取打卡机用户信息Json数据时出现异常");
					e.printStackTrace();
					continue;
				}
				
				if(mach_userid == null || StringUtils.isBlank(badgenumber)) {
					logger.error("打卡机用户信息中有部分必要字段数据为null异常：user_info_json = " + user_info_json);
					continue;
				}
				
				int ret_epuid = getEpUidByWorkNo(badgenumber);
				if(ret_epuid <= 0) {
					logger.error("根据工号获取公司用户id出现异常，ret_epuid = " + ret_epuid + "，工号：" + badgenumber);
					continue;
				}
				
				EpUser epu_upd = new EpUser();
				epu_upd.setId(ret_epuid);
				epu_upd.setMach_userid(mach_userid);
				valid_epu_upds.add(epu_upd);
				
			} else {
				logger.error("出现非JSONObject的打卡机用户信息数据");
				continue;
			}
		}
		
		if(valid_epu_upds.size() == 0) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "有效的打卡机用户信息数据个数为0");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		int batch_mod_ret = epUserService.batchModifyEpUsersUnderSomeConditions(valid_epu_upds);
		
		if(batch_mod_ret <= 0) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "批量更新公司用户信息出现异常 + batch_mod_ret = " + batch_mod_ret);
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "打卡机用户信息数据推送成功 + batch_mod_ret = " + batch_mod_ret);
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
}

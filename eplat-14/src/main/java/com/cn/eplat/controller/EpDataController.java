package com.cn.eplat.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zsl.testmybatis.TestEpUserDao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.b510b.excel.ReadExcel;
import com.b510b.excel.vo.Employee;
import com.cn.eplat.model.DeptClue;
import com.cn.eplat.model.DeptIdClue;
import com.cn.eplat.model.DeptUser;
import com.cn.eplat.model.EpData;
import com.cn.eplat.model.EpDept;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.model.PushFilterLog;
import com.cn.eplat.model.PushToHw;
import com.cn.eplat.service.IDeptClueService;
import com.cn.eplat.service.IDeptIdClueService;
import com.cn.eplat.service.IDeptUserService;
import com.cn.eplat.service.IEpAttenService;
import com.cn.eplat.service.IEpDataService;
import com.cn.eplat.service.IEpDeptService;
import com.cn.eplat.service.IEpUserService;
import com.cn.eplat.service.IPushFilterLogService;
import com.cn.eplat.service.IPushToHwService;
import com.cn.eplat.utils.DateUtil;
import com.cn.eplat.utils.FileUtil;
import com.cn.eplat.utils.Files_Helper_DG;
import com.test.maintest.DeptIdClueUtil;

@Controller
@RequestMapping("/epDataController")
// TODO: 通过Excel表格批量导入用户信息等数据到本地数据库
// TODO: Dubbox 后台服务框架（分布式REST）
// TODO: 微服务框架（支持多版本服务接口）
// TODO:（2016.11.17）手机端通过URL下载pdf、视频等文件，PC端通过管理页面管理这些文件
// TODO:（2016.11.18）参考ECP开源项目中的组织机构管理、用户管理模块
// TODO:（2016.11.18）Word、Excel、PDF等格式文档转换、分页传给移动端进行展示
// TODO:（2016.11.18）iLearning功能模块中，支持视频课程
public class EpDataController {
	
	private static Logger logger = Logger.getLogger(EpDataController.class);
	
	@Resource
	private IEpUserService epUserService;
	@Resource
	private IEpDataService epDataService;
	@Resource
	private IDeptClueService deptClueService;
	@Resource
	private IDeptUserService deptUserService;
	@Resource
	private IEpDeptService epDeptService;
	@Resource
	private IDeptIdClueService deptIdClueService;
	@Resource
	private IPushFilterLogService pushFilterLogService;
	@Resource
	private IEpAttenService epAttenService;
	@Resource
	private IPushToHwService pushToHwService;
	
	@RequestMapping(params = "getData", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getDataJson(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		if(session == null) {
			System.out.println("当前session为空");
		} else {
			System.out.println("当前session不为空，session id = " + session.getId());
		}
		
		JSONObject jsonObj = new JSONObject();
		
		String appid = request.getParameter("appid");
		String user_code = request.getParameter("user_code");
		
		if(StringUtils.isEmpty(appid) || StringUtils.isEmpty(user_code)) {
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "参数为空");
			return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		EpUser epu = epUserService.getEpUserByCode(user_code);
		if(epu == null) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "未找到该用户代码对应的用户");
			return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(epu.getRole_id() <= 0) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "当前用户无对应角色");
			return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		EpData epd = epDataService.getEpDataByAppidAndRoleId(appid, epu.getRole_id());
		if(epd == null) {
			jsonObj.put("ret_code", -3);
			jsonObj.put("ret_message", "未找到所需的数据");
			return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		if(StringUtils.isBlank(epd.getFile_path())) {
			jsonObj.put("ret_code", -4);
			jsonObj.put("ret_message", "数据文件路径路径为空");
			return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		try {
			String fake_data = FileUtil.readFile(epd.getFile_path());
			Object json_data = JSON.parse(fake_data);
			jsonObj.put("data", json_data);
			jsonObj.put("ret_code", 1);
			jsonObj.put("ret_message", "获取数据成功");
			return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		} catch (IOException e) {
			e.printStackTrace();
			jsonObj.put("ret_code", -5);
			jsonObj.put("ret_message", "读取数据文件异常");
			return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		} catch (Exception e) {
			System.out.println("=============>>>>>>>>>>>>>>");
			e.printStackTrace();
			System.out.println("=============>>>>>>>>>>>>>>");
			jsonObj.put("ret_code", -6);
			jsonObj.put("ret_message", "解析数据文件异常");
			return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
//		return JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
	@RequestMapping(params = "gotoEmpInfoUpload")
	public String gotoFileUploadPage(HttpServletRequest request) {
		return "fileUpload/EmpInfoFileUpload";
	}
	
	@RequestMapping(params = "gotoEmpInfoUpload_someNewEmps")
	public String gotoFileUploadPage_someNewEmps(HttpServletRequest request) {
		return "fileUpload/EmpInfoFileUpload_someNewEmps";
	}
	
	@RequestMapping(params = "gotoEmpInfoUpload_ep")
	public String gotoFileUploadPage_ep(HttpServletRequest request) {
		return "fileUpload/EpEmpInfoFileUpload";
	}
	
	@RequestMapping(params = "gotoEmpInfoUpload_ep_extra")
	public String gotoFileUploadPage_ep_extra(HttpServletRequest request) {
		return "fileUpload/ExtraEpEmpInfoFileUpload";
	}
	
	@RequestMapping(params = "gotoHwEmpInfoUpload")
	public String gotoFileUploadPage_hw_emp(HttpServletRequest request) {
		return "fileUpload/HwEmpInfoFileUpload";
	}
	
	@RequestMapping(params = "impEmpInfo", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String impEmployeeInfo(HttpServletRequest request, @RequestParam("emp_info_xls") MultipartFile multipartFile) {
		// 调用保存文件的帮助类进行保存文件，并返回文件的相对路径
		String xls_file_path = Files_Helper_DG.FilesUpload_transferTo_spring(request, multipartFile, "\\filesOut\\Upload");
		JSONObject imp_ret = impEmpInfo(xls_file_path);
		
		return imp_ret.toJSONString();
	}
	
	@RequestMapping(params = "impEmpInfo_someNewEmps", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String impEmployeeInfo_someNewEmps(HttpServletRequest request, @RequestParam("some_new_emps_info_xls") MultipartFile multipartFile) {
		// 调用保存文件的帮助类进行保存文件，并返回文件的相对路径
		String xls_file_path = Files_Helper_DG.FilesUpload_transferTo_spring(request, multipartFile, "\\filesOut\\Upload");
		JSONObject imp_ret = impSomeNewEmpInfo(xls_file_path);
		
		return imp_ret.toJSONString();
	}
	
	
	@RequestMapping(params = "impEmpInfo_ep", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String impEmployeeInfo_ep(HttpServletRequest request, @RequestParam("ep_emp_info_xls") MultipartFile multipartFile) {
		// 调用保存文件的帮助类进行保存文件，并返回文件的相对路径
		String xls_file_path = Files_Helper_DG.FilesUpload_transferTo_spring(request, multipartFile, "\\filesOut\\Upload");
		JSONObject imp_ret = impEmpInfo_ep(xls_file_path);
		
		return imp_ret.toJSONString();
	}
	
	@RequestMapping(params = "impEmpInfo_extra_ep", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String impEmployeeInfo_extra_ep(HttpServletRequest request, @RequestParam("ep_emp_info_extra_xls") MultipartFile multipartFile) {
		// 调用保存文件的帮助类进行保存文件，并返回文件的相对路径
		String xls_file_path = Files_Helper_DG.FilesUpload_transferTo_spring(request, multipartFile, "\\filesOut\\Upload");
		JSONObject imp_ret = impEmpInfo_ep_extra(xls_file_path);
		
		return imp_ret.toJSONString();
	}
	
	@RequestMapping(params = "impHwEmpInfo", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String impHwEmployeeInfo(HttpServletRequest request, @RequestParam("hw_emp_info_xlsm") MultipartFile multipartFile) {
		// 调用保存文件的帮助类进行保存文件，并返回文件的相对路径
		String xls_file_path = Files_Helper_DG.FilesUpload_transferTo_spring(request, multipartFile, "\\filesOut\\Upload");
		JSONObject imp_ret = impHwEmpInfo(xls_file_path);
		
		return imp_ret.toJSONString();
	}
	
	
	public int filterPush2HwAttenOperation(Date start, Date end) {
		
		EpUser epu_query = new EpUser();
		epu_query.setPush2hw_atten(true);
		/*
		// TODO: 临时代码 (Start)
		epu_query.setId(913); 	// 符边正 的ep_uid=913
		// TODO: 临时代码 (End)
		*/
		List<EpUser> epus_push2hw = epUserService.getEpUserByCriteria(epu_query);	// 1. 首先查出准备推送华为考勤系统的人员信息
		List<EpUser> epus_valid = new ArrayList<EpUser>();	// 有效的人员信息数组列表
		TreeMap<Integer, EpUser> epus_valid_map = new TreeMap<Integer, EpUser>();
		List<EpUser> epus_invalid = new ArrayList<EpUser>();	// 无效的人员信息数组列表
		
		JSONArray invalid_epus_ja = new JSONArray();
		
		for(EpUser epu : epus_push2hw) {
			// 有效的人员信息必须满足id为正整数，工号、姓名、身份证号非空
			if(epu.getId() != null && epu.getId() > 0 && StringUtils.isNotBlank(epu.getWork_no()) && StringUtils.isNotBlank(epu.getName()) && StringUtils.isNotBlank(epu.getIdentity_no())) {
				epus_valid.add(epu);
				epus_valid_map.put(epu.getId(), epu);
			} else {
				epus_invalid.add(epu);
			}
		}
		
		if(epus_invalid.size() > 0) {
			invalid_epus_ja.addAll(epus_invalid);
		}
		
		if(epus_valid.size() == 0) {
			return -3;
		}
		
		// TODO: 2. 然后，从筛选日志中查出上次筛选出现异常的数据，进行重新筛选并修改状态...
		
		
		
		// 3. 从筛选日志表中查出已做过筛选的考勤日期（后面要从给定的日期范围里排除掉这部分已做过筛选的日期）
		List<Date> filtered_dates = pushFilterLogService.getFilteredDates();
		if(filtered_dates == null) {
			return -4;
		}
		
		/*
		// TODO: 临时代码 (Start)
		filtered_dates = null;
		start = DateUtil.parse2date(1, "2017-03-08");
		end = DateUtil.parse2date(1, "2017-03-08");
		// TODO: 临时代码 (End)
		*/
		
		List<Date> need_dates = DateUtil.calcDatesExcludeGivenDatesByStartEndDate(filtered_dates, start, end);
		
		if(need_dates == null) {
			return -5;
		}
		
		if(need_dates.size() == 0) {
			return -6;
		}
		
		List<HashMap<String, Object>> results = epAttenService.getFirstAndLastPunchTimeValidByDatesAndEpUidsBeforeToday(need_dates, epus_valid);
		
		if(results == null) {
			return -7;
		}
		
		if(results.size() == 0) {
			return -8;
		}
		
		List<PushToHw> pthws = new ArrayList<PushToHw>();
		List<PushFilterLog> filtered_valid = new ArrayList<PushFilterLog>();
		List<PushFilterLog> filtered_invalid = new ArrayList<PushFilterLog>();
		List<PushFilterLog> filtered_all = new ArrayList<PushFilterLog>();
		
		Date filter_time = new Date();
		
		for(HashMap<String, Object> result : results) {
			Object ep_uid_obj = result.get("ep_uid");
			Object punch_date_obj = result.get("punch_date");
			Object first_punch_time_obj = result.get("first_punch_time");
			Object last_punch_time_obj = result.get("last_punch_time");
			
			PushFilterLog pfl = new PushFilterLog();
			pfl.setFilter_time(filter_time);
			
			if(ep_uid_obj instanceof Long) {
				Long ep_uid = (Long) ep_uid_obj;
				pfl.setEp_uid(ep_uid.intValue());
				
				if(punch_date_obj instanceof String && first_punch_time_obj instanceof Timestamp && last_punch_time_obj instanceof Timestamp) {
					Date punch_date = DateUtil.parse2date(1, (String) punch_date_obj);
					Date first_punch_time = (Timestamp) first_punch_time_obj;
					Date last_punch_time = (Timestamp) last_punch_time_obj;
					
					pfl.setDayof_date(punch_date);
					
					EpUser epUser = epus_valid_map.get(ep_uid.intValue());
					if(epUser != null) {
						PushToHw pthw = new PushToHw();
						pthw.setEp_uid(ep_uid.intValue());
						pthw.setDayof_date(punch_date);
						pthw.setOn_duty_time(first_punch_time);
						if(first_punch_time.equals(last_punch_time) || first_punch_time.after(last_punch_time)) {
							pthw.setOff_duty_time(null);
						} else {
							pthw.setOff_duty_time(last_punch_time);
						}
						pthw.setName(epUser.getName());
						pthw.setId_no(epUser.getIdentity_no());
						// 星期几。。。
						pthw.setDayof_week(DateUtil.getDayOfWeekByDate(punch_date, 1));
						pthws.add(pthw);
						
						pfl.setStatus("filter_success");
						pfl.setDescribe("成功筛选出该用户的考勤信息");
						filtered_valid.add(pfl);
					} else {
						pfl.setStatus("filter_error_1");
						pfl.setDescribe("根据用户id获取用户信息为null");
						filtered_invalid.add(pfl);
					}
				} else {
					pfl.setStatus("filter_error_2");
					pfl.setDescribe("该用户的考勤数据日期非日期类型");
					filtered_invalid.add(pfl);
				}
			} else {
				pfl.setStatus("filter_error_3");
				pfl.setDescribe("该用户的用户id为null异常");
				filtered_invalid.add(pfl);
			}
		}
		
		if(pthws.size() == 0) {
			logger.error("准备推送华为考勤数据条数为0");
		} else {
			Integer batch_add_pthws = pushToHwService.batchAddPushToHws(pthws);
			
			if(batch_add_pthws == null || batch_add_pthws < 0) {
				return -9;
			}
		}
		
		filtered_all.addAll(filtered_invalid);
		filtered_all.addAll(filtered_valid);
		
		if(filtered_all.size() == 0) {
			logger.error("筛选日志数据条数为0");
		} else {
			int batch_add_pfls = pushFilterLogService.batchAddPushFilterLogs(filtered_all);
			
			if(batch_add_pfls < 0) {
				return -10;
			}
		}
		
		return 1;
	}
	
	
	/**
	 * 根据给定日期范围，对需要推送华为考勤系统的用户的考勤信息进行筛选，并写入到单独的一张推送华为考勤信息表中去
	 * 
	 */
	@RequestMapping(params = "filterPush2HwAttens", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String filterPush2HwAttensJson(HttpServletRequest request) {
		JSONObject json_ret = new JSONObject();
		
		// 从接收到的请求中获得传入参数
//		String work_no = request.getParameter("work_no");
		String date_range_start = request.getParameter("start");	// 开始日期
		String date_range_end = request.getParameter("end");	// 结束日期
		
		// 对传入参数进行最基本的校验（如非空校验）
		if(StringUtils.isBlank(date_range_start) || StringUtils.isBlank(date_range_end)) {
			json_ret.put("ret_code", -1);
			json_ret.put("ret_message", "日期范围（开始、结束）参数为空");
			return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
		}
		
		String start_date_str_trim = date_range_start.trim();	// 去掉首尾空白字符后的开始日期字符串
		String end_date_str_trim = date_range_end.trim();	// 去掉首尾空白字符后的结束日期字符串
		Date start_date = DateUtil.parse2date(1, start_date_str_trim);	// 将开始日期字符串转换成开始日期（yyyy-MM-dd 格式）
		Date end_date = DateUtil.parse2date(1, end_date_str_trim);	// 将结束日期字符串转换成结束日期（yyyy-MM-dd 格式）
		
		if(start_date == null || end_date == null) {
			json_ret.put("ret_code", -2);
			json_ret.put("ret_message", "开始日期或结束日期转换异常");
			return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
		}
		
		EpUser epu_query = new EpUser();
		epu_query.setPush2hw_atten(true);
		
		List<EpUser> epus_push2hw = epUserService.getEpUserByCriteria(epu_query);	// 1. 首先查出准备推送华为考勤系统的人员信息
		List<EpUser> epus_valid = new ArrayList<EpUser>();	// 有效的人员信息数组列表
		TreeMap<Integer, EpUser> epus_valid_map = new TreeMap<Integer, EpUser>();
		List<EpUser> epus_invalid = new ArrayList<EpUser>();	// 无效的人员信息数组列表
		
		JSONArray invalid_epus_ja = new JSONArray();
		
		for(EpUser epu : epus_push2hw) {
			// 有效的人员信息必须满足id为正整数，工号、姓名、身份证号非空
			if(epu.getId() != null && epu.getId() > 0 && StringUtils.isNotBlank(epu.getWork_no()) && StringUtils.isNotBlank(epu.getName()) && StringUtils.isNotBlank(epu.getIdentity_no())) {
				epus_valid.add(epu);
				epus_valid_map.put(epu.getId(), epu);
			} else {
				epus_invalid.add(epu);
			}
		}
		
		if(epus_invalid.size() > 0) {
			invalid_epus_ja.addAll(epus_invalid);
		}
		
		if(epus_valid.size() == 0) {
			json_ret.put("invalid_epus", invalid_epus_ja);
			json_ret.put("ret_code", -3);
			json_ret.put("ret_message", "准备推送华为考勤系统的有效人员信息为空");
			return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
		}
		
		// TODO: 2. 然后，从筛选日志中查出上次筛选出现异常的数据，进行重新筛选并修改状态...
		
		
		
		// 3. 从筛选日志表中查出已做过筛选的考勤日期（后面要从给定的日期范围里排除掉这部分已做过筛选的日期）
		List<Date> filtered_dates = pushFilterLogService.getFilteredDates();
		if(filtered_dates == null) {
			json_ret.put("ret_code", -4);
			json_ret.put("ret_message", "查询已筛选过的日期出现异常");
			return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
		}
		
		List<Date> need_dates = DateUtil.calcDatesExcludeGivenDatesByStartEndDate(filtered_dates, start_date, end_date);
		
		if(need_dates == null) {
			json_ret.put("ret_code", -5);
			json_ret.put("ret_message", "计算所需筛选的日期出现异常");
			return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
		}
		
		if(need_dates.size() == 0) {
			json_ret.put("ret_code", -6);
			json_ret.put("ret_message", "计算得到需要筛选的日期为空");
			return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
		}
		
		List<HashMap<String, Object>> results = epAttenService.getFirstAndLastPunchTimeValidByDatesAndEpUidsBeforeToday(need_dates, epus_valid);
		
		if(results == null) {
			json_ret.put("ret_code", -7);
			json_ret.put("ret_message", "根据所需筛选日期列表和有效人员列表查询考勤信息出现异常");
			return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
		}
		
		if(results.size() == 0) {
			json_ret.put("ret_code", -8);
			json_ret.put("ret_message", "根据所需筛选日期列表和有效人员列表查询考勤信息结果为空");
			return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
		}
		
		List<PushToHw> pthws = new ArrayList<PushToHw>();
		List<PushFilterLog> filtered_valid = new ArrayList<PushFilterLog>();
		List<PushFilterLog> filtered_invalid = new ArrayList<PushFilterLog>();
		List<PushFilterLog> filtered_all = new ArrayList<PushFilterLog>();
		
		Date filter_time = new Date();
		
		for(HashMap<String, Object> result : results) {
//			if(result.containsKey("ep_uid") && result.containsKey("punch_date") && result.containsKey("first_punch_time") && result.containsKey("last_punch_time")) {
//				
//			} else {
//				
//			}
			Object ep_uid_obj = result.get("ep_uid");
			Object punch_date_obj = result.get("punch_date");
			Object first_punch_time_obj = result.get("first_punch_time");
			Object last_punch_time_obj = result.get("last_punch_time");
			
			PushFilterLog pfl = new PushFilterLog();
			pfl.setFilter_time(filter_time);
			
			if(ep_uid_obj instanceof Integer) {
				Integer ep_uid = (Integer) ep_uid_obj;
				pfl.setEp_uid(ep_uid);
				
				if(punch_date_obj instanceof Date && first_punch_time_obj instanceof Date && last_punch_time_obj instanceof Date) {
					Date punch_date = (Date) punch_date_obj;
					Date first_punch_time = (Date) first_punch_time_obj;
					Date last_punch_time = (Date) last_punch_time_obj;
					
					pfl.setDayof_date(punch_date);
					
					EpUser epUser = epus_valid_map.get(ep_uid);
					if(epUser != null) {
						PushToHw pthw = new PushToHw();
						pthw.setEp_uid(ep_uid);
						pthw.setDayof_date(punch_date);
						pthw.setOn_duty_time(first_punch_time);
						if(first_punch_time.equals(last_punch_time) || first_punch_time.after(last_punch_time)) {
							pthw.setOff_duty_time(null);
						} else {
							pthw.setOff_duty_time(last_punch_time);
						}
						pthw.setName(epUser.getName());
						pthw.setId_no(epUser.getIdentity_no());
						// 星期几。。。
						pthw.setDayof_week(DateUtil.getDayOfWeekByDate(punch_date, 1));
						pthws.add(pthw);
						
//						pfl.setDayof_date(punch_date);
//						pfl.setEp_uid(ep_uid);
//						pfl.setFilter_time(filter_time);
						pfl.setStatus("filter_success");
						pfl.setDescribe("成功筛选出该用户的考勤信息");
						filtered_valid.add(pfl);
					} else {
//						pfl.setEp_uid(ep_uid);
//						pfl.setFilter_time(filter_time);
//						pfl.setDayof_date(punch_date);
						pfl.setStatus("filter_error_1");
						pfl.setDescribe("根据用户id获取用户信息为null");
						filtered_invalid.add(pfl);
					}
				} else {
					pfl.setStatus("filter_error_2");
					pfl.setDescribe("该用户的考勤数据日期非日期类型");
					filtered_invalid.add(pfl);
				}
			} else {
				pfl.setStatus("filter_error_3");
				pfl.setDescribe("该用户的用户id非整数类型");
				filtered_invalid.add(pfl);
			}
		}
		
		if(pthws.size() == 0) {
			logger.error("准备推送华为考勤数据条数为0");
		} else {
			Integer batch_add_pthws = pushToHwService.batchAddPushToHws(pthws);
			
			if(batch_add_pthws == null || batch_add_pthws < 0) {
				json_ret.put("ret_code", -9);
				json_ret.put("ret_message", "批量添加准备推送华为考勤数据出现异常");
				return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
			}
		}
		
		filtered_all.addAll(filtered_invalid);
		filtered_all.addAll(filtered_valid);
		
		if(filtered_all.size() == 0) {
			logger.error("筛选日志数据条数为0");
		} else {
			int batch_add_pfls = pushFilterLogService.batchAddPushFilterLogs(filtered_all);
			
			if(batch_add_pfls < 0) {
				json_ret.put("ret_code", -10);
				json_ret.put("ret_message", "批量添加筛选日志数据出现异常");
				return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
			}
		}
		
		
		
		
		json_ret.put("ret_code", 1);
		json_ret.put("ret_message", "添加准备推送华为考勤系统考勤数据成功");
		return JSONObject.toJSONString(json_ret, SerializerFeature.WriteMapNullValue);
	}
	
	
	/**
	 * 导入公司员工信息（准备推送华为考勤系统的人员信息Excel表）
	 * 
	 * @param file_path（Excel表格文件路径）
	 * @return
	 */
	public JSONObject impHwEmpInfo(String file_path) {
		JSONObject json_obj = new JSONObject();	// 用来存放返回数据的JSON对象
		
		boolean is_success = true;	// 表示导入员工信息Excel表格数据是否全部成功
		
//		List<JSONObject> failed_hw_emps = new ArrayList<JSONObject>();	// 用来存放导入失败的用户信息的数组
		JSONArray failed_hw_emps_ja = new JSONArray();	// 用来存放导入失败的用户信息的JSON数组
		
		try {
			List<EpUser> hw_emps = new ReadExcel().readXls_push2hw_emp(file_path);
			
			for(EpUser epu : hw_emps) {
				JSONObject failed_hw_emp_info = new JSONObject();
				/*
                EpUser epu_by_wn = EpAttenController.getEpUserByWorkNoMach(epu.getWork_no());
                if(epu_by_wn != null) {
                	String epu_notes = epu_by_wn.getNotes();
                	if(!StringUtils.isBlank(epu_notes) && epu_notes.startsWith("query_result=")) {
                		invalid_epu_list.add(epu_by_wn);
                		System.out.println("根据工号查询用户信息出现异常：work_no = " + epu_by_wn.getWork_no() + ", " + epu_notes);
                	} else {
                		// 正常获取到用户信息。。。
                		valid_epu_list.add(epu_by_wn);
//                		epu.setId(epu_by_wn.getId());
//                		epu.setWork_no(null);	// 设为null表示不更新工号字段的值
                		EpUser epu_upd = new EpUser();
                		epu_upd.setId(epu_by_wn.getId());
                		epu_upd.setIdentity_no(epu.getIdentity_no());
                		epu_upd.setBase_place(epu.getBase_place());
                		epu_upd.setPush2hw_atten(true);
//                		epUser
                	}
                } else {
                	invalid_epu_list.add(epu);
                }
                
//                System.out.println(epu);
                */
				EpUser epu_by_wn = EpAttenController.getEpUserByWorkNoMach(epu.getWork_no());
				if(epu_by_wn != null) {
					String epu_notes = epu_by_wn.getNotes();
                	if(!StringUtils.isBlank(epu_notes) && epu_notes.startsWith("query_result=")) {
                		System.out.println("根据工号查询用户信息出现异常：work_no = " + epu_by_wn.getWork_no() + ", " + epu_notes);
                		failed_hw_emp_info.put("work_no", epu.getWork_no());
                		failed_hw_emp_info.put("name", epu.getName());
                		failed_hw_emp_info.put("base_place", epu.getBase_place());
    					failed_hw_emp_info.put("notes", epu.getNotes() + ", " + epu_notes);
//    					failed_hw_emps.add(failed_hw_emp_info);
    					failed_hw_emps_ja.add(failed_hw_emp_info);
                		is_success = false;
                	} else {
                		// // 正常获取到用户信息。。。
                		
                		// 如果Excel表格中的数据与数据库中已有的用户信息完全一致，则跳过更新用户信息操作；否则才需要更新用户信息
                		boolean is_id_no_equal = isTwoBasicTypeEqual(epu.getIdentity_no(), epu_by_wn.getIdentity_no());
                		boolean is_base_place_equal = isTwoBasicTypeEqual(epu.getBase_place(), epu_by_wn.getBase_place());
                		boolean is_push2hw_equal = isTwoBasicTypeEqual(epu.getPush2hw_atten(), epu_by_wn.getPush2hw_atten());
                		
                		if(is_id_no_equal && is_base_place_equal && is_push2hw_equal) {
                			logger.info("该用户信息已为最新，无须更新，work_no = " + epu.getWork_no() + ", name = " + epu.getName() + ", notes = " + epu_notes);
                			continue;
                		}
                		
                		EpUser epu_upd = new EpUser();
                		epu_upd.setId(epu_by_wn.getId());
                		epu_upd.setIdentity_no(epu.getIdentity_no());
                		epu_upd.setBase_place(epu.getBase_place());
                		epu_upd.setPush2hw_atten(true);
                		int mod_ret = epUserService.modifyEpUserById(epu_upd);
                		if(mod_ret <= 0) {
                			System.out.println("更新是否推送华为考勤系统的用户信息出现异常 + mod_ret = " + mod_ret);
                			failed_hw_emp_info.put("work_no", epu.getWork_no());
                			failed_hw_emp_info.put("name", epu.getName());
                    		failed_hw_emp_info.put("base_place", epu.getBase_place());
        					failed_hw_emp_info.put("notes", epu.getNotes());
//        					failed_hw_emps.add(failed_hw_emp_info);
        					failed_hw_emps_ja.add(failed_hw_emp_info);
                			is_success = false;
                		} else {
                			System.out.println("更新是否推送华为考勤系统的用户信息成功，行号：" + epu.getNotes());
                		}
                	}
				} else {
					failed_hw_emp_info.put("work_no", "<BLANK_WORK_NO / 空白工号>");
					failed_hw_emp_info.put("name", epu.getName());
            		failed_hw_emp_info.put("base_place", epu.getBase_place());
					failed_hw_emp_info.put("notes", epu.getNotes());
//					failed_hw_emps.add(failed_hw_emp_info);
					failed_hw_emps_ja.add(failed_hw_emp_info);
					System.out.println("出现空白字符串的工号：" + epu);
					is_success = false;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
//			System.out.println("读取xlsm表格时出现IO异常...");
			logger.error("读取xlsm表格时出现IO异常...");
		}
		
		if(is_success) {
			json_obj.put("ret_code", 1);
			json_obj.put("ret_message", "操作成功");
		} else {
			json_obj.put("ret_code", 0);
			json_obj.put("failed_hw_emps", failed_hw_emps_ja);
			json_obj.put("failed_count", failed_hw_emps_ja.size());
			json_obj.put("ret_message", "操作失败");
		}
		
		return json_obj;
	}
	
	public static boolean isTwoBasicTypeEqual(Object type1, Object type2) {
		if(type1 == null || type2 == null) {
			return false;
		}
		
//		if(type1 instanceof String && type2 instanceof String) {
//			String type1_str = (String) type1;
//			String type2_str = (String) type2;
//			
//			return type1_str.equals(type2_str);
//		}
		
//		if(type1 instanceof Integer && type2 instanceof Integer) {
//			
//		}
		
		return type1.equals(type2);
//		return false;
	}
	
	/**
	 * 导入公司员工信息（移动考勤试用人员b信息Excel表）
	 * 
	 * @param file_path（Excel表格文件路径）
	 * @return
	 */
	public JSONObject impEmpInfo_ep_extra(String file_path) {
		JSONObject json_obj = new JSONObject();	// 用来存放返回数据的JSON对象
		
		boolean is_success = true;	// 表示导入员工信息Excel表格数据是否全部成功
		
		try {
			List<EpUser> ep_emps_extra = new ReadExcel().readXls_ep_emp_extra(file_path);
			
//			if(ep_emps != null) {
//				System.out.println("总共员工数：" + ep_emps.size());
//				for(EpUser epu:ep_emps) {
//					
//				}
//			}
			
			for(EpUser epu:ep_emps_extra) {
//				int ret = epUserService.addEpUser(epu);
				
				String epu_name = epu.getName();
				
				int one_id = 0;
				
				if(StringUtils.isNotBlank(epu_name)) {
//					EpUser epu_query = new EpUser();
//					epu_query.setName(epu_name);
//					List<EpUser> epus_ret = epUserService.getEpUserByCriteria(epu_query);
					
					List<EpUser> epus_ret = epUserService.getOneEpUserByNameAndSpecialCriterion(epu_name);
					
					if(epus_ret != null) {
						if(epus_ret.size() == 1) {
							EpUser epu_one = epus_ret.get(0);
							int epu_id = epu_one.getId();
							one_id = epu_id;	// 拿到姓名对应的唯一用户
						} else {
							System.out.println("根据用户姓名查询到多个用户：");
							for(EpUser ei:epus_ret) {
								System.out.println("多个重名用户的详细信息：" + ei);
							}
							is_success = false;	// 一旦有一条员工信息数据未导入成功，则设置是否全部成功的标志为false（即表示导入失败）
							continue;
						}
					} else {
						System.out.println("根据用户姓名查询用户信息失败：name = " + epu_name);
						System.out.println("用户详细信息：epu = " + epu);
						is_success = false;	// 一旦有一条员工信息数据未导入成功，则设置是否全部成功的标志为false（即表示导入失败）
						continue;
					}
				} else {
					System.out.println("出现姓名为空的用户：epu = " + epu);
					is_success = false;	// 一旦有一条员工信息数据未导入成功，则设置是否全部成功的标志为false（即表示导入失败）
					continue;
				}
				
//				EpUser epu_mod = new EpUser();
//				epu_mod.setId(one_id);
//				epu_mod.setIdentity_no(epu.getIdentity_no());
//				epu_mod.setOrigin_pwd(epu.getOrigin_pwd());
				
				epu.setId(one_id);
				epu.setName(null);	// 清除姓名字段的值，从而在操作数据库时保持姓名字段值不变
				int ret = epUserService.modifyEpUserById(epu);
				
				if(ret <= 0) {
					is_success = false;	// 一旦有一条员工信息数据未导入成功，则设置是否全部成功的标志为false（即表示导入失败）
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(is_success) {
			json_obj.put("ret_code", 1);
			json_obj.put("ret_msg", "全部员工基本信息导入成功");
		} else {
			json_obj.put("ret_code", -1);
			json_obj.put("ret_msg", "员工基本信息导入失败");
		}
		
		return json_obj;
	}
	
	
	/**
	 * 导入公司员工信息（公司员工信息Excel表）
	 * 
	 * @param file_path（Excel表格文件路径）
	 * @return
	 */
	public JSONObject impEmpInfo_ep(String file_path) {
		JSONObject json_obj = new JSONObject();	// 用来存放返回数据的JSON对象
		
		boolean is_success = true;	// 表示导入员工信息Excel表格数据是否全部成功
		
		try {
			List<EpUser> ep_emps = new ReadExcel().readXls_ep_emp(file_path);
			
//			if(ep_emps != null) {
//				System.out.println("总共员工数：" + ep_emps.size());
//				for(EpUser epu:ep_emps) {
//					
//				}
//			}
			
			for(EpUser epu:ep_emps) {
				int ret = epUserService.addEpUser(epu);
				if(ret <= 0) {
					is_success = false;	// 一旦有一条员工信息数据未导入成功，则设置是否全部成功的标志为false（即表示导入失败）
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(is_success) {
			json_obj.put("ret_code", 1);
			json_obj.put("ret_msg", "全部员工基本信息导入成功");
		} else {
			json_obj.put("ret_code", -1);
			json_obj.put("ret_msg", "员工基本信息导入失败");
		}
		
		return json_obj;
	}
	
	/**
	 * 导入部分新公司员工信息（公司员工信息Excel表）
	 * 
	 * @param file_path（Excel表格文件路径）
	 * @return
	 */
	public JSONObject impSomeNewEmpInfo(String file_path) {
		JSONObject json_obj = new JSONObject();	// 用来存放返回数据的JSON对象
		
		boolean is_success = true;	// 表示导入员工信息Excel表格数据是否全部成功
		
		JSONArray failed_emps = new JSONArray();
		
		try {
			List<EpUser> ep_emps = new ReadExcel().readXls_ep_emp(file_path);
			
			for(EpUser epu:ep_emps) {
				JSONObject failed_emp_info = new JSONObject();
				
				EpUser epu_by_wn = EpAttenController.getEpUserByWorkNoMach(epu.getWork_no());
				
				if(epu_by_wn != null) {
					String epu_notes = epu_by_wn.getNotes();
                	if(!StringUtils.isBlank(epu_notes) && epu_notes.startsWith("query_result=")) {
                		// query_result=匹配该工号的用户数量为0
                		if(epu_notes.equals("query_result=匹配该工号的用户数量为0")) {
                			EpUser epu_add = new EpUser();
                			epu_add.setWork_no(epu.getWork_no());
                			epu_add.setName(epu.getName());
                			epu_add.setMobile_phone(epu.getMobile_phone());
                			epu_add.setDept_name(epu.getDept_name());
                			epu_add.setProject_name(epu.getProject_name());
                			epu_add.setEmail(epu.getEmail());
                			epu_add.setIdentity_no(epu.getIdentity_no());	// 身份证号
                			epu_add.setBase_place(epu.getBase_place());		// base地
                			epu_add.setPwd(epu.getPwd());
                			epu_add.setOrigin_pwd(epu.getPwd());
                			epu_add.setMima(epu.getMima());
                			epu_add.setType("2");	// “2”表示有小周末的员工类型
                			epu_add.setPush2hw_atten(true);		// true表示要推华为考勤系统
                			int add_ret = epUserService.addEpUser(epu_add);
                			if(add_ret <= 0) {
                				failed_emp_info.put("name", epu.getName());
                    			failed_emp_info.put("work_no", epu.getWork_no());
                    			failed_emp_info.put("notes", epu.getNotes() + ", " + epu_notes);
                    			failed_emp_info.put("message", "添加用户信息出现异常");
                    			failed_emps.add(failed_emp_info);
                    			is_success = false;
                			} else {
//                				logger.info("添加用户信息成功！");
                			}
                		} else {
                			System.out.println("根据工号查询用户信息出现异常：work_no = " + epu_by_wn.getWork_no() + ", " + epu_notes);
                			failed_emp_info.put("name", epu.getName());
                			failed_emp_info.put("work_no", epu.getWork_no());
                			failed_emp_info.put("notes", epu.getNotes() + ", " + epu_notes);
                			failed_emp_info.put("message", "已存在该用户但该用户信息异常，或查询数据库异常");
                			failed_emps.add(failed_emp_info);
                			is_success = false;
                		}
                	} else {
                		// // 已存在该用户信息。。。
                		failed_emp_info.put("name", epu.getName());
                		failed_emp_info.put("work_no", epu.getWork_no());
                		failed_emp_info.put("notes", epu.getNotes());
                		failed_emp_info.put("message", "已存在该用户信息");
                		failed_emps.add(failed_emp_info);
                		is_success = false;
                	}
				} else {
					failed_emp_info.put("name", epu.getName());
					failed_emp_info.put("work_no", epu.getWork_no());
					failed_emp_info.put("row_no", epu.getNotes());
					failed_emp_info.put("message", "工号为空白字符串");
					failed_emps.add(failed_emp_info);
					is_success = false;
				}
				
//				int ret = epUserService.addEpUser(epu);
//				if(ret <= 0) {
//					is_success = false;	// 一旦有一条员工信息数据未导入成功，则设置是否全部成功的标志为false（即表示导入失败）
//				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(is_success) {
			json_obj.put("ret_code", 1);
			json_obj.put("ret_msg", "全部员工基本信息导入成功");
		} else {
			json_obj.put("ret_code", -1);
			json_obj.put("failed_emps_info", failed_emps);
			json_obj.put("failed_count", failed_emps.size());
			json_obj.put("ret_msg", "员工基本信息导入失败");
		}
		
		return json_obj;
	}
	
	
	/**
	 * 导入公司员工信息（企业通讯录）
	 * 参考钉钉管理后台导出的员工信息Excel表中的数据结构
	 * @param file_path（Excel表格文件路径）
	 * @return
	 */
	public JSONObject impEmpInfo(String file_path) {
		JSONObject json_obj = new JSONObject();	// 用来存放返回数据的JSON对象
		
		try {
			List<Employee> emp_list = new ReadExcel().readXls_emp(file_path);
			
			String company_name = null;	// 准备用来存放公司名称/最顶层的根部门名称
			
			if(emp_list != null) {
				// 首先，取出员工信息数组列表中第一行数据（一个特殊的员工信息，用来存放解析出来的公司名称）
				Employee first_emp = emp_list.get(0);
				if(first_emp != null) {
					// 如果第一条特殊员工信息的部门字段值不是空标识"<empty>"，且备注字段值为"flag=company_name"时，说明解析公司名称信息成功
					if(!"<empty>".equals(first_emp.getDept()) && "flag=company_name".equals(first_emp.getNotes())) {
						company_name = first_emp.getDept();
					} else {	// 否则，说明从Excel表格中解析公司名称信息失败，直接返回相应的错误信息
						json_obj.put("ret_code", -1);
						json_obj.put("ret_msg", "公司名称/根部门名称解析失败");
						return json_obj;
					}
				}
				
				// 然后，从第二条员工信息数据开始逐行遍历，依次进行处理
				JSONArray ja_blank_error = new JSONArray();	// 用来存放处理时因为必填字段为空错误的每行数据的JSON数组
				JSONArray ja_dup_phone = new JSONArray();	// 用来存放因手机号与数据库中已有数据重复时的所有行数据的JSON数组
				for(int emp_index = 1; emp_index < emp_list.size(); emp_index ++) {	// 员工信息列表的下标从1开始，即从第2名员工信息开始解析（因为第1名员工信息已被用于存放公司名称company_name信息）
					Employee emp = emp_list.get(emp_index);
					String dept_clue = emp.getDept();	// 先取出员工信息中的部门字段中的部门线索信息，方便后面对部门字段进行解析
					String is_managers = emp.getIs_managers();	// 取出员工信息中的是否部门主管字段的信息（如果为空，则默认为“否”，如果有多于部门个数的，则舍弃）
					// 首先对从Excel表格中读取到员工信息进行最基本的必填字段非空校验：部门、姓名、手机号，这三个字段为必填字段。
					if(StringUtils.isBlank(dept_clue) || StringUtils.isBlank(emp.getName()) || StringUtils.isBlank(emp.getMobile_phone())) {
						// 先对异常员工信息数据做记录（记录异常数据在Excel表格中所在行号），然后直接跳到下一次for循环
//						JSONObject error_obj = new JSONObject();
//						error_obj.put("error_type", 0);	// 设置错误类型为0，表示三个必填字段中至少有一个为空
//						error_obj.put("error_data", emp);	// 将出错的数据放入error_data中，以便返回错误信息可以查看到具体是哪条数据出现错误或异常
//						error_obj.put("error_data_2", JSONObject.toJSON(emp));	// 将出错的数据先转成JSON对象后，再放入error_data中，对比上面直接存放Java对象的写法，看看结果会有什么不同，最后取效果较好的写法
//						ja_blank_error.add(error_obj);	// 将存有异常员工信息数据的JSON对象添加进前面已经声明的JSON数组中，方便方法调用返回时一起返回
						ja_blank_error.add(emp);
//						ja_blank_error.add(JSON.toJSON(emp));
						continue;
					}
					
					EpUser epu = new EpUser();
					// 设置员工基本信息
					epu.setUserid(emp.getUserid());
					epu.setJob_title(emp.getJob_title());
					epu.setName(emp.getName());
					epu.setGender(emp.getGender());
					epu.setWork_no(emp.getWork_no());
					epu.setMobile_phone(emp.getMobile_phone());
					epu.setEmail(emp.getEmail());
					epu.setFixed_phone(emp.getFixed_phone());
					epu.setWork_place(emp.getWork_place());
					epu.setNotes(emp.getNotes());
					
					EpUser epu2 = new EpUser();
					epu2.setName(emp.getName());
					epu2.setMobile_phone(emp.getMobile_phone());
					List<EpUser> res2 = epUserService.getEpUserByCriteria(epu2);
					// TODO: 对当前员工信息做重复校验，如果在数据库中已存在，则抛弃这条数据，并做记录（方便返回错误信息）
					// 判断员工信息是否重复的自定义标准：“部门+姓名+手机号”三个必填字段都相同时才判定为重复数据行。
					// 另外，如果“部门+姓名”组合字段不同（即这两个字段中至少有一个不同），但“手机号”字段的值在数据库中已存在时，则认为这条数据是无效数据（因为要求所有人不论是公司内部员工还是外部联系人，
					// 手机号都要保持唯一性，不能出现多个人共用一个手机号的情况），应当舍弃。
					// 所以，综合上诉分析，可以认为只要是手机号与数据库中已有的数据相同时，则都应当舍弃这条数据。
					if(epUserService.isMobilePhoneDuplicate(epu)) {	// 如果当前这条员工信息数据中的手机号在数据库中已存在与之重复的数据，则默认认为导入数据的用户的意图是：
																	// 想要继续对当前这个员工的信息进行追加，包括员工所属部门信息
						if(res2 != null && res2.size() == 0) {	// 如果根据“姓名+手机号”双重验证，未查询到匹配的员工信息，则说明这条员工信息可能有误，因为同一手机号不应该同时属于两个人（这里认为姓名不同即为不同人）
																// 这时才应当将这条数据放进重复手机号的数组里去（PS.这里的判断逻辑：首先判断手机号相同，然后判断“姓名+手机号”不同，那么就是两个不同名的人手机号相同）
							ja_dup_phone.add(emp);
							continue;
						}
						// 然后，剩下的员工信息即为满足“姓名+手机号”都相同的情况，然后才对这名已存在的员工进行追加部门信息操作（注意，在导入数据库时不做员工基本信息的更新操作，
						// 即使Excel表格数据中员工信息与数据库中的员工基本信息不一致，也会忽略掉）
					} else {
						// 将手机号未重复的员工信息存入数据库（即认为这名员工是新员工）
						epUserService.addEpUser(epu);	// 注意，存入数据库后，epu对象的id属性的值会自动赋上相应的数据库中的id字段的值
					}
					
					EpUser res_one = null;
					if(epu.getId() == 0) {	// 如果当前导入的这名员工不是新添加的员工
						if(res2 != null && res2.size() == 1) {	// 如果数据库中有且仅有一名员工的“姓名+手机号”与当前导入的这条员工信息相匹配，则可以进行部门信息的追加操作；否则，无法确定该对哪个员工的部门信息进行操作
							res_one = res2.get(0);	// 获取到唯一匹配“姓名+手机号”双重条件验证的那条员工信息
						} else {	// 无法确定该对哪个员工进行部门信息操作的情况，只抛出异常，不做进一步处理
							try {
								throw new Exception("数据库中出现多条员工信息同时满足“姓名+手机号”双重条件，系统无法确定该对哪个员工进行操作！"
										+ "(Excel行号=" + emp.getRow_no() + ", 姓名=" + emp.getName() + ", 手机号=" + emp.getMobile_phone() + ")");
							} catch (Exception e) {
								e.printStackTrace();
							}
							continue;
						}
					}
					
					// 对“部门”、“是否部门主管”两个字段进行解析
					// Step1. 根据Excel表格数据中的“部门”字段解析出公司的组织架构（树形结构），并将这种结构存入数据库中
					// TODO: 注：暂时只考虑公司所有部门下都至少有一名员工的情况（如果有部分部门下没有员工，则Excel表格数据中就会不存在该部门的相关信息，从而无法解析出这部分部门）
					// TODO: 暂时排除掉根部门下的直属部门名有与根部门名同名的情况
					// TODO: 由于从钉钉管理后台导出的Excel表格数据中，如果是根部门，部门字段的值就是根部门名；如果是根部门的下属部门，部门字段的值就是以根部门下的直属部门名开头以短横线连接起来的部门线索信息，
					//       所以，暂时只考虑导入这种结构的数据（如果根部门的下属部门部门名字段的值有以根部门名开头的情况，需另做处理）
					// Step1.1  先将部门字段的值以逗号为分隔符拆分开，成为一条条的部门线索信息
					// TODO: 假设所有部门名中都不包含英文逗号（,）这个字符，可以避免拆分部门线索信息时出现拆分不正确的情况
					String[] clue_strs = dept_clue.split(",");
					int clue_str_count = clue_strs.length;	// 获得部门线索信息的条数，也即对应将要解析的部门个数
					// 对是否部门主管字段按英文逗号（,）进行拆分
					String[] is_manager_strs = new String[clue_str_count];	// 用来存放对是否部门主管字段按逗号拆分后的字符串数组
					Integer[] is_manager_ints = new Integer[clue_str_count];	// 用来存放是否部门主管标志的整型数组，1代表“是”，0代表“否”
					if(StringUtils.isNotBlank(is_managers)) {
						String is_managers_trim = is_managers.trim();
						if(StringUtils.isNotBlank(is_managers_trim)) {
							is_manager_strs = is_managers_trim.split(",");
							
						} else {	// 如果trim后的是否部门主管字段为空白字符串，则设置默认都为“否”
							for(int i=0; i<clue_str_count; i++) {
								is_manager_strs[i] = "否";
							}
						}
					} else {	// 如果是否部门主管字段为空白字符串，则设置默认都为“否”
						for(int i=0; i<clue_str_count; i++) {
							is_manager_strs[i] = "否";
						}
					}
					if(clue_str_count > 0) {
						String[] is_managers_tmp = new String[clue_str_count];	// 新创建一个临时的是否部门主管标志数组
						if(is_manager_strs.length < clue_str_count) {	// 如果是否部门主管标志的个数小于部门线索信息条数，则不够的是否部门主管标志以默认的“否”来填充
							for(int i=0; i<is_manager_strs.length; i++) {
								if(StringUtils.isNotBlank(is_manager_strs[i])) {
									String is_manager_stri_trim = is_manager_strs[i].trim();
									if(StringUtils.isNotBlank(is_manager_stri_trim)) {
										if("是".equals(is_manager_stri_trim)) {
											is_managers_tmp[i] = "是";
										} else {
											is_managers_tmp[i] = "否";
										}
									} else {
										is_managers_tmp[i] = "否";
									}
								} else {
									is_managers_tmp[i] = "否";
								}
							}
							for(int i=is_manager_strs.length; i<clue_str_count; i++) {	// 对于剩下不够的是否部门主管标志设置默认的“否”进行填充
								is_managers_tmp[i] = "否";
							}
						} else if(is_manager_strs.length >= clue_str_count) {	// 如果是否部门主管标志的个数大于部门线索信息条数，则舍弃多余的是否部门主管标志；如果相等，则标志个数保持不变
							for(int i=0; i<clue_str_count; i++) {
								if(StringUtils.isNotBlank(is_manager_strs[i])) {
									String is_manager_stri_trim = is_manager_strs[i].trim();
									if(StringUtils.isNotBlank(is_manager_stri_trim)) {
										if("是".equals(is_manager_stri_trim)) {
											is_managers_tmp[i] = "是";
										} else {
											is_managers_tmp[i] = "否";
										}
									} else {
										is_managers_tmp[i] = "否";
									}
								} else {
									is_managers_tmp[i] = "否";
								}
							}
						}
						is_manager_strs = is_managers_tmp;	// 将临时的是否部门主管标志数组的引用赋给后面要用到的is_manager_strs字符串数组
					} else {	// 如果部门线索信息条数为0，则设置是否部门主管标志数组为空的字符串数组，即此时认为是否部门主管字段的值是无效的，即使有值也会忽略掉
						is_manager_strs = new String[]{};
					}
					
					for(int i=0; i<clue_str_count; i++) {
						if("是".equals(is_manager_strs[i])) {
							is_manager_ints[i] = 1;
						} else {
							is_manager_ints[i] = 0;
						} 
					}
					
					for(int clue_index = 0; clue_index < clue_str_count; clue_index++) {
						String clue_str = clue_strs[clue_index];
						String clue_trim = clue_str.trim();	// 去掉拆分出来的部门线索信息字符串中首尾的空白字符（不限于空格字符，只要是ASCII码小于或等于空格字符的字符都属于空白字符）
															// 至于中间若出现空白字符的情况，则不作处理（因为钉钉管理后台在添加部门时并未限制部门名中间不能出现空白字符）
						if(StringUtils.isBlank(clue_trim)) {
							continue;	// 如果去掉部门线索信息首尾空白字符后，是一个空白字符串，则直接跳到下一条线索的解析
						}
						// Step1.2  在数据库中查询该部门线索信息是否已经解析过，
						// 		如果已经解析过，则可以直接跳过对当前线索的解析，进入下一条线索的解析；
						// 		如果没有解析过，则先将当前线索存入数据库，然后开始对该线索进行解析。
						DeptClue dept_clue_res = deptClueService.findDeptClueByDeptClueStr(clue_trim);
						if(dept_clue_res != null) {	// 如果在数据库中找到该部门线索信息字符串对应的部门线索信息，则说明之前已解析过该条部门线索信息
							// 在跳到下一条线索解析之前，先建好当前员工与该部门之间的关联关系，将这种关联关系存入数据库表dept_user中
							if(dept_clue_res.getLevel_count() != null && dept_clue_res.getLast_dept_id() != null) {	// 如果在部门线索信息表中已有所需的数据，则直接拿来用
								if(dept_clue_res.getLevel_count().intValue() > 0 && dept_clue_res.getLast_dept_id().intValue() > 0) {	// 如果所需的数据都合法，至少要满足最基本的大于0范围要求
									EpUser eu_tmp = epu;	// 默认把将要建立部门-员工对应关系的员工引用指向新员工epu
									if(epu.getId() > 0 && res_one == null) {	// 如果当前导入的这条员工信息是一名新员工的（导入之前数据库中不存在该名员工的信息），
									}
									if(epu.getId() == 0 && res_one != null) {	// 如果当前导入的这条员工信息是一名老员工的（导入之前数据库中已存在该名员工的信息），
										eu_tmp = res_one;	// 将将要建立部门-员工对应关系的员工引用指向老员工res_one
									}
									DeptUser du_tmp = new DeptUser();
									du_tmp.setDept_id(dept_clue_res.getLast_dept_id());
									du_tmp.setEp_uid(eu_tmp.getId());
									DeptUser du_res = deptUserService.getDeptUserByDeptIdAndUid(du_tmp);
									if(du_res == null) {	// 如果在数据库中当前员工与部门之间的对应关系为空，即当前员工的部门归属信息未在数据库中出现过时，则需要在数据库中新添加一条部门-员工对应关系
										du_tmp.setIs_manager(is_manager_ints[clue_index]);	// 暂时设置默认不是部门主管，后面再根据具体导入的Excel表格中的“是否此部门主管”字段的值来确定
										int ret = deptUserService.addDeptUserInfo(du_tmp);
										if(ret > 0) {	// 如果添加部门-员工对应关系信息成功
											// TODO: 添加成功后要做的事情。。。
										} else {	// 如果添加部门-员工对应关系信息时出现异常，则首先抛出异常，然后跳到下一次循环
											try {
												throw new Exception("添加部门-员工对应关系信息时出现异常:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
											} catch (Exception e) {
												e.printStackTrace();
											}
											continue;
										}
									} else {	// 如果在数据库中已经存在当前员工与部门之间的对应关系信息，则在后台日志输出这条部门-员工对应关系信息，然后直接跳到下一次循环
										logger.info("在数据库中已存在该部门-员工对应关系信息:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
										continue;
									}
								}
							}
							continue;
						} else {	// 如果在数据库中未找到该部门线索信息字符串对应的部门线索信息，则说明之前尚未解析过该条部门线索信息，需要对该线索进行解析
							String[] dept_strs = clue_trim.split("-");
							if(dept_strs.length > 0) {	// 如果由当前部门线索信息拆分出来的部门名个数大于0（即至少有一个部门名）
								if(dept_strs.length == 1) {	// 如果拆分出来的部门名有且仅有一个时，说明这个部门名要么是根部门名，要么是根部门下的直属部门名
									String dept_one = dept_strs[0];	// 取出仅有的这个部门名
									String dept_one_trim = dept_one.trim();	// 去除部门名字符串首尾的空白字符
									if(StringUtils.isNotBlank(dept_one_trim)) {	// 如果trim后的部门名不是空白字符串，即可作为一个有效的部门名使用
										if(dept_one_trim.equals(company_name)) {	// 如果这个部门名与根部门名相同，则说明当前要导入的员工是根部门下的直属员工
											// 首先在部门信息表中查询是否已经存有该根部门的信息，如果不存在，则先要在部门表中添加该部门的信息；如果已存在，则无须再添加
											EpDept ed_query = new EpDept();
											ed_query.setName(dept_one_trim);
											ed_query.setLevel(1); 	// 根部门的部门层级数定义为1
											List<EpDept> ed_res = epDeptService.findEpDeptByCriteria(ed_query);
											if(ed_res.size() > 0) {	// 如果根据给定“部门名+部门层级数”条件有已知的部门信息与之匹配，则无须添加这个部门的信息
												if(ed_res.size() == 1) {	// 如果有且仅有一条部门信息与“部门名+部门层级数”双重条件相匹配
													// 取得仅有的这条部门信息
													EpDept ed_res_one = ed_res.get(0);
													// 往数据库表dept_clue中添加一条部门线索信息
													DeptClue dc = new DeptClue();
													dc.setCompany_name(company_name);
													dc.setClue_str(clue_trim);
//													dc.setDept_id_clue(ed_res_one.getId()+"");	// 构造根部门的部门id线索串
													dc.setLevel_count(1); 	// 根部门部门层级数设为1
													dc.setLast_dept_id(ed_res_one.getId());	// 如果部门线索信息中仅有一个根部门，那么这条部门线索信息的最后一个部门的部门id也就是这个根部门的id
													deptClueService.addDeptClue(dc);
													// 添加完部门线索信息。。。
													// 开始添加部门-员工对应关系信息
													EpUser eu_tmp = epu;	// 默认把将要建立部门-员工对应关系的员工引用指向新员工epu
													if(epu.getId() > 0 && res_one == null) {	// 如果当前导入的这条员工信息是一名新员工的（导入之前数据库中不存在该名员工的信息），
													}
													if(epu.getId() == 0 && res_one != null) {	// 如果当前导入的这条员工信息是一名老员工的（导入之前数据库中已存在该名员工的信息），
														eu_tmp = res_one;	// 将将要建立部门-员工对应关系的员工引用指向老员工res_one
													}
													DeptUser du_tmp = new DeptUser();
													du_tmp.setDept_id(ed_res_one.getId());
													du_tmp.setEp_uid(eu_tmp.getId());
													DeptUser du_res = deptUserService.getDeptUserByDeptIdAndUid(du_tmp);
													if(du_res == null) {	// 如果在数据库中当前员工与部门之间的对应关系为空，即当前员工的部门归属信息未在数据库中出现过时，则需要在数据库中新添加一条部门-员工对应关系
														du_tmp.setIs_manager(is_manager_ints[clue_index]);	// 暂时设置默认不是部门主管，后面再根据具体导入的Excel表格中的“是否此部门主管”字段的值来确定
														int ret = deptUserService.addDeptUserInfo(du_tmp);
														if(ret > 0) {	// 如果添加部门-员工对应关系信息成功
															// TODO: 添加成功后要做的事情。。。
														} else {	// 如果添加部门-员工对应关系信息时出现异常，则首先抛出异常，然后跳到下一次循环
															try {
																throw new Exception("添加部门-员工对应关系信息时出现异常:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
															} catch (Exception e) {
																e.printStackTrace();
															}
															continue;
														}
													} else {	// 如果在数据库中已经存在当前员工与部门之间的对应关系信息，则在后台日志输出这条部门-员工对应关系信息，然后直接跳到下一次循环
														logger.info("在数据库中已存在该部门-员工对应关系信息:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
														continue;
													}
												} else {	// 如果数据库中有多条部门信息与“部门名+部门层级数”双重条件相匹配，则无法确定当前员工属于哪个部门，抛出异常，不做进一步处理
													try {
														throw new Exception("在同一部门层级上出现两个或多个同名的部门，系统无法确定对哪个部门进行操作：（部门名=" + dept_one_trim + ", 部门层级=1）");
													} catch (Exception e) {
														e.printStackTrace();
													}
													continue;	// 出现异常后，直接跳到下一次循环，不做后续处理
												}
											} else {	// 如果在数据库中没有已知的部门信息与给定的“部门名+部门层级数”条件相匹配的记录，则须先新添加一条部门信息
												EpDept ed_add = new EpDept();
												ed_add.setName(dept_one_trim);
												ed_add.setLevel(1);
												ed_add.setSuperior_id(0);	// 根部门的上级部门id定义为0
												epDeptService.addEpDept(ed_add);
												// 新添加完一条部门信息。。。
												// 然后往数据库表dept_clue中添加一条部门线索信息
												DeptClue dc = new DeptClue();
												dc.setCompany_name(company_name);
												dc.setClue_str(clue_trim);
//												dc.setDept_id_clue(ed_add.getId()+"");	// 构造新创建的根部门的部门id线索串
												dc.setLevel_count(1);
												dc.setLast_dept_id(ed_add.getId()); 	// 这条部门线索信息中的最后一个部门的部门id是上面新添加的部门ed_add的id
												deptClueService.addDeptClue(dc);
												// 添加完部门线索信息。。。
												// 开始添加部门-员工对应关系信息
												EpUser eu_tmp = epu;	// 默认把将要建立部门-员工对应关系的员工引用指向新员工epu
												if(epu.getId() > 0 && res_one == null) {	// 如果当前导入的这条员工信息是一名新员工的（导入之前数据库中不存在该名员工的信息），
												}
												if(epu.getId() == 0 && res_one != null) {	// 如果当前导入的这条员工信息是一名老员工的（导入之前数据库中已存在该名员工的信息），
													eu_tmp = res_one;	// 将将要建立部门-员工对应关系的员工引用指向老员工res_one
												}
												DeptUser du_tmp = new DeptUser();
												du_tmp.setDept_id(ed_add.getId());
												du_tmp.setEp_uid(eu_tmp.getId());
												DeptUser du_res = deptUserService.getDeptUserByDeptIdAndUid(du_tmp);
												if(du_res == null) {	// 如果在数据库中当前员工与部门之间的对应关系为空，即当前员工的部门归属信息未在数据库中出现过时，则需要在数据库中新添加一条部门-员工对应关系
													du_tmp.setIs_manager(is_manager_ints[clue_index]);	// 暂时设置默认不是部门主管，后面再根据具体导入的Excel表格中的“是否此部门主管”字段的值来确定
													int ret = deptUserService.addDeptUserInfo(du_tmp);
													if(ret > 0) {	// 如果添加部门-员工对应关系信息成功
														// TODO: 添加成功后要做的事情。。。
													} else {	// 如果添加部门-员工对应关系信息时出现异常，则首先抛出异常，然后跳到下一次循环
														try {
															throw new Exception("添加部门-员工对应关系信息时出现异常:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
														} catch (Exception e) {
															e.printStackTrace();
														}
														continue;
													}
												} else {	// 如果在数据库中已经存在当前员工与部门之间的对应关系信息，则在后台日志输出这条部门-员工对应关系信息，然后直接跳到下一次循环
													logger.info("在数据库中已存在该部门-员工对应关系信息:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
													continue;
												}
											}
										} else {	// 如果这个部门名与根部门名不同，则说明当前要导入的员工是根部门下的直属部门的员工
											// 首先在数据库部门信息表中查询是否已经存在有该部门的信息，如果不存在，则先要在部门信息表中添加该部门的信息；如果已存在，则无须再添加
											EpDept ed_query = new EpDept();
											ed_query.setName(dept_one_trim);
											ed_query.setLevel(2); 	// 根部门下的直属部门的部门层级数定义为2
											List<EpDept> ed_res = epDeptService.findEpDeptByCriteria(ed_query);
											if(ed_res.size() > 0) {
												if(ed_res.size() == 1) {
													// 取得仅有的这条部门信息
													EpDept ed_res_one = ed_res.get(0);
													// 往数据库表dept_clue中添加一条部门线索信息
													DeptClue dc = new DeptClue();
													dc.setCompany_name(company_name);
													dc.setClue_str(clue_trim);
													dc.setLevel_count(2); 	// 根部门下的直属部门层级数设为2
													dc.setLast_dept_id(ed_res_one.getId());	// 如果部门线索信息中仅有一个根部门的直属部门，那么这条部门线索信息的最后一个部门的部门id也就是这个根部门的直属部门的id
													deptClueService.addDeptClue(dc);
													// 添加完部门线索信息。。。
													// 开始添加部门-员工对应关系信息
													EpUser eu_tmp = epu;	// 默认把将要建立部门-员工对应关系的员工引用指向新员工epu
													if(epu.getId() > 0 && res_one == null) {	// 如果当前导入的这条员工信息是一名新员工的（导入之前数据库中不存在该名员工的信息），
													}
													if(epu.getId() == 0 && res_one != null) {	// 如果当前导入的这条员工信息是一名老员工的（导入之前数据库中已存在该名员工的信息），
														eu_tmp = res_one;	// 将将要建立部门-员工对应关系的员工引用指向老员工res_one
													}
													DeptUser du_tmp = new DeptUser();
													du_tmp.setDept_id(ed_res_one.getId());
													du_tmp.setEp_uid(eu_tmp.getId());
													DeptUser du_res = deptUserService.getDeptUserByDeptIdAndUid(du_tmp);
													if(du_res == null) {	// 如果在数据库中当前员工与部门之间的对应关系为空，即当前员工的部门归属信息未在数据库中出现过时，则需要在数据库中新添加一条部门-员工对应关系
														du_tmp.setIs_manager(is_manager_ints[clue_index]);	// 暂时设置默认不是部门主管，后面再根据具体导入的Excel表格中的“是否此部门主管”字段的值来确定
														int ret = deptUserService.addDeptUserInfo(du_tmp);
														if(ret > 0) {	// 如果添加部门-员工对应关系信息成功
															// TODO: 添加成功后要做的事情。。。
														} else {	// 如果添加部门-员工对应关系信息时出现异常，则首先抛出异常，然后跳到下一次循环
															try {
																throw new Exception("添加部门-员工对应关系信息时出现异常:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
															} catch (Exception e) {
																e.printStackTrace();
															}
															continue;
														}
													} else {	// 如果在数据库中已经存在当前员工与部门之间的对应关系信息，则在后台日志输出这条部门-员工对应关系信息，然后直接跳到下一次循环
														logger.info("在数据库中已存在该部门-员工对应关系信息:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
														continue;
													}
												} else {
													try {
														throw new Exception("在同一部门层级上出现两个或多个同名的部门，系统无法确定对哪个部门进行操作：（部门名=" + dept_one_trim + ", 部门层级=2）");
													} catch (Exception e) {
														e.printStackTrace();
													}
													continue;	// 出现异常后，直接跳到下一次循环，不做后续处理
												}
											} else {
												// 先查询出根部门的部门信息
												EpDept ed_root = new EpDept();
												ed_root.setName(company_name);	// 根部门的部门名为前面从Excel表格数据中解析出的company_name字段的值
												ed_root.setLevel(1); 	// 根部门的部门层级数定义为1
												List<EpDept> res_root = epDeptService.findEpDeptByCriteria(ed_root);
												if(res_root.size() > 0) {	// 如果数据库中存在该根部门的信息
													if(res_root.size() == 1) {	// 如果有且仅有一条该根部门的信息（正常情况）
														ed_root = res_root.get(0);	// 直接将这条仅有的部门信息赋给作为查询条件的部门信息变量ed_root
													} else {	// 如果有多条根部门的信息（异常情况）
														try {
															throw new Exception("出现两个或多个同名的根部门，系统无法确定对哪个根部门进行操作：（部门名=" + company_name + ", 部门层级=1）");
														} catch (Exception e) {
															e.printStackTrace();
														}
														continue;	// 出现异常后，直接跳到下一次循环，不做后续处理
													}
												} else {	// 如果数据库中不存在根部门的信息，则先创建一条根部门的信息，然后再进行后续操作
													ed_root.setSuperior_id(0); 	// 将根部门的上级部门id设为0
													epDeptService.addEpDept(ed_root);	// 添加完根部门后，ed_root对象的id属性就会被自动赋上数据库中相对应的主键id值
												}
												// 开始新添加一条根部门下的直属部门信息
												EpDept ed_add = new EpDept();
												ed_add.setName(dept_one_trim);
												ed_add.setLevel(2);
												ed_add.setSuperior_id(ed_root.getId());	// 根部门下的直属部门的上级部门id即为根部门的id
												epDeptService.addEpDept(ed_add);
												// 新添加完一条根部门下的直属部门信息。。。
												// 往数据库表dept_clue中添加一条部门线索信息
												DeptClue dc = new DeptClue();
												dc.setCompany_name(company_name);
												dc.setClue_str(clue_trim);
												dc.setLevel_count(2); 	// 根部门下的直属部门层级数设为2
												dc.setLast_dept_id(ed_add.getId());	// 如果部门线索信息中仅有一个根部门的直属部门，那么这条部门线索信息的最后一个部门的部门id也就是这个根部门的直属部门的id
												deptClueService.addDeptClue(dc);
												// 添加完部门线索信息。。。
												// 开始添加部门-员工对应关系信息
												EpUser eu_tmp = epu;	// 默认把将要建立部门-员工对应关系的员工引用指向新员工epu
												if(epu.getId() > 0 && res_one == null) {	// 如果当前导入的这条员工信息是一名新员工的（导入之前数据库中不存在该名员工的信息），
												}
												if(epu.getId() == 0 && res_one != null) {	// 如果当前导入的这条员工信息是一名老员工的（导入之前数据库中已存在该名员工的信息），
													eu_tmp = res_one;	// 将将要建立部门-员工对应关系的员工引用指向老员工res_one
												}
												DeptUser du_tmp = new DeptUser();
												du_tmp.setDept_id(ed_add.getId());
												du_tmp.setEp_uid(eu_tmp.getId());
												DeptUser du_res = deptUserService.getDeptUserByDeptIdAndUid(du_tmp);
												if(du_res == null) {	// 如果在数据库中当前员工与部门之间的对应关系为空，即当前员工的部门归属信息未在数据库中出现过时，则需要在数据库中新添加一条部门-员工对应关系
													du_tmp.setIs_manager(is_manager_ints[clue_index]);	// 暂时设置默认不是部门主管，后面再根据具体导入的Excel表格中的“是否此部门主管”字段的值来确定
													int ret = deptUserService.addDeptUserInfo(du_tmp);
													if(ret > 0) {	// 如果添加部门-员工对应关系信息成功
														// TODO: 添加成功后要做的事情。。。
													} else {	// 如果添加部门-员工对应关系信息时出现异常，则首先抛出异常，然后跳到下一次循环
														try {
															throw new Exception("添加部门-员工对应关系信息时出现异常:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
														} catch (Exception e) {
															e.printStackTrace();
														}
														continue;
													}
												} else {	// 如果在数据库中已经存在当前员工与部门之间的对应关系信息，则在后台日志输出这条部门-员工对应关系信息，然后直接跳到下一次循环
													logger.info("在数据库中已存在该部门-员工对应关系信息:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
													continue;
												}
											}
										}
									} else {	// 如果trim后的部门名是空白字符串，则直接跳到下一个循环，解析下一条部门线索信息
										continue;
									}
								} else {	// 如果从部门线索字符串中解析出来的部门名个数大于1（即至少有2个部门名）时，说明这条线索是以第2级部门打头，后面跟着一个或多个下级部门的一条线索
									// 开始逐个解析部门线索信息中的每个部门。。。
									// 如果这条线索中出现的部门未在数据库中出现，则应当首先创建一个新部门，然后创建新的部门-部门、部门线索信息，以及部门-员工关联关系
									int dept_count = dept_strs.length;	// 解析出来的部门个数
									int level_count = dept_count + 1;	// 最后一个部门的部门层级数应为解析出来的部门个数加1
									
									// 创建一个临时的部门信息数组，用于存放解析出来的每个部门的信息
									List<EpDept> dept_list = new ArrayList<EpDept>();
									for(int i=0; i<dept_count; i++) {	// 初始化部门信息数组
										dept_list.add(null);	// 数组中部门的初始值都设为null
									}
									
									for(int i = 0; i < dept_count; i++) {	// 开始循环逐个解析每个部门名
										String dept_name = dept_strs[i];	// 取出第(i+1)个部门名
										// 首先在数据库中查询是否已经存有该部门的信息（采用“部门名+部门层级数”双重条件来查询）
										EpDept ed_query = new EpDept();
										ed_query.setName(dept_name);
										ed_query.setLevel(i+2);	// 第(i+1)个部门的部门层级数为(i+2)
										List<EpDept> ret_depts = epDeptService.findEpDeptByCriteria(ed_query);
										if(ret_depts != null) {
											if(ret_depts.size() > 0) {
												if(ret_depts.size() == 1) {	// 如果数据库中有且仅有一个已知部门信息与“部门名+部门层级数”双重条件相匹配（正常情况）
													EpDept ret_dept_one = ret_depts.get(0);	// 取出这个仅有的部门信息
													dept_list.set(i, ret_dept_one);	// 把这个部门信息放到临时的部门信息数组的第(i+1)个位置上
												} else {	// TODO: 如果数据库中有多个已知部门信息与“部门名+部门层级数”双重条件相匹配（异常情况），
													try {	// 抛出异常，跳出循环，后面程序应当控制进入下一条部门线索的解析
														throw new Exception("出现两个或多个同名的同级部门，系统无法确定对哪个部门进行操作：（部门名=" + dept_name + ", 部门层级=" + (i+2) + "）");
													} catch (Exception e) {
														e.printStackTrace();
													}
													break;
												}
											} else {	// 如果数据库中没有已知部门信息与“部门名+部门层级数”双重条件相匹配，则应先创建一个新部门信息
												// 首先要确定当前部门的上级部门id
												int parent_dept_id = 0;	// 用来存放上级部门的id
												if(i == 0) {	// 如果是部门线索中排在第一位的部门，则它的上级部门就是根部门
													// 先查询出根部门的部门信息
													EpDept ed_root = new EpDept();
													ed_root.setName(company_name);	// 根部门的部门名为前面从Excel表格数据中解析出的company_name字段的值
													ed_root.setLevel(1); 	// 根部门的部门层级数定义为1
													List<EpDept> res_root = epDeptService.findEpDeptByCriteria(ed_root);
													if(res_root.size() > 0) {	// 如果数据库中存在该根部门的信息
														if(res_root.size() == 1) {	// 如果有且仅有一条该根部门的信息（正常情况）
															ed_root = res_root.get(0);	// 直接将这条仅有的部门信息赋给作为查询条件的部门信息变量ed_root
														} else {	// 如果有多条根部门的信息（异常情况）
															try {
																throw new Exception("出现两个或多个同名的根部门，系统无法确定对哪个根部门进行操作：（部门名=" + company_name + ", 部门层级=1）");
															} catch (Exception e) {
																e.printStackTrace();
															}
															break;	// TODO: 出现异常后，直接跳出循环，放弃对当前部门线索的解析，跳出后应控制程序进入下一条部门线索的解析
														}
													} else {	// 如果数据库中不存在根部门的信息，则先创建一条根部门的信息，然后再进行后续操作
														ed_root.setSuperior_id(0); 	// 将根部门的上级部门id设为0
														epDeptService.addEpDept(ed_root);	// 添加完根部门后，ed_root对象的id属性就会被自动赋上数据库中相对应的主键id值
													}
													parent_dept_id = ed_root.getId();	// 设置当前排在部门线索中第一位的部门的上级部门id为根部门的部门id
													
												} else {	// 如果是部门线索中排在第一位之后的部门，则它的上级部门应当已经在之前就已经创建过了（因为我是按照从左到右的顺序依次遍历过每个部门的，
															// 没有在数据库中的部门都会创建一个新的部门。如果查询不到它的上级部门，则认为是出现了异常，只抛出异常，不做进一步处理）
													// 先从数据库中查出部门线索中排在当前部门前面的那个部门的部门信息，以便确认当前部门的上级部门的id
													EpDept ed_pre = new EpDept();
													ed_pre.setName(dept_strs[i-1]);	// 部门线索中排在当前部门前面的那个部门的部门名应为解析出来的部门名数组dept_strs中的第i个字符串
													ed_pre.setLevel(i+1);	// 部门线索中排在当前部门前面的那个部门的部门层级数应为(i+1)
													List<EpDept> res_pre = epDeptService.findEpDeptByCriteria(ed_pre);
													if(res_pre != null) {
														if(res_pre.size() > 0) {
															if(res_pre.size() == 1) {
																ed_pre = res_pre.get(0);	// 查出的唯一一条部门信息应当就是当前部门的上级部门的信息
																parent_dept_id = ed_pre.getId();
															} else {
																try {	// 抛出异常，跳出循环，后面程序应当控制进入下一条部门线索的解析
																	throw new Exception("出现两个或多个同名的同级部门，系统无法确定对哪个部门进行操作：（部门名=" + dept_strs[i-1] + ", 部门层级=" + (i+1) + "）");
																} catch (Exception e) {
																	e.printStackTrace();
																}
																break;
															}
														} else {
															try {	// 抛出异常，跳出循环，后面程序应当控制进入下一条部门线索的解析
																throw new Exception("找不到与之匹配的部门信息：（部门名=" + dept_strs[i-1] + ", 部门层级=" + (i+1) + "）");
															} catch (Exception e) {
																e.printStackTrace();
															}
															break;
														}
													} else {
														try {	// TODO: 出现异常情况后，应当控制程序进入下一条部门线索信息的解析
															throw new Exception("在数据库中查询当前部门的上级部门信息时出现异常");
														} catch (Exception e) {
															e.printStackTrace();
														}
														break;
													}
												}
												
												// 开始创建一个新的部门信息。。。
												EpDept ed_new = new EpDept();
												ed_new.setName(dept_name);
												ed_new.setLevel(i+2);
												ed_new.setSuperior_id(parent_dept_id);
												epDeptService.addEpDept(ed_new);
												// 创建新的部门信息完成。。。
												// 然后把这个新创建的部门信息加入到部门信息数组中第(i+1)个位置上去
												dept_list.set(i, ed_new);
											}
										} else {	// 如果查询数据库时出现异常，返回结果为空指针null，则直接抛出异常，不做进一步处理
											try {
												throw new Exception("在数据库中查询部门信息时出现异常");
											} catch (Exception e) {
												e.printStackTrace();
											}
											break;
										}
										
									}
									
									
									boolean all_dept_ok = true;	// 是否部门线索信息中所有部门都被正常解析出来的标志
									for(int i=0; i<dept_count; i++) {
										if(dept_list.get(i) == null) {
											all_dept_ok = false;	// 只要有一个部门信息为空null，就说明解析未完全成功
										}
									}
									// 取出部门线索信息中最后一个部门的信息
									EpDept dept_last = null;
									if(all_dept_ok) {	// 如果部门线索信息中的所有部门都被正常解析出来，存放进了部门信息数组dept_list中
										dept_last = dept_list.get(dept_count-1);	// 部门线索中的最后一个部门在部门信息数组dept_list中的下标是(dept_count-1)，即部门个数减1
									} else {	// 如果解析出来的部门数组不完整，说明解析过程中出现了异常，应当舍弃这一条部门线索的解析，然后抛出异常
										try {
											throw new Exception("解析部门线索信息得到的结果异常");
										} catch (Exception e) {
											e.printStackTrace();
										}
										continue;	// 抛出异常信息后，直接跳到下一条部门线索信息
									}
									
									// 处理完线索信息字符串中的每个部门后，再将部门-员工之间的关联关系存入数据库中
									// 开始添加部门-员工对应关系信息。。。
									EpUser eu_tmp = epu;	// 默认把将要建立部门-员工对应关系的员工引用指向新员工epu
									if(epu.getId() > 0 && res_one == null) {	// 如果当前导入的这条员工信息是一名新员工的（导入之前数据库中不存在该名员工的信息），
									}
									if(epu.getId() == 0 && res_one != null) {	// 如果当前导入的这条员工信息是一名老员工的（导入之前数据库中已存在该名员工的信息），
										eu_tmp = res_one;	// 将将要建立部门-员工对应关系的员工引用指向老员工res_one
									}
									DeptUser du_tmp = new DeptUser();
									if(dept_last != null) {
										du_tmp.setDept_id(dept_last.getId());	// 设置部门-员工关联关系中的部门id为部门线索信息中最后一个部门的部门id
									}
									du_tmp.setEp_uid(eu_tmp.getId());
									DeptUser du_res = deptUserService.getDeptUserByDeptIdAndUid(du_tmp);
									if(du_res == null) {	// 如果在数据库中当前员工与部门之间的对应关系为空，即当前员工的部门归属信息未在数据库中出现过时，则需要在数据库中新添加一条部门-员工对应关系
										du_tmp.setIs_manager(is_manager_ints[clue_index]);	// 暂时设置默认不是部门主管，后面再根据具体导入的Excel表格中的“是否此部门主管”字段的值来确定
										int ret = deptUserService.addDeptUserInfo(du_tmp);
										if(ret > 0) {	// 如果添加部门-员工对应关系信息成功
											// TODO: 添加成功后要做的事情。。。
										} else {	// 如果添加部门-员工对应关系信息时出现异常，则首先抛出异常，然后跳到下一次循环
											try {
												throw new Exception("添加部门-员工对应关系信息时出现异常:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
											} catch (Exception e) {
												e.printStackTrace();
											}
											continue;
										}
									} else {	// 如果在数据库中已经存在当前员工与部门之间的对应关系信息，则在后台日志输出这条部门-员工对应关系信息，然后直接跳到下一次循环
										logger.info("在数据库中已存在该部门-员工对应关系信息:(部门id=" + du_tmp.getDept_id() + ", 员工uid=" + du_tmp.getEp_uid() + ")");
										continue;
									}
									// 添加完部门-员工对应关系信息。。。
									
									// 应当在完全解析完当前部门线索信息后，再把这条部门线索信息字符串存入到数据库表dept_clue中去
									DeptClue dc = new DeptClue();
									dc.setCompany_name(company_name);
									dc.setClue_str(clue_trim);
									dc.setLevel_count(level_count); 	// 设置这条部门线索信息的层级总数（其实也就是最后一个部门的部门层级数）
									if(dept_last != null) {
										dc.setLast_dept_id(dept_last.getId());	// 设置这条线索中最后一个部门的id
									}
									deptClueService.addDeptClue(dc);
									// 添加完部门线索信息。。。
								}
							} else {	// 如果解析出来的部门名个数为0，说明这是一条空的部门线索信息，应不做任何处理，直接跳到下一条部门线索信息
								continue;
							}
						}
					}
					
					// 建立部门-员工、部门-部门之间的关系，并将这种关系存入数据库（在前面各种if分支中已做处理）
					
				}
				
				if(ja_blank_error.size() == 0 && ja_dup_phone.size() == 0) {	// 如果导入员工信息Excel表的过程中未出现错误，则可以返回正常提示信息
					json_obj.put("ret_code", 1);
					json_obj.put("ret_msg", "导入员工信息成功！");
				}
				
				if(ja_blank_error.size() > 0) {	// 如果出现必填字段为空的数据行数至少有一行时（简言之，就是至少有一行异常数据时），
																	// 在返回结果中附上详细的异常数据清单，并进行分组归类
					json_obj.put("ret_code", -2);
					json_obj.put("ret_msg", "导入数据中至少有一行出现必填字段为空的情况");
					json_obj.put("blank_error", ja_blank_error);	// 将所有必填字段有为空的行一起放在blank_error字段里返回
					if(ja_dup_phone.size() > 0) {
						json_obj.put("dup_phone", ja_dup_phone);	// 如果有重复手机号的员工信息时，可以在返回结果中看到这部分追加员工部门信息的数据
					}
					return json_obj;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return json_obj;
	}
	
	
	/**
	 * 刷新部门id线索信息
	 * 
	 * 当传入的根部门id为null或负数或0时，默认刷新所有根部门下的部门id线索；
	 * 当传入的根部门id不为null且为正数时，如果这个部门不存在或不是根部门（根部门：上级id为0的部门），则不进行刷新操作；
	 * 		如果这个部门存在且是根部门，才开始进行刷新操作。
	 * @param root_did
	 */
	public void refreshDeptIdClue(Integer root_did) {
		EpDept epd_que = new EpDept();
		epd_que.setSuperior_id(0);
		DeptIdClueUtil dicu = new DeptIdClueUtil();
		
		if(root_did == null || root_did <= 0) {	// 刷新所有根部门下的部门id线索
			List<EpDept> root_epds = epDeptService.findEpDeptByCriteria(epd_que);
			if(root_epds.size() > 0) {
				for(EpDept epd:root_epds) {
					Integer epd_id = epd.getId();
					if(epd_id == null || epd_id <= 0) {
						continue;
					}
					refreshDeptIdClue(epd_id);
				}
			}
		} else {
			epd_que.setId(root_did);
//			epd_que.setSuperior_id(0);
			List<EpDept> root_epd = epDeptService.findEpDeptByCriteria(epd_que);
			if(root_epd.size() == 1) {
				EpDept root_one = root_epd.get(0);
				EpDept epd_tree = dicu.genEpDeptTree(root_did);
				dicu.iterateEpDept(epd_tree, new Stack<EpDept>());
				TreeMap<Integer, ArrayList<EpDept>> path_map = dicu.getPath_map();
				int rmv_count = deptIdClueService.removeDeptIdClueByRootDid(root_did);
				logger.info("已移除部门id线索信息串( " + rmv_count + " )条");
				TreeMap<Integer, String> trans_path_map = dicu.transPathMap(path_map);
				Collection<String> path_values = trans_path_map.values();
				for(String path:path_values) {
					DeptIdClue dic_new = new DeptIdClue();
					dic_new.setDept_id_clue(path);
					dic_new.setCompany_name(root_one.getName());
					deptIdClueService.addOneDeptIdClue(dic_new);
				}
			}
		}
	}
	
	
	public String[] genDeptIdClueStrsByRootDeptId(Integer rootDid) {
		if(rootDid == null || rootDid <= 0) {	// 如果传入的根部门id为null或负数或0时，直接返回null
			return null;
		}
		
//		if(rootDid == 0) {
//			return null;
//		}
		
		EpDept ed_que = new EpDept();
		ed_que.setSuperior_id(rootDid);
		List<EpDept> sub_depts = epDeptService.findEpDeptByCriteria(ed_que);
		if(sub_depts == null) {
			return null;
		}
		
		int sub_count = sub_depts.size();
		if(sub_count == 0) {
			return new String[]{"-" + rootDid + "-"};
		}
		
		String[] strs1 = new String[sub_count];
		int strs1_len = strs1.length;
		
		for(int i=0; i<strs1_len; i++) {
			EpDept epd = sub_depts.get(i);
			Integer epd_id = epd.getId();
			if(epd_id == null || epd_id <= 0) {
				return null;
			}
//			EpDept epd_que = new EpDept();
//			epd_que.setSuperior_id(epd_id);
//			List<EpDept> sub_epds = epDeptService.findEpDeptByCriteria(epd_que);
//			if(sub_epds == null) {
//				return null;
//			}
			String[] strs2 = genDeptIdClueStrsByRootDeptId(epd_id);
			int strs2_len = strs2.length;
			for(int j=0; j< strs2_len; j++) {
				strs2[j] = "-" + epd_id + strs1[i];
			}
//			return strs2;
		}
		
		return strs1;
	}
	
	
	public static void main(String[] args) {
		/*
		Employee emp1 = new Employee();
		Employee emp2 = new Employee();
		Employee emp3 = new Employee();
		Employee emp4 = new Employee();
		Employee emp5 = new Employee();
		
		emp1.setName("aaaa");
		emp2.setName("bbbb");
		emp3.setName("cccc");
		emp4.setName("dddd");
		emp5.setName("eeee");
		
		List<Employee> emp_list = new ArrayList<Employee>();
		emp_list.add(emp1);
		emp_list.add(emp2);
		emp_list.add(emp3);
		emp_list.add(emp4);
		emp_list.add(emp5);
		
		Object obj = JSON.toJSON(emp1);
		Object obj_list = JSON.toJSON(emp_list);
		
		JSONArray ja = new JSONArray();
		ja.add(emp1);
		ja.add(emp2);
		ja.add(emp3);
		
		JSONArray ja2 = new JSONArray();
		ja2.add(JSON.toJSON(emp3));
		ja2.add(JSON.toJSON(emp4));
		ja2.add(JSON.toJSON(emp5));
		
		System.out.println(obj.getClass());
		System.out.println(obj);
		System.out.println("----------------");
		System.out.println(obj_list.getClass());
		System.out.println(obj_list);
		System.out.println("--------------------------");
		System.out.println(ja);
		System.out.println("--------------------------============");
		System.out.println(ja2);
		*/
		
		/*
		String str = ",a,,c,dd, ";
		String[] str_split = str.split(",");
		System.out.println("len = " + str_split.length);
		for(String str1:str_split) {
			System.out.println(str1);
		}
		*/
		
		/*
		String str = " \t hkhl jkj \tn\n  90909  \n ";
		System.out.println(str.trim());
		*/
		
		/*
		String str = "abcd";
		System.out.println(str.hashCode());
		*/
		
		/*
		String str = "jk-kkd [[] -   &&& - --45";
		str = "密码喵喵喵dsds";
		String[] str_split = str.split("-");
		System.out.println("len = " + str_split.length);
		for(String str1:str_split) {
			System.out.println(str1);
			System.out.println("TorF : " + str1.equals(str));
		}
		*/
		
		/*
		List<Integer> inl = new ArrayList<Integer>();
		for(int i=0; i<12; i++) {
			inl.add(null);	// 初始化整数对象数组inl
		}
		inl.set(0, 8);
		inl.add(1);
		inl.add(2, 20);
		System.out.println("inl size = " + inl.size());
		System.out.println("inl index(1) = " + inl.get(1));
		System.out.println("--------------");
		for(Integer in:inl) {
			System.out.println(in + ", ");
		}
		*/
		
		/*
		String[] strs = new String[]{};
		System.out.println("strs len = " + strs.length);
		*/
		
		/*
		String xls_file_path = "E:/TempE/14/12971031_employee(2).xls";
		JSONObject imp_ret = new EpDataController().impEmpInfo(xls_file_path);
		System.out.println(imp_ret);
		*/
		
		
		
		
	}

}

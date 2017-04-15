package com.cn.eplat.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.util.TextUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.b510b.excel.ReadExcel;
import com.cn.eplat.consts.Constants;
import com.cn.eplat.service.IMobileVersionsService;
import com.cn.eplat.service.IRestCalendarService;
import com.cn.eplat.utils.db2excel.DbToExcelUtil;
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/upLoadFileController")
public class UpLoadFileController {
	
	@Resource
	private IRestCalendarService restCalendarService;
	
	@Resource
	private IMobileVersionsService mobileVersionsService;
	
	@RequestMapping(params = "gotoImportRestCalendar")
	public String gotoImportRestCalendarPage(HttpServletRequest request) {
		return "fileUpload/importRestCalendar";
	}
	
	@RequestMapping(params = "importRestCalendar")
	public String importRestCalendarToDb(HttpServletRequest request,HttpServletResponse response,@RequestParam("importFile") MultipartFile multipartFile) {
		String info = "toast/handledFail";
		String excelPath = DbToExcelUtil.saveFileToServer(request,multipartFile, "\\filesOut\\Upload");
		if (!StringUtils.isNullOrEmpty(excelPath)) {
			TreeMap<Date, String> datas = new ReadExcel().readDatesFromExcel(excelPath);
			List<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
			List<Date> deletes = new ArrayList<Date>();
			if (datas != null && !datas.isEmpty()) {
				 List<HashMap<String, Object>> existDatas = restCalendarService.getDatesBetweenStartAndEnd(datas.firstKey(),datas.lastKey());
				 if(existDatas != null && !existDatas.isEmpty()){
					 for (HashMap<String, Object> hashMap : existDatas) {
						 Date date = (Date)hashMap.get("rest_time");
						 String type = (String)hashMap.get("type");
						 if(datas.containsKey(date)){
							 if(type != null && type.equals(datas.get(date))){
								 datas.remove(date);
							 }else{
								 deletes.add(date);
								 datas.put(date, combineString(datas.get(date), type));
							 }
						 }
					 }
				 }
				 
				 if(!deletes.isEmpty()){
					 restCalendarService.deleteRecordsByDates(deletes);//批量删除重复数据
				 }
				 
				Set<Entry<Date,String>> entrySet = datas.entrySet();
				Iterator<Entry<Date, String>> iterator = entrySet.iterator();
				while(iterator.hasNext()){
					HashMap<String, Object> map = new HashMap<String, Object>();
					Entry<Date, String> next = iterator.next();
					Date key = next.getKey();
					String value = next.getValue();
					map.put("rest_time", key);
					map.put("type", value);
					result.add(map);
				}
				
				if(!result.isEmpty()){
					int success = restCalendarService.insertRestCalendar(result);
					if(success > 0){
						info = "toast/handledSuccess";
					}else{
						info = "toast/handledFail";
					}
				}else{
					info = "toast/handledSuccess";
				}
			}
		}
		return info;
	}
	
	/**
	 * 将2个字符串通过“,”连接起来，去掉重复的字符
	 * @param before
	 * @param after
	 * @return
	 */
	private String combineString(String before,String after){
		if(TextUtils.isEmpty(before))before = "";
		if(TextUtils.isEmpty(after))after = "";
		String[] split = after.split(",");
		for (String string : split) {
			if(!before.contains(string)){
				before += ","+string;
			}
		}
		return before;
	}
	
	@RequestMapping(params = "gotoUpLoadApk")
	public String gotoUpLoadAndroidApk(HttpServletRequest request) {
		return "fileUpload/UpLoadNewAndroidApk";
	}
	
	//上传Android apk到服务器
	@RequestMapping(params = "upLoadAndroidApk")
	public String upLoadNewAndroidApk(HttpServletRequest request,HttpServletResponse response,@RequestParam("importFile") MultipartFile multipartFile) {
		String info = "toast/handledFail";
		String version = request.getParameter("version");
		if(TextUtils.isEmpty(version)) return info;
		
		String point = request.getParameter("point");
		String name = multipartFile.getOriginalFilename();
		String projectPath = request.getSession().getServletContext().getRealPath("/");
		File  file = new File(projectPath);
		String appPath = file.getParent();
		String absolutePath = appPath + Constants.ANDROID_APKS_DIR;
		String apkPath = DbToExcelUtil.saveFileToServerByAbsolutePath(request,multipartFile, absolutePath);
		if(!TextUtils.isEmpty(apkPath)){
			Integer id = mobileVersionsService.getIdByVersion(version);
			if(id != null && id > 0){
				int updateSuccess = mobileVersionsService.updateMobileVersionByVersion(version, "android", point, name);
				if(updateSuccess > 0){
					info = "toast/handledSuccess";
				}
			}else{
				int result = mobileVersionsService.insertMobileVersion(version, "android", point,name);
				if(result > 0 ){
					info = "toast/handledSuccess";
				}
			}
		}
		return info;
	}
	
	/**
	 * android端获取最新的版本号（用作版本升级用）
	 */
	@RequestMapping(params="getLatestVersionNumber",produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String  getLatestVersionNumber(HttpServletRequest request){
		JSONObject object = new JSONObject();
		HashMap<String, String> map = mobileVersionsService.getLatestVersion();
		if(map == null){
			object.put("ret_code", 0);
			object.put("ret_message", "无数据！");
			return JSONObject.toJSONString(object,SerializerFeature.WriteMapNullValue);
		}
		
		if(!map.containsKey("version")){
			object.put("ret_code", -1);
			object.put("ret_message", "获取最新版本失败！");
			return JSONObject.toJSONString(object,SerializerFeature.WriteMapNullValue);
		}
		
		String version = map.get("version");
		String point = map.get("point");
		String name = map.get("name");
		if(TextUtils.isEmpty(version) || TextUtils.isEmpty(name)){
			object.put("ret_code", -2);
			object.put("ret_message", "版本数据异常！");
			return JSONObject.toJSONString(object,SerializerFeature.WriteMapNullValue);
		}
		
		object.put("ret_code", 1);
		object.put("ret_message", "获取最新版本成功！");
		object.put("version", version);
		object.put("url", Constants.ANDROID_APKS_URL+name);
		object.put("point", point);
		return JSONObject.toJSONString(object,SerializerFeature.WriteMapNullValue);
	}
	
	/**
	 * android端下载最新版本URL（用作重定向二维码扫描下载apk）
	 */
	@RequestMapping(params="downLoadNewApk")
	public String downLoadAndroidApk(HttpServletRequest request,HttpServletResponse response) {
		try {
			HashMap<String, String> map = mobileVersionsService.getLatestVersion();
			if (map != null) {
				if (map.containsKey("version")) {
					String version = map.get("version");
					String name = map.get("name");
					if (!TextUtils.isEmpty(version) && !TextUtils.isEmpty(name)) {
						String projectPath = request.getSession().getServletContext().getRealPath("/");
						File file = new File(projectPath);
						String appPath = file.getParent();
						String absolutePath = appPath+ Constants.ANDROID_APKS_DIR;
						String filePath = absolutePath + "/" + name;
						File f = new File(filePath);
						if(f.exists()){
							DbToExcelUtil.downLoadFile(request, response, filePath);
						}
					}
				}
			}
		} catch (Exception e) {

		}
		return null;
	}
}

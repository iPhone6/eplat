package com.cn.eplat.utils.db2excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.cn.eplat.dao.IEpAttenDao;
import com.cn.eplat.model.EpAttenExport;

/**
 * 将数据库里的考勤数据导出的工具类
 * @author zhangshun
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class DbToExcelUtil {
	
	@Resource
	private IEpAttenDao epAttenDao;
	
	@Test
	public static String exportAttendDatasToExcel(List<EpAttenExport> attenDatas) {
		
		attenDatas = getValidAttendDatas(attenDatas);
		
		Date date = new Date();
		SimpleDateFormat itemSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = itemSdf.format(date);
		String excelPath = "D:/AttendDatas/考勤表-" + format.replace(":", "-") + ".xls";
		try {
			File file = new File(excelPath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			// 以fileName为文件名来创建一个Workbook
			WritableWorkbook wwb = Workbook.createWorkbook(file);
			// 创建工作表
			WritableSheet ws = wwb.createSheet("考勤数据", 0);

			// 查询数据库中所有的数据
//			List<EpAttenExport> attenDatas = epAttenDao.queryAllEpAttenExportDatas();
			// 要插入到的Excel表格的行号，默认从0开始
			Label id = new Label(0, 0, "序号");
			Label name = new Label(1, 0, "员工姓名");
			Label Type = new Label(2, 0, "打卡类型");
			Label longtitude = new Label(3, 0, "经度");
			Label latitude = new Label(4, 0, "纬度");
			Label wifi_name = new Label(5, 0, "wifi名字");
			Label time = new Label(6, 0, "打卡时间");
			Label platform = new Label(7, 0, "客户端平台");

			ws.addCell(id);
			ws.addCell(name);
			ws.addCell(Type);
			ws.addCell(longtitude);
			ws.addCell(latitude);
			ws.addCell(wifi_name);
			ws.addCell(time);
			ws.addCell(platform);
			
			for (int i = 0; i < attenDatas.size(); i++) {
				id = new Label(0, i + 1, String.valueOf(i+1));
				name = new Label(1, i + 1, String.valueOf(attenDatas.get(i).getName()));
				Type = new Label(2, i + 1, attenDatas.get(i).getType());
				if(StringUtils.isEmpty(String.valueOf( attenDatas.get(i).getLongtitude()))||"null".equals(String.valueOf( attenDatas.get(i).getLongtitude()))){
					longtitude = new Label(3, i + 1,"无");
				}else{
					longtitude = new Label(3, i + 1,String.valueOf( attenDatas.get(i).getLongtitude()));
				}
				
				if(StringUtils.isEmpty(String.valueOf( attenDatas.get(i).getLatitude()))||"null".equals(String.valueOf( attenDatas.get(i).getLatitude()))){
					latitude = new Label(4, i + 1, "无");
				}else{
					latitude = new Label(4, i + 1, String.valueOf(attenDatas.get(i).getLatitude()));
				}
				
				if(StringUtils.isEmpty(attenDatas.get(i).getWifi_name())||"<EMPTY>".equals(attenDatas.get(i).getWifi_name())){
					wifi_name = new Label(5, i + 1, "无");
				}else{
					wifi_name = new Label(5, i + 1, attenDatas.get(i).getWifi_name());
				}
				
				time = new Label(6, i + 1,itemSdf.format(attenDatas.get(i).getTime()));
				platform = new Label(7, i + 1, attenDatas.get(i).getPlatform());
				
				ws.addCell(id);
				ws.addCell(name);
				ws.addCell(Type);
				ws.addCell(longtitude);
				ws.addCell(latitude);
				ws.addCell(wifi_name);
				ws.addCell(time);
				ws.addCell(platform);
			}

			// 写进文档
			wwb.write();
			// 关闭Excel工作簿对象
			wwb.close();
			return excelPath;
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return "";
	}
	
	/**
	 * 从数据源里找出所有的有效的打卡数据
	 * @param attenDatas
	 * @return
	 */
	public static List<EpAttenExport> getValidAttendDatas(List<EpAttenExport> attenDatas){
		if(attenDatas == null || attenDatas.isEmpty()) return new ArrayList<EpAttenExport>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		HashMap<String,TreeMap<String,List<EpAttenExport>>>  maps = new HashMap<>();
		
		for (EpAttenExport epAttenExport : attenDatas) {
			TreeMap<String,List<EpAttenExport>> childMaps = new TreeMap<>();
			if(maps.containsKey(epAttenExport.getEmail())){
				childMaps = maps.get(epAttenExport.getEmail());
			}else{
				maps.put(epAttenExport.getEmail(), childMaps);
			}
			
			String time = sdf.format(epAttenExport.getTime());
			List<EpAttenExport>  childList = new ArrayList<>();
			if(childMaps.containsKey(time)){
				childList = childMaps.get(time);
				childList.add(epAttenExport);
				if(childList.size() > 2){
					sortAttendDatas(childList);
				}
			}else{
				childList.add(epAttenExport);
				childMaps.put(time, childList);
			}
		}
	
		return iteratorMaps(maps);
	}
	
	/**
	 * 遍历数据，每天的有效打卡只能有2条，其他的都舍弃
	 * @param datas
	 * @return
	 */
	private static void sortAttendDatas(List<EpAttenExport> datas){
		if(datas == null || datas.isEmpty()) return;
		final Calendar calendar = Calendar.getInstance(); 
		Collections.sort(datas, new Comparator<EpAttenExport>() {

			@Override
			public int compare(EpAttenExport ep1, EpAttenExport ep2) {
				calendar.setTime(ep1.getTime());
				long ep1Mills = calendar.getTimeInMillis();
				calendar.setTime(ep2.getTime());
				long ep2Mills = calendar.getTimeInMillis();
				return (int)(ep1Mills - ep2Mills);
			}
		});
		
		datas.remove(1);
	}
	
	/**
	 * 迭代集合得到最终的结果
	 * @param maps
	 * @return
	 */
	private static List<EpAttenExport> iteratorMaps(HashMap<String,TreeMap<String,List<EpAttenExport>>>  maps){
		ArrayList<EpAttenExport> result = new ArrayList<EpAttenExport>();
		Set<Entry<String,TreeMap<String,List<EpAttenExport>>>> entrySet = maps.entrySet();
		Iterator<Entry<String, TreeMap<String, List<EpAttenExport>>>> iterator = entrySet.iterator();
		while(iterator.hasNext()){
			Entry<String, TreeMap<String, List<EpAttenExport>>> next = iterator.next();
			TreeMap<String,List<EpAttenExport>> value = next.getValue();
			Set<Entry<String,List<EpAttenExport>>> entrySet2 = value.entrySet();
			Iterator<Entry<String, List<EpAttenExport>>> iterator2 = entrySet2.iterator();
			while(iterator2.hasNext()){
				Entry<String, List<EpAttenExport>> next2 = iterator2.next();
				result.addAll(next2.getValue());
			}
		}
		return result;
	}

	public static void downLoadFile(HttpServletRequest request, HttpServletResponse response, String filePath) {
		File file = new File(filePath);
		if(!file.exists()) return;
		String filenames = file.getName();
		InputStream inputStream = null;
		OutputStream os = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			response.reset();
			// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filenames.replaceAll(" ", "").getBytes("utf-8"), "iso8859-1"));
			response.addHeader("Content-Length", "" + file.length());
			os = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			
			byte[] buffer = new byte[8192];
			int count = 0;
			while((count = inputStream.read(buffer)) > 0){
				os.write(buffer, 0, count);
			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(inputStream != null)inputStream.close();
				if(os != null)os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 将文件上传到服务器
	 * 
	 * @param request
	 * @param multipartFile
	 * @param filePath
	 *            文件保存在服务器的路径(相对的,目前只适用于存在tomcat对应的目录下)
	 * @return
	 */
	public static String saveFileToServer(HttpServletRequest request,MultipartFile multipartFile, String filePath) {
		String originFileName = multipartFile.getOriginalFilename();
		String DatePath = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String savePath = request.getSession().getServletContext().getRealPath(filePath);
		File saveDir = new File(savePath);
		if (!saveDir.exists() || !saveDir.isDirectory()) {
			saveDir.mkdir();
		}
		String fileName = savePath + File.separator+ DatePath.replace(":", "-") + originFileName;
		// String relativePath = filePath + File.separator + DatePath +File.separator + "考勤"+DatePath.replace(":", "-")+".xls";//相对路径
		try {
			multipartFile.transferTo(new File(fileName));
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 将文件上传到服务器(绝对路径 eg:(D:/test/))
	 */
	public static String saveFileToServerByAbsolutePath(HttpServletRequest request,MultipartFile multipartFile, String filePath) {
		String originFileName = multipartFile.getOriginalFilename();
		File saveDir = new File(filePath);
		if (!saveDir.exists() || !saveDir.isDirectory()) {
			saveDir.mkdirs();
		}
		String fileName = filePath + File.separator+ originFileName;
		try {
			multipartFile.transferTo(new File(fileName));
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

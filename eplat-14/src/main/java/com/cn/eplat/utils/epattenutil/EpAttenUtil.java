package com.cn.eplat.utils.epattenutil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.util.TextUtils;

import com.cn.eplat.model.EpAtten;
import com.cn.eplat.utils.DateUtil;
/**
 * 考勤工具类
 * @author zhangshun
 *
 */
public class EpAttenUtil {

	public static final String ON_DUTY_HH_MM = " 08:30:00";//上班时间
	public static final String OFF_DUTY_HH_MM = " 18:00:00";//下班时间
	public static final String DEFAULT_START_DATE = "2016-12-10 00:00:00";//所有异常打卡的默认开始时间
	
	/**
	 * 从数据源里找出所有的有效的打卡数据
	 * @param attenDatas
	 * @return TreeMap<String,List<EpAtten>>   key-yyyy-MM-dd   value-对应该天的打卡记录集合，最多2条（最早，最晚）
	 */
	public static TreeMap<String,List<EpAtten>> getValidAttendDatas(List<EpAtten> attenDatas){
		if(attenDatas == null || attenDatas.isEmpty()) return new TreeMap<String,List<EpAtten>>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		TreeMap<String,List<EpAtten>>  maps = new TreeMap<>();
		
		for (EpAtten epAtten : attenDatas) {
			String time = sdf.format(epAtten.getTime());
			List<EpAtten>  childList = new ArrayList<>();
			if(maps.containsKey(time)){
				childList = maps.get(time);
				childList.add(epAtten);
				if(childList.size() > 2){
					sortAttendDatas(childList);
				}
			}else{
				childList.add(epAtten);
				maps.put(time, childList);
			}
		}
	
		return maps;
	}
	
	/**
	 * 遍历数据，每天的有效打卡只能有2条，其他的都舍弃
	 * @param datas
	 * @return
	 */
	private static void sortAttendDatas(List<EpAtten> datas){
		if(datas == null || datas.isEmpty()) return;
		final Calendar calendar = Calendar.getInstance(); 
		Collections.sort(datas, new Comparator<EpAtten>() {

			@Override
			public int compare(EpAtten ep1, EpAtten ep2) {
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
	 * 判断数据源里对应的数据是不是正常打卡
	 * 
	 * @param date
	 * @param epas
	 * @return
	 */
	public static boolean isNormalAttend(String currentdate, List<EpAtten> epas) {
		if (currentdate == null || epas == null || epas.isEmpty()|| epas.size() < 2) {
			return false;
		}

		String shouldUpDutyString = currentdate + ON_DUTY_HH_MM;
		String shouldOffDutyString = currentdate + OFF_DUTY_HH_MM;

		boolean isUpDutyNormal = false;
		boolean isOffDutyNormal = false;
		for (EpAtten epAtten : epas) {
			String fullString = DateUtil.formatDate(2, epAtten.getTime());// yyyy-mm-dd hh-mm-ss
			if (!isUpDutyNormal&& fullString.compareToIgnoreCase(shouldUpDutyString) < 0) {// 上班卡正常
				isUpDutyNormal = true;
			}

			if (!isOffDutyNormal&& fullString.compareToIgnoreCase(shouldOffDutyString) > 0) {// 下班卡正常
				isOffDutyNormal = true;
			}
		}
		return isUpDutyNormal&&isOffDutyNormal;
	}
	
	/**
	 * 判断上班时间是否正常
	 * @param time
	 * @return true-正常，false-不正常
	 */
	public static boolean isOnTimeNormal(Date time){
		if(time == null) return false;
		String string = DateUtil.formatDate(2, time);
		if((DateUtil.formatDate(1, time)+ON_DUTY_HH_MM).compareToIgnoreCase(string) >= 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断下班时间是否正常
	 * @param time
	 * @return true-正常，false-不正常
	 */
	public static boolean isOffTimeNormal(Date time){
		if(time == null) return false;
		String string = DateUtil.formatDate(2, time);
		if((DateUtil.formatDate(1, time)+OFF_DUTY_HH_MM).compareToIgnoreCase(string) <= 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断当前date是否是休息日期
	 * @param date Date
	 * @return
	 */
	public static boolean isRestday(Date date,List<Date> restDates){
		if(date == null || restDates == null || restDates.isEmpty())		return false;
		String dateString = DateUtil.formatDate(1, date);
		if(TextUtils.isEmpty(dateString))		return false;
		
		for (Date restDate : restDates) {
			String restDateString = DateUtil.formatDate(1, restDate);
			if(dateString.equalsIgnoreCase(restDateString)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断当前date是否是休息日期
	 * @param date  (yyyy-MM-dd格式)
	 * @return
	 */
	public static boolean isRestday(String dateString,List<Date> restDates){
		if(TextUtils.isEmpty(dateString) || restDates == null || restDates.isEmpty())		return false;
		
		for (Date restDate : restDates) {
			String restDateString = DateUtil.formatDate(1, restDate);
			if(dateString.equalsIgnoreCase(restDateString)){
				return true;
			}
		}
		return false;
	}
	
	
	
}

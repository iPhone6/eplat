package com.cn.eplat.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IRestCalendarService {
	
	//批量插入休息日期
	public int insertRestCalendar(List<HashMap<String, Object>> datas);
	
	//查找开始和结束日期之间的所有休息日期
	public List<HashMap<String, Object>> getDatesBetweenStartAndEnd(Date startDate,Date endDate);
	
	//根据类型查找开始和结束日期之间的所有休息日期（模糊查询）
	public List<Date> getDatesBetweenStartAndEndByType(@Param("startdate") Date startDate,@Param("enddate") Date endDate,@Param("type") String type);
		
	//根据日期批量删除对应的记录
	public int deleteRecordsByDates(List<Date> dates);
}

package com.cn.eplat.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.EpAtten;
import com.cn.eplat.model.EpUser;

public interface IEpAttenService {
	// 添加一条打卡记录信息
	public int addEpAtten(EpAtten epa);
	// 根据用户id获取该用户当天最后一次打卡记录是第几次打卡
	public int getLastPunchCardCountByEpUid(EpAtten epa);
	// 根据用户id和给定的时间范围天数，获取用户在该时间范围内的全部打卡记录信息
	public List<EpAtten> getEpAttenByUidAndDayRange(int uid, int day_range);
	// 根据主键id获取打卡记录信息
	public EpAtten getEpAttenById(Long id);
	// 根据给定的日期范围（开始日期和结束日期）获取打卡记录信息 getEpAttenByStartDateAndEndDate
	public List<EpAtten> getEpAttenByStartDateAndEndDate(Date start_date, Date end_date);
	// 根据用户id和给定的日期范围（开始日期和结束日期）获取打卡记录信息
	public List<EpAtten> getEpAttenByEpUidAndStartDateAndEndDate(int ep_uid, Date start_date, Date end_date);
	// 根据用户id和给定的日期范围（开始日期和结束日期）获取全部有效的打卡记录信息
	public List<EpAtten> getValidEpAttenByEpUidAndStartDateAndEndDate(int ep_uid, Date start_date, Date end_date);

	//导出指定员工,指定日期范围内的考勤数据
	public String getAllEpAttenExportDatas(List<String> emails,Date startDate,Date endDate);
	
	// 批量添加打卡机打卡数据
	public int batchAddEpAttens(List<EpAtten> epas);
	
	// 根据给定日期列表和用户id列表查询出每个用户在这些日期中的最早一次和最晚一次有效/成功打卡的打卡时间（只查今天0点整之前的打卡数据）
	public List<HashMap<String, Object>> getFirstAndLastPunchTimeValidByDatesAndEpUidsBeforeToday(List<Date> dates, List<EpUser> epus);
	
	// 在所有打卡数据中，查询最早的一次和最晚的一次打卡数据的日期（只查今天0点之前的）
	public HashMap<String, Object> getFirstAndLastPunchTimeValid();
	
}

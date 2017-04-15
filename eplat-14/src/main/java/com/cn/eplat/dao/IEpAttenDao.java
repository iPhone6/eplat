package com.cn.eplat.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.EpAtten;
import com.cn.eplat.model.EpAttenExport;
import com.cn.eplat.model.EpUser;

public interface IEpAttenDao {
	// 添加一条打卡记录
	public int insertEpAtten(EpAtten epa);
	// 根据用户id查询该用户当天打卡记录中最后一次打卡记录是第几次打卡
	public Integer queryLastPunchCardCountToday(EpAtten epa);
	// 根据用户id和时间范围（给定天数）查询该用户在这个时间范围内的全部打卡记录信息
	public List<EpAtten> queryEpAttenByUidAndDayRange(@Param("uid") int uid, @Param("day_range") int day_range);
	// 根据打卡记录id查询打卡记录
	public EpAtten queryEpAttenById(Long id);
	
	//导出指定员工,指定日期范围内的考勤数据
	public List<EpAttenExport> queryAllEpAttenExportDatas(@Param("datas") List<String> emails,@Param("startDate") Date startDdate,@Param("endDate") Date endDate);
	
	// 根据给定的日期范围（开始日期和结束日期）查询打卡记录信息
	public List<EpAtten> queryEpAttenByStartDateAndEndDate(@Param("start_date") Date start_date, @Param("end_date") Date end_date);
	// 根据用户id和给定的日期范围（开始日期和结束日期）查询打卡记录信息
	public List<EpAtten> queryEpAttenByEpUidAndStartDateAndEndDate(@Param("ep_uid") int ep_uid, @Param("start_date") Date start_date, @Param("end_date") Date end_date);
	// 根据用户id和给定的日期范围（开始日期和结束日期）查询全部有效的打卡记录信息（注：“有效的”即表示is_valid字段的值为true，且打卡时间time字段的值不为NULL）
	public List<EpAtten> queryValidEpAttenByEpUidAndStartDateAndEndDate(@Param("ep_uid") int ep_uid, @Param("start_date") Date start_date, @Param("end_date") Date end_date);
	
	// 批量插入打卡机打卡数据
	public int batchInsertEpAttens(List<EpAtten> epas);
	
	// 根据给定日期列表和用户id列表查询出每个用户在这些日期中的最早一次和最晚一次有效/成功打卡的打卡时间（只查今天0点整之前的打卡数据）
	public List<HashMap<String, Object>> queryFirstAndLastPunchTimeValidByDatesAndEpUidsBeforeToday(@Param("dates") List<Date> dates, @Param("epus") List<EpUser> epus);
	
	// 在所有打卡数据中，查询最早的一次和最晚的一次打卡数据的日期（只查今天0点之前的）
	public HashMap<String, Object> queryFirstAndLastPunchTimeValid();
	
	// 查出所有GPS打卡数据
	public List<EpAtten> queryAllGPSEpAttens();
	
	// 查出所有距离所有中心点的最近距离字段(gps_distance)的值为NULL的GPS打卡数据
	public List<EpAtten> queryAllGPSEpAttensWithNullGPSDistance();
	
	// 根据id修改打卡数据信息
	public int updateEpAttenById(EpAtten epa);
	
}

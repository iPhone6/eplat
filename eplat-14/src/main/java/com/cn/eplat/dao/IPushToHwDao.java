package com.cn.eplat.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.PushToHw;

public interface IPushToHwDao {
	
	// 批量插入准备推送华为考勤系统的数据
	public Integer batchInsertPushToHws(List<PushToHw> pthws);
	
	//根据日期获取当天的所有要导入到华为的考勤数据
	public List<PushToHw> getPushToHwsByDate(@Param("startdate") Date startdate,@Param("enddate") Date enddate);
	
	//根据ids查找数据
	public List<PushToHw> getPushToHwsByIds(List<Long> ids);
	
	
	// 按名字查找某个人的所有推送数据
	List<PushToHw> findPushToHwsByName(String name);
	
	// 按序号筛选推送数据（从开始到结束序号）
	List<PushToHw> getPushToHwsByIdSeq(@Param("idSeqStart") Long idSeqStart, @Param("idSeqEnd") Long idSeqEnd);
	
}

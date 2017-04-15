package com.cn.eplat.dao;

import java.util.Date;
import java.util.List;

import com.cn.eplat.model.PushFilterLog;

public interface IPushFilterLogDao {
	// 查询出已做过筛选的考勤日期
	public List<Date> queryFilteredDates();
	
	// 批量插入筛选日志数据
	public int batchInsertPushFilterLogs(List<PushFilterLog> pfls);
	
	// 查询最早的一次筛选时间
	public Date queryEarliestPushFilterLogTime();
	
}

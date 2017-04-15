package com.cn.eplat.service;

import java.util.Date;
import java.util.List;

import com.cn.eplat.model.PushFilterLog;

public interface IPushFilterLogService {
	// 获取已做过筛选的考勤日期
	public List<Date> getFilteredDates();
	
	// 批量添加筛选日志数据
	public int batchAddPushFilterLogs(List<PushFilterLog> pfls);
	
	// 获取最早的一次筛选时间
	public Date getEarliestPushFilterLogTime();
	
}

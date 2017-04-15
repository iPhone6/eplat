package com.cn.eplat.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.PushLog;

public interface IPushLogDao {
	
	//批量插入推送日志
	public int insert2PushLog(List<PushLog> logs);
	
	//获取开始，结束日期之间所有的推送日志
	public List<HashMap<String,Object>> getAllPushLogsBetweenStartAndEnd(@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	//查询出昨天之前所有的推送失败了得日志的pth_id
	public List<Long> getAllFailPthIds();
	
	//获取日志表（push_log）里最晚的time
	public Date getLatestPushLogTime();
	
	//批量删除日志
	public int deletePushLogsByPthids(List<Long> pthIds);
}

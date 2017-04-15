package com.cn.eplat.service;

import java.util.Date;
import java.util.List;

import com.cn.eplat.model.PushToHw;

public interface IPushToHwService {
	
	// 批量插入准备推送华为考勤系统的数据
	public Integer batchAddPushToHws(List<PushToHw> pthws);
	
	//根据开始结束日期获取所有要导入到华为的考勤数据
	public List<PushToHw> getPushToHwsByDate(Date startdate,Date enddate);
}

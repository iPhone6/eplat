package com.cn.eplat.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IAttenExceptionService {
	//插入最后查询异常打卡信息的日期
	public int insertAttenExceptions(List<HashMap<String,Object>> exceptionAttens);
	
	//根据ep_uid查找最后一条异常打卡的时间
	public Date getLastTimeByUid(int uid);
	
	//根据ep_uid获取异常打卡表中的所有的未处理异常数据
	public List<HashMap<String,Date>> getAllAttenExceptionsByUid(int uid);
	
	//根据ep_uid获取异常打卡表中的所有的已处理异常数据
	public List<HashMap<String,Date>> getAllHandledAttenExceptionsByUid(int uid);
	
	//根据ep_uid和date删除对应的数据
	public int deleteAttenExceptionByUidAndDate(@Param("uid") int uid,@Param("date") Date date);
}

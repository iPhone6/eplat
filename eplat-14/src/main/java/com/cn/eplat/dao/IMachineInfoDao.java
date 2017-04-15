package com.cn.eplat.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IMachineInfoDao {
	
	//批量插入打卡机信息
	public int insertMachineInfos(List<HashMap<String, Object>> datas);
	
	//获取所有打卡机信息
	public List<HashMap<String,Object>> getAllMachineInfos();
	
	//根据打卡机系列号获取其所在地区名
	public String getNameBySN(String sn);
	
	//根据打卡机系列号更新对应的打卡机信息
	public int updateMachineInfoBySN(@Param("status") int status,@Param("sn") String name);
}

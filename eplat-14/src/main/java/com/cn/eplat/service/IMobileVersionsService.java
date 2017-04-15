package com.cn.eplat.service;

import java.util.HashMap;

public interface IMobileVersionsService {
	// 插入移动版本信息
	public int insertMobileVersion(String version,String platform,String point, String name);

	// 获取数据库中最新的版本号
	public HashMap<String, String> getLatestVersion();
	
	//根据版本号获取id
	public Integer getIdByVersion(String version);
	
	//更新对应的数据
	public int updateMobileVersionByVersion(String version,String platform,String point,String name);
}

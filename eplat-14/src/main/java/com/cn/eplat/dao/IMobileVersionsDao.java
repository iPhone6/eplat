package com.cn.eplat.dao;

import java.util.HashMap;

import org.apache.ibatis.annotations.Param;

public interface IMobileVersionsDao {
	
	//插入移动版本信息
	public int insertMobileVersion(@Param("version") String version,@Param("platform") String platform,@Param("point") String point,@Param("name") String name);
	
	//获取数据库中最新的版本号
	public HashMap<String,String> getLatestVersion();
	
	//根据版本号获取id
	public Integer getIdByVersion(String version);
	
	//更新对应的数据
	public int updateMobileVersionByVersion(@Param("version") String version,@Param("platform") String platform,@Param("point") String point,@Param("name") String name);
}

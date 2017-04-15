package com.cn.eplat.dao;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.EpData;

public interface IEpDataDao {
	// 根据appid和角色id查询假数据
	public EpData queryEpDataByAppidAndRoleId(@Param("appid") String appid, @Param("role_id") int role_id);

}

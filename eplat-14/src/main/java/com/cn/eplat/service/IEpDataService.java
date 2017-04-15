package com.cn.eplat.service;

import com.cn.eplat.model.EpData;

public interface IEpDataService {
	// 根据appid和角色id获取假数据
	public EpData getEpDataByAppidAndRoleId(String appid, Integer role_id);

}

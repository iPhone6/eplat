package com.cn.eplat.service.impl;

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IMobileVersionsDao;
import com.cn.eplat.service.IMobileVersionsService;

@Service("mobileVersionsService")
@Transactional
public class MobileVersionsServiceImpl implements IMobileVersionsService {
	
	@Resource
	private IMobileVersionsDao mobileVersionsDdao;

	@Override
	public int insertMobileVersion(String version, String platform, String point,String name) {
		return mobileVersionsDdao.insertMobileVersion(version, platform, point,name);
	}

	@Override
	public HashMap<String, String> getLatestVersion() {
		return mobileVersionsDdao.getLatestVersion();
	}

	@Override
	public Integer getIdByVersion(String version) {
		return mobileVersionsDdao.getIdByVersion(version);
	}

	@Override
	public int updateMobileVersionByVersion(String version,String platform,String point,String name) {
		return mobileVersionsDdao.updateMobileVersionByVersion(version,platform,point,name);
	}

}

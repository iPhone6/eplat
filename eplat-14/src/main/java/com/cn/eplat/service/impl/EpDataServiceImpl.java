package com.cn.eplat.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEpDataDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpData;
import com.cn.eplat.service.IEpDataService;

@Service("epDataService")
@Transactional
public class EpDataServiceImpl implements IEpDataService {
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpDataDao epDataDao;

	@Override
	public EpData getEpDataByAppidAndRoleId(String appid, Integer role_id) {
		return epDataDao.queryEpDataByAppidAndRoleId(appid, role_id);
	}

}

package com.cn.eplat.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IMachCheckInOutDao;
import com.cn.eplat.dao.IMachUserInfoDao;
import com.cn.eplat.model.MachUserInfo;
import com.cn.eplat.service.IMachUserInfoService;

@Service("machUserInfoService")
@Transactional
public class MachUserInfoServiceImpl implements IMachUserInfoService {
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	@Resource
	private IMachUserInfoDao machUserInfoDao;
	
	@Override
	public List<MachUserInfo> queryAllMachUserInfos() {
		return machUserInfoDao.queryAllMachUserInfos();
	}

	@Override
	public int queryMachUserInfoNumber() {
		return machUserInfoDao.queryMachUserInfoNumber();
	}

}

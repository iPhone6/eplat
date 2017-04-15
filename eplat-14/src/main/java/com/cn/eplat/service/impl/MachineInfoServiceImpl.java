package com.cn.eplat.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IMachineInfoDao;
import com.cn.eplat.service.IMachineInfoService;

@Service("machineInfoService")
@Transactional
public class MachineInfoServiceImpl implements IMachineInfoService {
	
	@Resource
	private IMachineInfoDao machineInfoDao;

	@Override
	public int insertMachineInfos(List<HashMap<String, Object>> datas) {
		return machineInfoDao.insertMachineInfos(datas);
	}

	@Override
	public List<HashMap<String, Object>> getAllMachineInfos() {
		return machineInfoDao.getAllMachineInfos();
	}

	@Override
	public String getNameBySN(String sn) {
		return machineInfoDao.getNameBySN(sn);
	}

	@Override
	public int updateMachineInfoBySN(int status, String name) {
		return machineInfoDao.updateMachineInfoBySN(status, name);
	}
}

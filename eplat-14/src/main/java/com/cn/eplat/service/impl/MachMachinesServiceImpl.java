package com.cn.eplat.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IMachCheckInOutDao;
import com.cn.eplat.dao.IMachMachinesDao;
import com.cn.eplat.model.MachMachines;
import com.cn.eplat.service.IMachMachinesService;

@Service("machMachinesService")
@Transactional
public class MachMachinesServiceImpl implements IMachMachinesService {
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	
	@Resource
	private IMachMachinesDao machMachinesDao;
	
	@Override
	public List<MachMachines> queryAllMachMachines() {
		return machMachinesDao.queryAllMachMachines();
	}

	@Override
	public List<String> queryAllMachineSns() {
		return machMachinesDao.queryAllMachineSns();
	}

}

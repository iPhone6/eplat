package com.cn.eplat.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEpDeviceDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpDevice;
import com.cn.eplat.service.IEpDeviceService;

@Service("epDeviceService")
@Transactional
public class EpDeviceServiceImpl implements IEpDeviceService {
	
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpDeviceDao epDeviceDao;

	@Override
	public int getDeviceBoundCount(EpDevice epd) {
		return epDeviceDao.queryDeviceBoundCount(epd);
	}

	@Override
	public EpDevice getEpDeviceById(int id) {
		return epDeviceDao.queryEpDeviceById(id);
	}

	@Override
	public int addEpDevice(EpDevice epd) {
		return epDeviceDao.insertEpDevice(epd);
	}

	@Override
	public int modifyEpDeviceById(EpDevice epd) {
		return epDeviceDao.updateEpDeviceById(epd);
	}
	
	@Override
	public int modifyEpDeviceByIdIncludingNull(EpDevice epd) {
		return epDeviceDao.updateEpDeviceByIdIncludingNull(epd);
	}

	@Override
	public List<EpDevice> getEpDeviceByCriterion(EpDevice epd) {
		return epDeviceDao.queryEpDeviceByCriterion(epd);
	}

}

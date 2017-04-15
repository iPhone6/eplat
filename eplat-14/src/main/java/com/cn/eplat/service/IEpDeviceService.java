package com.cn.eplat.service;

import java.util.List;

import com.cn.eplat.model.EpDevice;

public interface IEpDeviceService {
	// 根据用户id获取该用户已绑定设备的个数
	public int getDeviceBoundCount(EpDevice epd);
	// 根据设备id获取设备信息
	public EpDevice getEpDeviceById(int id);
	// 添加一条设备记录信息
	public int addEpDevice(EpDevice epd);
	// 根据设备id修改一条设备记录信息
	public int modifyEpDeviceById(EpDevice epd);
	// 根据设备id修改一条设备记录信息（包括值为Null的字段）
	public int modifyEpDeviceByIdIncludingNull(EpDevice epd);
	// 根据除id以外的条件获取设备信息
	public List<EpDevice> getEpDeviceByCriterion(EpDevice epd);
}

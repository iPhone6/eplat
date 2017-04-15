package com.cn.eplat.dao;

import java.util.List;

import com.cn.eplat.model.EpDevice;

public interface IEpDeviceDao {
	// 根据用户id和设备id查询设备绑定状态是否已绑定
	public Boolean queryIfDeviceBound(EpDevice epd);
	// 根据用户id查询该用户已绑定设备的个数
	public int queryDeviceBoundCount(EpDevice epd);
	// 根据设备id查询设备信息
	public EpDevice queryEpDeviceById(int id);
	// 新增一条设备记录信息
	public int insertEpDevice(EpDevice epd);
	// 根据设备id修改一条设备记录信息
	public int updateEpDeviceById(EpDevice epd);
	// 根据设备id修改一条设备记录信息（包含值为null的字段）
	public int updateEpDeviceByIdIncludingNull(EpDevice epd);
	// 根据除id以外的条件查询设备信息
	public List<EpDevice> queryEpDeviceByCriterion(EpDevice epd);
}

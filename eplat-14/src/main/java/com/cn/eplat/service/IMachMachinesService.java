package com.cn.eplat.service;

import java.util.List;

import com.cn.eplat.model.MachMachines;

public interface IMachMachinesService {
	
	
	// // 以下是在打卡机的Access数据库中的操作	// //
	// 查出所有的打卡机信息
	public List<MachMachines> queryAllMachMachines();
	
	// 查询所有打卡机的序列号
	public List<String> queryAllMachineSns();
	
	
}

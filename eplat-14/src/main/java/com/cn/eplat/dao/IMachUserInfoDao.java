package com.cn.eplat.dao;

import java.util.List;

import com.cn.eplat.model.MachUserInfo;

public interface IMachUserInfoDao {
	
	// 以下方法操作的是Access数据库中的Userinfo表对象 //
	// 查出所有打卡机用户信息
	public List<MachUserInfo> queryAllMachUserInfos();
	// 查出打卡机用户信息条数
	public int queryMachUserInfoNumber();
	
	
}

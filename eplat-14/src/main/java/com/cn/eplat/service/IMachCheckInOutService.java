package com.cn.eplat.service;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.MachCheckInOut;

public interface IMachCheckInOutService {
	public List<MachCheckInOut> getMachCheckInOutById(int id);
	
	public List<MachCheckInOut> getMachCheckInOutByCheckTime(Date ct);
	// 根据打卡时间往后获取前100条打卡数据
	public List<MachCheckInOut> getMachCheckInOutByCheckTimeTop100(Date ct);
	// 关联打卡数据表(CHECKINOUT)和用户信息表(USERINFO)，根据打卡时间往后获取前100条打卡数据
	public List<MachCheckInOut> getMachCheckInOutAndUserInfoByCheckTimeTop100(Date ct);
	// 关联打卡数据表(CHECKINOUT)和用户信息表(USERINFO)，根据打卡时间往后获取前100条打卡数据，并排除掉部分userid的打卡数据
	public List<MachCheckInOut> getMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100(Date ct, List<Integer> userids);
	// 根据打卡机序列号，关联打卡数据表(CHECKINOUT)和用户信息表(USERINFO)，根据打卡时间往后查询前100条打卡数据，并排除掉部分userid的打卡数据
	public List<MachCheckInOut> queryMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100ByMachSn(Date ct, List<Integer> userids, String sn);
	// 根据打卡机序列号和打卡时间往后查询前100条打卡数据，并排除掉部分userid的打卡数据（只查打卡数据表(CHECKINOUT)，查询结果没有用户工号信息）
	public List<MachCheckInOut> queryMachCheckInOutByCheckTimeExcludeSomeUseridsTop100ByMachSn(Date ct, List<Integer> userids, String sn);
	// 关联打卡数据表(CHECKINOUT)和用户信息表(USERINFO)，获取前100条打卡数据
	public List<MachCheckInOut> getMachCheckInOutAndUserInfoTop100();
	
	// 根据打卡机序列号，关联打卡数据表(CHECKINOUT)和用户信息表(USERINFO)，查询前100条打卡数据
	public List<MachCheckInOut> queryMachCheckInOutAndUserInfoTop100ByMachSn(String sn);
	
	public List<MachCheckInOut> getAllMachCheckInOut();
	
	// 批量添加打卡机打卡数据
	public int batchAddMachCheckInOut(List<MachCheckInOut> mcios);
	
	// 批量添加打卡机打卡数据（打卡时间明显有误的）
	public int batchAddMachCheckInOutWithErrorCheckTime(List<MachCheckInOut> mcios);
	
	// 根据条件获取打卡机打卡数据
	public List<MachCheckInOut> getMachCheckInOutByCriteria(MachCheckInOut mcio);
	
	// 根据普通条件和特殊条件获取打卡机打卡数据
	public List<MachCheckInOut> getMachCheckInOutByNormalAndSpecialCriteria(MachCheckInOut mcio);
	
	// 根据id批量修改打卡机打卡数据
	public int batchModifyMachCheckInOutById(List<MachCheckInOut> macios);
	
	// 获取id最大的打卡机打卡数据（也即是最后一条打卡机打卡数据）
	public MachCheckInOut getMachCheckInOutWithMaxId();
	
	// 根据打卡机序列号查询打卡时间最晚的打卡机打卡数据
	public List<MachCheckInOut> queryMachCheckInOutWithMaxCheckTimeByMachSn(String sn);
	
	// 获取打卡时间最晚的打卡机打卡数据
	public List<MachCheckInOut> getMachCheckInOutWithMaxCheckTime();
	
	// 从Access打卡数据拷贝表中找出在本地MySQL打卡数据推送日志表中没有的打卡数据
	public List<MachCheckInOut> queryMissedMachCheckInOutsByCompareAccessAndMySQLDatas();
	
	// 把全部Access的打卡数据批量插入本地MySQL的打卡数据拷贝表中
	public int batchInsertAllAccessCheckinoutsToMySQLMachCheckInOutCopy(List<MachCheckInOut> all_datas);
	
	// 删除全部打卡机Access打卡数据在MySQL数据库中的拷贝（即清空mach_chkio_copy表中的数据）
	public int deleteAllAccessCheckInOutsCopyInMySQL();
	
	
	
}

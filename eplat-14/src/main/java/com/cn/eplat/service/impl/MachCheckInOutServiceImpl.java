package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEpAttenDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.dao.IMachCheckInOutDao;
import com.cn.eplat.model.MachCheckInOut;
import com.cn.eplat.service.IMachCheckInOutService;

@Service("machCheckInOutService")
@Transactional
public class MachCheckInOutServiceImpl implements IMachCheckInOutService {
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpAttenDao epAttenDao;
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	
	@Override
	public List<MachCheckInOut> getMachCheckInOutById(int id) {
		return machCheckInOutDao.queryMachCheckInOutById(id);
	}
	
	@Override
	public List<MachCheckInOut> getMachCheckInOutByCheckTime(Date ct) {
		return machCheckInOutDao.queryMachCheckInOutByCheckTime(ct);
	}
	
	@Override
	public List<MachCheckInOut> getMachCheckInOutByCheckTimeTop100(Date ct) {
		return machCheckInOutDao.queryMachCheckInOutByCheckTimeTop100(ct);
	}

	@Override
	public List<MachCheckInOut> getMachCheckInOutAndUserInfoByCheckTimeTop100(Date ct) {
		return machCheckInOutDao.queryMachCheckInOutAndUserInfoByCheckTimeTop100(ct);
	}
	
	@Override
	public List<MachCheckInOut> getMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100(Date ct, List<Integer> userids) {
		return machCheckInOutDao.queryMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100(ct, userids);
	}
	
	@Override
	public List<MachCheckInOut> queryMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100ByMachSn(Date ct, List<Integer> userids, String sn) {
		return machCheckInOutDao.queryMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100ByMachSn(ct, userids, sn);
	}
	
	@Override
	public List<MachCheckInOut> queryMachCheckInOutByCheckTimeExcludeSomeUseridsTop100ByMachSn(Date ct, List<Integer> userids, String sn) {
		return machCheckInOutDao.queryMachCheckInOutByCheckTimeExcludeSomeUseridsTop100ByMachSn(ct, userids, sn);
	}
	
	@Override
	public List<MachCheckInOut> getMachCheckInOutAndUserInfoTop100() {
		return machCheckInOutDao.queryMachCheckInOutAndUserInfoTop100();
	}
	

	@Override
	public List<MachCheckInOut> queryMachCheckInOutAndUserInfoTop100ByMachSn(String sn) {
		return machCheckInOutDao.queryMachCheckInOutAndUserInfoTop100ByMachSn(sn);
	}
	
	@Override
	public List<MachCheckInOut> getAllMachCheckInOut() {
		return machCheckInOutDao.queryAllMachCheckInOut();
	}
	
	@Override
	public int batchAddMachCheckInOut(List<MachCheckInOut> mcios) {
		return machCheckInOutDao.batchInsertMachCheckInOut(mcios);
	}
	
	@Override
	public int batchAddMachCheckInOutWithErrorCheckTime(List<MachCheckInOut> mcios) {
		return machCheckInOutDao.batchInsertMachCheckInOutWithErrorCheckTime(mcios);
	}
	
	@Override
	public List<MachCheckInOut> getMachCheckInOutByCriteria(MachCheckInOut mcio) {
		return machCheckInOutDao.queryMachCheckInOutByCriteria(mcio);
	}
	
	@Override
	public List<MachCheckInOut> getMachCheckInOutByNormalAndSpecialCriteria(MachCheckInOut mcio) {
		return machCheckInOutDao.queryMachCheckInOutByNormalAndSpecialCriteria(mcio);
	}
	
	@Override
	public int batchModifyMachCheckInOutById(List<MachCheckInOut> macios) {
		return machCheckInOutDao.batchUpdateMachCheckInOutById(macios);
	}

	@Override
	public MachCheckInOut getMachCheckInOutWithMaxId() {
		return machCheckInOutDao.queryMachCheckInOutWithMaxId();
	}
	

	@Override
	public List<MachCheckInOut> queryMachCheckInOutWithMaxCheckTimeByMachSn(String sn) {
		return machCheckInOutDao.queryMachCheckInOutWithMaxCheckTimeByMachSn(sn);
	}
	
	@Override
	public List<MachCheckInOut> getMachCheckInOutWithMaxCheckTime() {
		return machCheckInOutDao.queryMachCheckInOutWithMaxCheckTime();
	}

	@Override
	public List<MachCheckInOut> queryMissedMachCheckInOutsByCompareAccessAndMySQLDatas() {
		return machCheckInOutDao.queryMissedMachCheckInOutsByCompareAccessAndMySQLDatas();
	}

	@Override
	public int batchInsertAllAccessCheckinoutsToMySQLMachCheckInOutCopy(List<MachCheckInOut> all_datas) {
		return machCheckInOutDao.batchInsertAllAccessCheckinoutsToMySQLMachCheckInOutCopy(all_datas);
	}
	
	@Override
	public int deleteAllAccessCheckInOutsCopyInMySQL() {
		return machCheckInOutDao.deleteAllAccessCheckInOutsCopyInMySQL();
	}
	
}

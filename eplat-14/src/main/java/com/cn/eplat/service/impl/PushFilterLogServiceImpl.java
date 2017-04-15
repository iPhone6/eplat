package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEpAttenDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.dao.IPushFilterLogDao;
import com.cn.eplat.model.PushFilterLog;
import com.cn.eplat.service.IPushFilterLogService;

@Service("pushFilterLogService")
@Transactional
public class PushFilterLogServiceImpl implements IPushFilterLogService {
	
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpAttenDao epAttenDao;
	@Resource
	private IPushFilterLogDao pushFilterLogDao;

	@Override
	public List<Date> getFilteredDates() {
		return pushFilterLogDao.queryFilteredDates();
	}

	@Override
	public int batchAddPushFilterLogs(List<PushFilterLog> pfls) {
		return pushFilterLogDao.batchInsertPushFilterLogs(pfls);
	}

	@Override
	public Date getEarliestPushFilterLogTime() {
		return pushFilterLogDao.queryEarliestPushFilterLogTime();
	}
	
	
}

package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IPushLogDao;
import com.cn.eplat.model.PushLog;
import com.cn.eplat.service.IPushLogService;

@Service("pushLogService")
@Transactional
public class PushLogServiceImpl implements IPushLogService {
	
	@Resource
	private IPushLogDao pushLogDao;

	@Override
	public int insert2PushLog(List<PushLog> logs) {
		return pushLogDao.insert2PushLog(logs);
	}

	@Override
	public List<HashMap<String,Object>> getAllPushLogsBetweenStartAndEnd(Date startDate, Date endDate) {
		return pushLogDao.getAllPushLogsBetweenStartAndEnd(startDate, endDate);
	}

	@Override
	public List<Long> getAllFailPthIds() {
		return pushLogDao.getAllFailPthIds();
	}
}

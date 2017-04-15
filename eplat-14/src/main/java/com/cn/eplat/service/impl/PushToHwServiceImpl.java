package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEpAttenDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.dao.IPushFilterLogDao;
import com.cn.eplat.dao.IPushToHwDao;
import com.cn.eplat.model.PushToHw;
import com.cn.eplat.service.IPushToHwService;

@Service("pushToHwService")
@Transactional
public class PushToHwServiceImpl implements IPushToHwService {
	
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpAttenDao epAttenDao;
	@Resource
	private IPushFilterLogDao pushFilterLogDao;
	@Resource
	private IPushToHwDao pushToHwDao;
	
	@Override
	public Integer batchAddPushToHws(List<PushToHw> pthws) {
		return pushToHwDao.batchInsertPushToHws(pthws);
	}

	@Override
	public List<PushToHw> getPushToHwsByDate(Date startdate,Date enddate) {
		return pushToHwDao.getPushToHwsByDate(startdate,enddate);
	}
	
}

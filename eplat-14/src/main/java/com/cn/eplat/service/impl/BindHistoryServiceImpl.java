package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IBindHistoryDao;
import com.cn.eplat.dao.IEpDeviceDao;
import com.cn.eplat.model.BindHistory;
import com.cn.eplat.service.IBindHistoryService;

@Service("bindHistoryService")
@Transactional
public class BindHistoryServiceImpl implements IBindHistoryService {
	
	@Resource
	private IEpDeviceDao epDeviceDao;
	@Resource
	private IBindHistoryDao bindHistoryDao;
	
	@Override
	public int addBindHistory(BindHistory bh) {
		return bindHistoryDao.insertBindHistory(bh);
	}

	@Override
	public BindHistory getBindHistoryById(int id) {
		return bindHistoryDao.queryBindHistoryById(id);
	}

	@Override
	public int modifyBindHistoryById(BindHistory bh) {
		return bindHistoryDao.updateBindHistoryById(bh);
	}

	@Override
	public List<BindHistory> getBindHistoryByCriterion(BindHistory bh) {
		return bindHistoryDao.queryBindHistoryByCriterion(bh);
	}
	
	@Override
	public List<BindHistory> getBindHistoryByCriterionOrderByTime(BindHistory bh) {
		return bindHistoryDao.queryBindHistoryByCriterionOrderByTime(bh);
	}
	
	@Override
	public List<BindHistory> getBindHistoryByCriterionValidOrderByTime(BindHistory bh, Date start, int range) {
		return bindHistoryDao.queryBindHistoryByCriterionValidOrderByTimeV2(bh, start, range);
	}

	@Override
	public BindHistory getLastBindHistoryValidByEpUid(int ep_uid, Date start, int range) {
		return bindHistoryDao.queryLastBindHistoryValidByEpUid(ep_uid, start, range);
	}
	
}

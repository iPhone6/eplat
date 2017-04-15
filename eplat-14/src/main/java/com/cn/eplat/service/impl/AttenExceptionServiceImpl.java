package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IAttenExceptionDao;
import com.cn.eplat.service.IAttenExceptionService;

@Service("attenExceptionService")
@Transactional
public class AttenExceptionServiceImpl implements IAttenExceptionService {

	@Resource
	private IAttenExceptionDao attenExceptionDao;

	@Override
	public int insertAttenExceptions(List<HashMap<String,Object>> exceptionAttens) {
		return attenExceptionDao.insertAttenExceptions(exceptionAttens);
	}

	@Override
	public Date getLastTimeByUid(int uid) {
		return attenExceptionDao.getLastTimeByUid(uid);
	}

	@Override
	public List<HashMap<String, Date>> getAllAttenExceptionsByUid(int uid) {
		return attenExceptionDao.getAllAttenExceptionsByUid(uid);
	}

	@Override
	public List<HashMap<String, Date>> getAllHandledAttenExceptionsByUid(int uid) {
		return attenExceptionDao.getAllHandledAttenExceptionsByUid(uid);
	}

	@Override
	public int deleteAttenExceptionByUidAndDate(int uid, Date date) {
		return attenExceptionDao.deleteAttenExceptionByUidAndDate(uid, date);
	}
}

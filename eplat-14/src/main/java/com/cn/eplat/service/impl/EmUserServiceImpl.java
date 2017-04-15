package com.cn.eplat.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEmUserDao;
import com.cn.eplat.model.EmUser;
import com.cn.eplat.service.IEmUserService;

@Service("emUserService")
@Transactional
public class EmUserServiceImpl implements IEmUserService {
	@Resource
	private IEmUserDao emUserDao;

	@Override
	public EmUser getEmUserById(int id) {
		return emUserDao.queryEmUserById(id);
	}

	@Override
	public int addEmUser(EmUser emu) {
		return emUserDao.insertEmUser(emu);
	}

	@Override
	public int modifyEmUserById(EmUser emu) {
		return emUserDao.updateEmUserById(emu);
	}

	@Override
	public List<Map<String, Object>> batchGetUserInfoByEmUsernames(List<String> em_unames) {
		return emUserDao.batchQueryUserInfoByEmUsernames(em_unames);
	}

}

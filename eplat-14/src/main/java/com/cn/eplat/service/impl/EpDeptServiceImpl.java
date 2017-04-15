package com.cn.eplat.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEpDeptDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpDept;
import com.cn.eplat.service.IEpDeptService;

@Service("epDeptService")
@Transactional
public class EpDeptServiceImpl implements IEpDeptService {
	@Resource
	private IEpDeptDao epDeptDao;

	@Override
	public int addEpDept(EpDept epd) {
		return epDeptDao.addEpDept(epd);
	}

	@Override
	public List<EpDept> findEpDeptByCriteria(EpDept ed) {
		return epDeptDao.queryEpDeptByCriteria(ed);
	}
	
}

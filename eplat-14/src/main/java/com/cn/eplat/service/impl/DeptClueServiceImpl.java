package com.cn.eplat.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IDeptClueDao;
import com.cn.eplat.dao.IEpDeptDao;
import com.cn.eplat.model.DeptClue;
import com.cn.eplat.service.IDeptClueService;

@Service("deptClueService")
@Transactional
public class DeptClueServiceImpl implements IDeptClueService {
	@Resource
	private IDeptClueDao deptClueDao;
	
	@Override
	public DeptClue findDeptClueByDeptClueStr(String dept_clue_str) {
		return deptClueDao.queryDeptClueByDeptClueStr(dept_clue_str);
	}

	@Override
	public int addDeptClue(DeptClue dc) {
		return deptClueDao.addDeptClue(dc);
	}

}

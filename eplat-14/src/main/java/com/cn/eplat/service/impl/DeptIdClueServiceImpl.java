package com.cn.eplat.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IDeptClueDao;
import com.cn.eplat.dao.IDeptIdClueDao;
import com.cn.eplat.model.DeptIdClue;
import com.cn.eplat.service.IDeptIdClueService;

@Service("deptIdClueService")
@Transactional
public class DeptIdClueServiceImpl implements IDeptIdClueService {
	
	@Resource
	private IDeptIdClueDao deptIdClueDao;

	@Override
	public int addOneDeptIdClue(DeptIdClue dic) {
		return deptIdClueDao.insertOneDeptIdClue(dic);
	}

	@Override
	public List<DeptIdClue> getAllDeptIdClueByDeptIdIncluded(int dept_id) {
		return deptIdClueDao.queryAllDeptIdClueByDeptIdIncluded(dept_id);
	}

	@Override
	public int removeDeptIdClueByRootDid(int root_did) {
		return deptIdClueDao.deleteDeptIdClueByRootDid(root_did);
	}
	
}

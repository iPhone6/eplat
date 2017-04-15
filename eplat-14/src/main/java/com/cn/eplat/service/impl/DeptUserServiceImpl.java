package com.cn.eplat.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IDeptClueDao;
import com.cn.eplat.dao.IDeptUserDao;
import com.cn.eplat.model.DeptUser;
import com.cn.eplat.service.IDeptUserService;

@Service("deptUserService")
@Transactional
public class DeptUserServiceImpl implements IDeptUserService {
	
	@Resource
	private IDeptUserDao deptUserDao;
	
	@Override
	public DeptUser getDeptUserByDeptIdAndUid(DeptUser du) {
		return deptUserDao.queryDeptUserByDeptIdAndUid(du);
	}

	@Override
	public int addDeptUserInfo(DeptUser du) {
		return deptUserDao.addDeptUserInfo(du);
	}

}

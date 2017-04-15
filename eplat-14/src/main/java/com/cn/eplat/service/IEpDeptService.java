package com.cn.eplat.service;

import java.util.List;

import com.cn.eplat.model.EpDept;

public interface IEpDeptService {
	// 添加一条部门信息
	public int addEpDept(EpDept epd);
	// 根据条件查找匹配的部门
	public List<EpDept> findEpDeptByCriteria(EpDept ed);
}

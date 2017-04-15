package com.cn.eplat.dao;

import java.util.List;

import com.cn.eplat.model.EpDept;

public interface IEpDeptDao {
	// 添加一条部门信息
	public int addEpDept(EpDept epd);
	// 根据给定条件查询部门信息
	public List<EpDept> queryEpDeptByCriteria(EpDept ed);
}

package com.cn.eplat.service;

import java.util.List;

import com.cn.eplat.model.DeptIdClue;

public interface IDeptIdClueService {
	// 新增一条部门id线索信息串
	public int addOneDeptIdClue(DeptIdClue dic);
	// 根据部门id获取包含该部门id的所有部门id线索字符串
	public List<DeptIdClue> getAllDeptIdClueByDeptIdIncluded(int dept_id);
	// 移除与某一根部门id相关的所有部门id线索信息串
	public int removeDeptIdClueByRootDid(int root_did);
}

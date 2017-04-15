package com.cn.eplat.dao;

import java.util.List;

import com.cn.eplat.model.DeptIdClue;

public interface IDeptIdClueDao {
	// 添加一条部门id线索串
	public int insertOneDeptIdClue(DeptIdClue dic);
	// 根据部门id查询包含该部门id的所有部门id线索字符串
	public List<DeptIdClue> queryAllDeptIdClueByDeptIdIncluded(int dept_id);
	// 删除与某一根部门id相关的所有部门id线索信息串
	public int deleteDeptIdClueByRootDid(int root_did);
}

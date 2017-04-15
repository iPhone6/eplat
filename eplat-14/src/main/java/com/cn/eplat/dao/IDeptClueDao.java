package com.cn.eplat.dao;

import com.cn.eplat.model.DeptClue;

public interface IDeptClueDao {
	// 根据部门线索信息字符串查询部门线索信息（假设每条部门线索信息字符串(对应clue_str字段)在数据库表dept_clue中都是唯一的，不会出现重复）
	public DeptClue queryDeptClueByDeptClueStr(String dept_clue_str);
	// 添加一条部门线索信息
	public int addDeptClue(DeptClue dc);
}

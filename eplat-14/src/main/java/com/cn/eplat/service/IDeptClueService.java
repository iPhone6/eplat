package com.cn.eplat.service;

import com.cn.eplat.model.DeptClue;

public interface IDeptClueService {
	// 根据部门线索信息字符串查找匹配的部门线索嘻嘻
	public DeptClue findDeptClueByDeptClueStr(String dept_clue_str);
	// 添加一条部门线索信息
	public int addDeptClue(DeptClue dc);
}

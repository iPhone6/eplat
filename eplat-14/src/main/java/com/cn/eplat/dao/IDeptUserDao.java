package com.cn.eplat.dao;

import com.cn.eplat.model.DeptUser;

public interface IDeptUserDao {
	// 根据部门id和人员id查询与之匹配的部门_人员对应关系信息
	public DeptUser queryDeptUserByDeptIdAndUid(DeptUser du);
	// 添加一条部门-员工对应关系信息
	public int addDeptUserInfo(DeptUser du);
}

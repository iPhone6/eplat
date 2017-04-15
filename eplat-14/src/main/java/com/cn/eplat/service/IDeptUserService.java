package com.cn.eplat.service;

import com.cn.eplat.model.DeptUser;

public interface IDeptUserService {
	// 根据部门id和用户uid获取匹配的部门_用户对应信息
	public DeptUser getDeptUserByDeptIdAndUid(DeptUser du);
	// 添加一条部门-员工对应关系信息
	public int addDeptUserInfo(DeptUser du);
}

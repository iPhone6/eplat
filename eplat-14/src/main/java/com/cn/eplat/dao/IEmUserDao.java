package com.cn.eplat.dao;

import java.util.List;
import java.util.Map;

import com.cn.eplat.model.EmUser;

public interface IEmUserDao {
	// 根据id查询环信系统用户信息
	public EmUser queryEmUserById(int id);
	// 添加一条环信用户信息
	public int insertEmUser(EmUser emu);
	// 根据id数组查询环信用户信息
	public List<EmUser> queryEmUsersByIds(int[] ids);
	// 根据id修改环信用户信息
	public int updateEmUserById(EmUser emu);
	// 根据环信用户名批量查询用户信息
	public List<Map<String, Object>> batchQueryUserInfoByEmUsernames(List<String> em_unames);
}

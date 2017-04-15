package com.cn.eplat.service;

import java.util.List;
import java.util.Map;

import com.cn.eplat.model.EmUser;

public interface IEmUserService {
	// 根据环信系统用户id获取环信系统用户信息
	public EmUser getEmUserById(int id);
	// 新增一个环信用户
	public int addEmUser(EmUser emu);
	// 根据id修改环信用户信息
	public int modifyEmUserById(EmUser emu);
	// 根据环信用户名批量获取用户信息
	public List<Map<String, Object>> batchGetUserInfoByEmUsernames(List<String> em_unames);
}

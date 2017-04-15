package com.cn.eplat.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.EmUser;
import com.cn.eplat.model.EpUser;

public interface IEpUserService {
	// 根据用户代码（code字段）获取用户信息
	public EpUser getEpUserByCode(String code);
	// 根据用户手机号码（mobile_phone字段）获取用户信息
	public EpUser getEpUserByMobileNum(String mobilePhone);
	// 根据用户邮箱（email字段）获取用户信息
	public EpUser getEpUserByEmail(String email);
	// 根据用户工号（work_no字段）获取用户信息
	public EpUser getEpUserByWorkNum(String workNum);
	// 根据用户id修改用户信息
	public int modifyEpUserById(EpUser epu);
	// 根据用户id修改用户信息（包含null字段值）
	public int modifyEpUserByIdIncludingNull(EpUser epu);
	// 根据关键字搜索公司账号，然后返回相应的环信账号
	public List<EmUser> findEmUsersByKey(String key);
	// 添加一条用户信息
	public int addEpUser(EpUser epu);
	// 根据手机号在数据库中查询是否已有用户的手机号与之重复
	public boolean isMobilePhoneDuplicate(EpUser epu);
	// 根据除主键id以外其它字段为条件查询员工信息
	public List<EpUser> getEpUserByCriteria(EpUser epu);
	// 根据用户id获取用户信息
	public EpUser getEpUserById(int id);
	
	// 根据用户工号（对应数据库表ep_user中的wrok_no字段）查询用户信息(仅仅包含一些简单信息)
	public EpUser getSimpleEpUserByWorkNum(String workNum);
	
	// 根据用户姓名和一些特殊条件获取唯一确定一个用户
	public List<EpUser> getOneEpUserByNameAndSpecialCriterion(String name);
	
	//给原始密码为空的用户设置初始密码
	public int updateOriginPwdById(Integer id,String pwd,String md5Pwd);
	
	//修改密码(原始密码不修改)
	public int updatePwdById(Integer id,String pwd,String md5Pwd);
	
	//修改验证码以及生成的时间
	public int updateVercodeById(Integer id,String ver_code,Date vercode_time);
	
	//根据email查询epuser
	public EpUser getVerCodeEpUserByEmail(String email);
		
	//更新对应的epuser的密码，验证码字段
	public int updateResetPwdByEmail(EpUser user);
	
	// 根据公司用户姓名模糊搜索匹配的公司用户信息及对应的环信用户信息
	public List<Map<String, Object>> fuzzyFindEpUserAndEmUserByName(String name_key);
	
	// 在一定条件下批量修改用户信息
	public int batchModifyEpUsersUnderSomeConditions(List<EpUser> epus);
}

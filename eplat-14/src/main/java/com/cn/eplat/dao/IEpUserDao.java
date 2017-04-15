package com.cn.eplat.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.EpUser;

public interface IEpUserDao {
	// 根据用户代码（对应数据库表ep_user中的code字段）查询用户信息
	public EpUser queryEpUserByCode(String code);
	
	// 根据用户手机号码（对应数据库表ep_user中的mobile_phone字段）查询用户信息
	public EpUser queryEpUserByMobileNum(String mobilePhone);
	
	// 根据用户邮箱（对应数据库表ep_user中的email字段）查询用户信息
	public EpUser queryEpUserByEmail(String email);
	
	// 根据用户工号（对应数据库表ep_user中的wrok_no字段）查询用户信息
	public EpUser queryEpUserByWorkNum(String workNum);
	
	// 根据用户id修改用户信息
	public int updateEpUserById(EpUser epu);
	// 根据用户id修改用户信息（包含null值）
	public int updateEpUserByIdIncludingNull(EpUser epu);
	// 根据用户id修改用户信息（包含null值）
	public List<EpUser> testQueryEpUser(@Param("user") EpUser epu);
	
	// 根据关键字模糊查询公司系统用户
	public List<EpUser> queryEpUsersByKey(String key);
	// 添加一条用户信息
	public int addEpUser(EpUser epu);
	// 根据手机号查询有该手机号的用户数量
	public int queryEpUserByMobilePhone(EpUser epu);
	// 根据员工信息（除主键id外的其它字段信息）查询员工信息
	public List<EpUser> queryEpUserByCriteria(EpUser epu);
	// 根据用户id获取用户信息
	public EpUser queryEpUserById(int id);
	
	// 根据用户工号（对应数据库表ep_user中的wrok_no字段）查询用户信息(仅仅包含一些简单信息)
	public EpUser querySimpleEpUserByWorkNum(String workNum);
	
	// 根据用户姓名和一些特殊条件唯一确定一个用户
	public List<EpUser> queryOneEpUserByNameAndSpecialCriterion(String name);
	
	//获取原始密码为空的用户id集合
	public List<Integer> queryOriginPwdUser();
	
	//给原始密码为空的用户设置初始密码
	public int updateOriginPwdById(@Param("id") Integer id, @Param("pwd") String pwd,@Param("md5Pwd") String md5Pwd);
	
	//修改密码(原始密码不修改)
	public int updatePwdById(@Param("id") Integer id, @Param("pwd") String pwd,@Param("md5Pwd") String md5Pwd);

	//修改验证码以及生成的时间
	public int updateVercodeById(@Param("id") Integer id, @Param("ver_code") String ver_code,@Param("vercode_time") Date vercode_time);
	
	//根据email查询epuser
	public EpUser queryVerCodeEpUserByEmail(String email);
	
	//更新对应的epuser的密码，验证码字段
	public int updateResetPwdByEmail(EpUser user);
	
	// 根据公司用户姓名模糊查询匹配的公司用户信息及对应的环信用户信息
	public List<Map<String, Object>> fuzzyQueryEpUserAndEmUserByName(String name_key);
	
	// 查出测试账号中工号最大的用户
	public EpUser queryTestAccountEpUserWithMaxWorkNo();
	
	// 批量添加用户信息
	public int batchInsertEpUsers(List<EpUser> epus);
	
	// 批量修改用户信息
	public int batchUpdateEpUsers(Map<String, List<EpUser>> epus);
	
	// 根据用户姓名模糊查询用户信息
	public List<EpUser> fuzzyQueryEpUserByName(String name_key);
	
	// 根据一组用户姓名查询用户信息
	public List<EpUser> queryEpUserByNamesGroup(List<String> names);
	
	// 根据一组用户姓名和一些特殊条件查询用户信息
	public List<EpUser> queryEpUserByNamesGroupAndSpecialConditions(List<String> names);
	
	// 在一定条件下批量修改用户信息
	public int batchUpdateEpUsersUnderSomeConditions(Map<String, List<EpUser>> epus_map);
	
	//根据id查询用户工号
	public String queryWorkNumById(int id);
}

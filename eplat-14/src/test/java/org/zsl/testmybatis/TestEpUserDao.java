package org.zsl.testmybatis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.utils.CreatePasswordHelper;
import com.cn.eplat.utils.CryptionUtil;
import com.cn.eplat.utils.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestEpUserDao {
	
	private static Logger logger = Logger.getLogger(TestEpUserDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	
	
	
	@Test
	public void testBatchUpdateEpUsersUnderSomeConditions() {
		EpUser epu1 = new EpUser();
		epu1.setId(8);
		epu1.setMach_userid(100);
		
		EpUser epu2 = new EpUser();
		epu2.setId(9);
		epu2.setMach_userid(102);
		
		EpUser epu3 = new EpUser();
		epu3.setId(7);
		epu3.setMach_userid(103);
		
//		EpUser epu4 = new EpUser();
//		epu4.setId(4);
//		epu3.setMach_userid(103);
		
		ArrayList<EpUser> epus_upd = new ArrayList<EpUser>();
		
		epus_upd.add(epu1);
		epus_upd.add(epu2);
		epus_upd.add(epu3);
//		epus_upd.add(epu4);
		
		HashMap<String, List<EpUser>> epus_map = new HashMap<String, List<EpUser>>();
		epus_map.put("data_map", epus_upd);
		
		int upd_ret = epUserDao.batchUpdateEpUsersUnderSomeConditions(epus_map);
		
		if(upd_ret <= 0) {
			logger.error("批量更新公司用户信息失败 + upd_ret = " + upd_ret);
			return;
		}
		
		logger.info("批量更新公司用户信息成功 + upd_ret = " + upd_ret);
		
	}
	
	
	
	/**
	 * 批量修改某些用户的最大设备数量和最大可绑定次数
	 */
	@Test
	public void batchModMaxDevNumAndMaxBindTimes() {
		String[] names_strs = {"任春宇1","张小庆","陈敬","谢时斌1","谢柳1","邹华新1","奋斗奋斗", "张洋舜","李伟1"};
		List<String> names = new ArrayList<String>();
		for(String name:names_strs) {
			names.add(name);
		}
		
		List<EpUser> epus = epUserDao.queryEpUserByNamesGroupAndSpecialConditions(names);
		if(epus == null) {
			logger.error("根据一组用户姓名获取用户信息失败");
			return;
		}
		
		logger.info("根据一组用户姓名获取用户信息成功，epus.size() = " + epus.size());
		
		List<EpUser> epus_to_mod = new ArrayList<EpUser>();
		
		for(EpUser epu:epus) {
//			System.out.println(epu);
			EpUser epu_mod = new EpUser();
			epu_mod.setId(epu.getId());
			boolean mod_flag = false;
			
			if(epu.getMax_device_num() == null || epu.getMax_device_num() < 2) {
				mod_flag = true;
				epu_mod.setMax_device_num(5);
			}
			
			if(epu.getMax_bind_times() == null || epu.getMax_bind_times() < 6) {
				mod_flag = true;
				epu_mod.setMax_bind_times(100);
			}
			
			if(mod_flag) {
				epus_to_mod.add(epu_mod);
			}
		}
		
		Map<String, List<EpUser>> data_map = new HashMap<String, List<EpUser>>();
		data_map.put("data_map", epus_to_mod);
		
		int batch_upd_ret = epUserDao.batchUpdateEpUsers(data_map);
		if(batch_upd_ret <= 0) {
			logger.error("批量更新用户信息失败，batch_upd_ret = " + batch_upd_ret);
			return;
		}
		
		logger.info("批量更新用户信息成功，batch_upd_ret = " + batch_upd_ret);
		
	}
	
	@Test
	public void testQueryEpUserByNamesGroup() {
		String[] names_strs = {"张三那","fghh","陈敬","李四","谢柳","邹华新","奋斗奋斗", "张洋舜","李伟"};
		List<String> names = new ArrayList<String>();
		for(String name:names_strs) {
			names.add(name);
		}
		
		List<EpUser> epus = epUserDao.queryEpUserByNamesGroup(names);
		if(epus == null) {
			logger.error("根据一组用户姓名获取用户信息失败");
			return;
		}
		
		logger.info("根据一组用户姓名获取用户信息成功，epus.size() = " + epus.size());
		for(EpUser epu:epus) {
			System.out.println(epu);
		}
	}
	
	
	@Test
	public void testBatchUpdateEpUsers() {
		
		String name_key = "eueueu";
		List<EpUser> epus = epUserDao.fuzzyQueryEpUserByName(name_key);
		if(epus == null) {
			logger.error("根据姓名模糊查询用户信息失败");
			return;
		}
		
		logger.info("根据姓名模糊查询用户信息成功 + epus.size() = " + epus.size());
		for(EpUser eu:epus) {
//			System.out.println(eu);
			Date now_date = new Date();
			eu.setNotes("测试修改备注，当前系统时间：" + DateUtil.formatDate(2, now_date));
//			eu.setName(null);
			eu.setWork_no(null);
			eu.setEmail(null);
			
		}
		
		Map<String, List<EpUser>> map_data = new HashMap<String, List<EpUser>>();
		List<EpUser> epu_list = new ArrayList<EpUser>();
		
		epu_list.addAll(epus);
		map_data.put("data_map", epu_list);
		
		int bueu_ret = epUserDao.batchUpdateEpUsers(map_data);
		
		if(bueu_ret <= 0) {
			logger.error("批量更新用户信息失败");
			return;
		}
		
		logger.info("批量更新用户信息成功，bueu_ret = " + bueu_ret);
		
	}
	
	
	@Test
	public void testFuzzyQueryEpUserByName() {
		String name_key = "eueu";
		
		List<EpUser> epus = epUserDao.fuzzyQueryEpUserByName(name_key);
		if(epus == null) {
			logger.error("根据姓名模糊查询用户信息失败");
			return;
		}
		
		logger.info("根据姓名模糊查询用户信息成功 + epus.size() = " + epus.size());
		for(EpUser eu:epus) {
			System.out.println(eu);
		}
		
		
	}
	
	
	@Test
	public void testBatchInsertEpUsers() {
		List<EpUser> epus = new ArrayList<EpUser>();
		for(int i=6; i<9; i++) {
			EpUser eu = new EpUser();
			eu.setName("eueueu" + genPostFix(i));
			eu.setMobile_phone("<NONE>");
			eu.setNotes(genPostFix(i));
			epus.add(eu);
		}
		
		System.out.println("批量添加操作前，用户信息：");
		for(EpUser epu:epus) {
			System.out.println(epu);
		}
		
		int ins_num = epUserDao.batchInsertEpUsers(epus);
		
		if(ins_num <= 0) {
			logger.error("批量添加用户信息失败");
			return;
		}
		
		logger.info("批量添加用户信息成功，ins_num = " + ins_num);
		
		System.out.println("批量添加成功后，用户信息：");
		for(EpUser epu:epus) {
			System.out.println(epu);
		}
		
	}
	
	
	private int processBatchCreateTestAcc(int count) {
		EpUser test_acc_max = epUserDao.queryTestAccountEpUserWithMaxWorkNo();
		if(test_acc_max == null) {
			return -1;
		}
		
		String tam_work_no = test_acc_max.getWork_no();
		if(tam_work_no == null) {
			return -2;
		}
		
		if(StringUtils.isBlank(tam_work_no)) {
			return -3;
		}
		
		try {
			int tam_work_num = Integer.parseInt(tam_work_no.trim());
			if(tam_work_num < 9000) {
				System.out.println("工号最大的测试账号的工号小于9000");
				return -5;
			}
			
			List<EpUser> epus_to_add = new ArrayList<EpUser>();
			
			for(int i=1; i<count+1; i++) {
				int base = tam_work_num - 9000 + i;	// 后缀加i
				String gen_post_fix = genPostFix(base);
				
				EpUser epu_new_test = new EpUser();
				epu_new_test.setName("测试账号" + gen_post_fix);
				epu_new_test.setPwd("E10ADC3949BA59ABBE56E057F20F883E");
				epu_new_test.setMima("123456");
				epu_new_test.setWork_no(String.valueOf(tam_work_num + i));	// 工号加i
				epu_new_test.setMobile_phone("<NONE>");
				epu_new_test.setEmail("ceshizh" + gen_post_fix + "@e-lead.cn");
				epu_new_test.setDept_name("XXX部");
				epu_new_test.setProject_name("0");
				epu_new_test.setOrigin_pwd("1E9932D8507C3C751D572AA1DA2806EF");
				
				epus_to_add.add(epu_new_test);
				/*
				int add_ret = epUserDao.addEpUser(epu_new_test);
				if(add_ret <= 0) {
					logger.error("添加新测试账号( " + epu_new_test + " )失败");
					continue;
				}
				logger.info("添加新测试账号( epu_id = " + epu_new_test.getId() + ", epu_name = " + epu_new_test.getName() + ", epu_work_no = " + epu_new_test.getWork_no() + " )成功");
				*/
			}
			
			int batch_ret = epUserDao.batchInsertEpUsers(epus_to_add);
			if(batch_ret <= 0) {
				logger.error("批量添加测试用户账号失败");
				return -7;
			}
			
			logger.info("批量添加测试用户账号成功，batch_ret = " + batch_ret);
			
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			System.out.println("将工号转换成整数时出现异常");
			return -4;
		} catch (Exception e2) {
			e2.printStackTrace();
			System.out.println("出现了其它异常！！！");
			return -6;
		}
		
		return 1;
	}
	
	/**
	 * 批量创建测试账号
	 */
	@Test
	public void batchCreateTestAccounts() {
		int count = 9;	// 一次性创建测试账号的个数，默认一次创建10个
		/*
		EpUser test_acc_max = epUserDao.queryTestAccountEpUserWithMaxWorkNo();
		if(test_acc_max == null) {
			return -1;
		}
		
		String tam_work_no = test_acc_max.getWork_no();
		if(tam_work_no == null) {
			return -2;
		}
		
		if(StringUtils.isBlank(tam_work_no)) {
			return -3;
		}
		
		try {
			int tam_work_num = Integer.parseInt(tam_work_no);
			if(tam_work_num < 3000) {
				System.out.println("工号最大的测试账号的工号小于3000");
				return -5;
			}
			
			for(int i=1; i<count+1; i++) {
				int base = tam_work_num - 3000 + i;	// 后缀加i
				String gen_post_fix = genPostFix(base);
				
				EpUser epu_new_test = new EpUser();
				epu_new_test.setName("测试账号" + gen_post_fix);
				epu_new_test.setPwd("E10ADC3949BA59ABBE56E057F20F883E");
				epu_new_test.setMima("123456");
				epu_new_test.setWork_no(String.valueOf(tam_work_num + i));	// 工号加i
				epu_new_test.setEmail("ceshizh" + gen_post_fix + "@e-lead.cn");
				epu_new_test.setDept_name("XXX部");
				epu_new_test.setProject_name("0");
				epu_new_test.setOrigin_pwd("1E9932D8507C3C751D572AA1DA2806EF");
				
				int add_ret = epUserDao.addEpUser(epu_new_test);
				if(add_ret <= 0) {
					logger.error("添加新测试账号( " + epu_new_test + " )失败");
					continue;
				}
				logger.info("添加新测试账号( epu_id = " + epu_new_test.getId() + ", epu_name = " + epu_new_test.getName() + ", epu_work_no = " + epu_new_test.getWork_no() + " )成功");
				
			}
			
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			System.out.println("将工号转换成整数时出现异常");
			return -4;
		}
		
		
		return 999;
		
		*/
		
		int proc_ret = processBatchCreateTestAcc(count);
		
		System.out.println("批量创建测试账号的处理过程返回结果代码为：" + proc_ret);
		
	}
	
	/**
	 * 生成所需的字符串后缀
	 * 
	 * @param num
	 * @return
	 */
	public static String genPostFix(int num) {
		if(num < 0) {
			return "-" + genPostFix(-num);
		}
		
		if(num < 10) {	// 如果传入的整数为一位数，则自动在前面补一个0
			return "0" + num;
		}
		
		return "" + num;
		
//		return null;
	}
	
	@Test
	public void testQueryTestAccountEpUserWithMaxWorkNo() {
		EpUser max_ta = epUserDao.queryTestAccountEpUserWithMaxWorkNo();
		if(max_ta == null) {
			logger.error("查询工号最大的测试账号用户信息失败");
			return;
		}
		
		logger.info("查询工号最大的测试账号用户信息成功");
		System.out.println("找到的用户信息：\n" + max_ta);
		
	}
	
	@Test
	public void testFuzzyQueryEpUserAndEmUserByName() {
		String name_key = "伟";
		List<Map<String, Object>> results = epUserDao.fuzzyQueryEpUserAndEmUserByName(name_key);
		if(results == null) {
			logger.error("根据用户姓名关键字查询用户列表失败");
			return;
		}
		
		logger.info("根据用户姓名关键字查询用户列表成功，results.size() = " + results.size());
		
		for(Map<String, Object> res:results) {
			System.out.println(JSONObject.toJSONString(res, SerializerFeature.WriteMapNullValue));
		}
	}
	
	public void testQueryEqUserByCode() {
		String code = "renchunyu";
		EpUser epu = epUserDao.queryEpUserByCode(code);
		if(epu != null) {
			logger.info("根据用户代码查询用户信息成功！");
			logger.info("epu = " + epu);
		} else {
			logger.debug("根据用户代码查询用户信息失败！");
		}
	}
	
	public void testModifyEpUserById() {
		EpUser epu = new EpUser();
		epu.setId(3);
//		epu.setPwd("hhh3");
//		epu.setMima("kkll");
		epu.setRole_id(8);
		int ret = epUserDao.updateEpUserById(epu);
		if(ret > 0) {
			logger.info("根据id修改用户信息成功！");
			logger.info("ret = " + ret);
		} else {
			logger.info("未修改用户信息！ret = " + ret);
		}
	}
	
	public void testQueryEpUsersByKey() {
		String key = "an";
		List<EpUser> epus = epUserDao.queryEpUsersByKey(key);
		if(epus != null) {
			logger.info("根据关键字查询公司系统用户成功！+ epus.size() = " + epus.size());
			for(EpUser epu:epus) {
				System.out.println(epu);
			}
		} else {
			logger.info("根据关键字未查询到匹配的公司系统用户！");
		}
	}
	
	@Test
	public void testAddEpUser() {
		EpUser epu = new EpUser();
		epu.setName("恐怕不多");
		epu.setOn_position_date(DateUtil.parse2date(2, "2015-10-03 22:10:12"));
		epu.setCode("000222111");
		epu.setMobile_phone("55566600333");
		
		logger.info("插入前，epu = " + epu);
		int ret = epUserDao.addEpUser(epu);
		if(ret > 0 ) {
			logger.info("插入一条用户信息成功！+ ret = " + ret);
			logger.info("插入后，epu = " + epu);
		} else {
			logger.debug("插入用户信息失败！+ ret = " + ret);
		}
	}
	
	public void testQueryEpUserByMobilePhone() {
		EpUser epu = new EpUser();
		epu.setMobile_phone("136302142200");
		int count = epUserDao.queryEpUserByMobilePhone(epu);
		if(count > 0) {
			logger.info("根据手机号找到已有 " + count + " 个用户的手机号与之相同");
		} else {
			logger.debug("在数据库中未找到已有用户的手机号与之相同");
		}
	}
	
	public void testQueryEpUserByCriteria() {
		EpUser epu = new EpUser();
		epu.setName("");
//		epu.setMobile_phone("126302142200");
		List<EpUser> res = epUserDao.queryEpUserByCriteria(epu);
		if(res != null) {
			logger.info("根据条件查询到匹配的员工信息条数：" + res.size());
			for(EpUser eu:res) {
				System.out.println(eu);
			}
		} else {
			logger.debug("根据条件未查询到匹配的员工信息");
		}
	}
	
	@Test
	public void testQueryEpUserById() {
		int id = 47;
		EpUser epu = epUserDao.queryEpUserById(id);
		if(epu != null) {
			logger.info("根据id查询用户信息成功");
			logger.info("epu = " + epu);
		} else {
			logger.info("根据id未查询到用户信息");
		}
	}
	
	public void testUpdateOriginPwd(){
		List<Integer> pwdUser = epUserDao.queryOriginPwdUser();
		for (Integer id : pwdUser) {
			String pwd = CreatePasswordHelper.createPassword(6);
			String md5Pwd = CryptionUtil.md5Hex(pwd);
			epUserDao.updateOriginPwdById(id, pwd, md5Pwd);
		}
	}
	
	@Test
	public void testUpdateEpUserByIdIncludingNull() {
		int id = 5;
		EpUser epu_orig = epUserDao.queryEpUserById(id);
		
		EpUser epu_upd = new EpUser();
	}
	
	
	@Test
	public void testQueryEpUser() {
		EpUser epu = new EpUser();
		epu.setName("任春宇");
		List<EpUser> epus = epUserDao.testQueryEpUser(epu);
		System.out.println("epus size = " + epus.size());
		System.out.println(epus);
	}
	
	
	
	
	public static void main(String[] args) {
//		Integer i = 12;
//		Integer j;
////		j = Integer.valueOf(i);
//		j = 12;
//		System.out.println(j == i);
		
//		String str1 = "123";
//		String str2 = null;
////		str2 = new String(str1);
//		str2 = "123";
//		System.out.println(str2 == str1);
		
		/*
		Date date1 = new Date();
		Date date2 = null;
		
//		date2 = new Date(date1.getTime());
		date2 = (Date) date1.clone();
		System.out.println(date2 == date1);
		System.out.println("date1 = " + date1 + ", time_mills = " + date1.getTime());
		System.out.println("date2 = " + date2 + ", time_mills = " + date2.getTime());
		*/
		
		int num = 0;
		String gen_ret = genPostFix(num);
		System.out.println("生成的字符串后缀：" + gen_ret);
		
	}
	
}

package org.zsl.testmybatis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IEmUserDao;
import com.cn.eplat.model.EmUser;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestEmUserDao {
	
	private static Logger logger = Logger.getLogger(TestEmUserDao.class);
	
	@Resource
	private IEmUserDao emUserDao;
	
	
	@Test
	public void testBatchQueryUserInfoByEmUsernames() {
		ArrayList<String> names_list = new ArrayList<String>();
		names_list.add("kkk");
		names_list.add("r0608");
		names_list.add("44ff");
		names_list.add("z1020");
		names_list.add("klf");
		List<Map<String, Object>> results = emUserDao.batchQueryUserInfoByEmUsernames(names_list);
		if(results == null) {
			logger.error("根据环信用户名批量查询用户信息失败");
			return;
		}
		
		int res_count = results.size();
		if(res_count == 0) {
			logger.info("根据环信用户名批量查询用户信息结果为空 + res_count = " + res_count);
			return;
		}
		
		logger.info("根据环信用户名批量查询用户信息成功 + res_count = " + res_count);
		for(Map<String, Object> res:results) {
			System.out.println(res);
		}
	}
	
	
	@Test
	public void testQueryEmUserById() {
		int id = 1;
		EmUser emu = emUserDao.queryEmUserById(id);
		if(emu != null) {
			logger.info("根据id查询环信系统用户信息成功！");
			logger.info("emu = " + emu);
		}
	}
	
	@Test
	public void testInsertEmUser() {
		EmUser emu = new EmUser();
		emu.setUsername("yyee");
		emu.setPassword("ferr44");
		emu.setCreated(123456789L);
		int ret = emUserDao.insertEmUser(emu);
		if(ret > 0) {
			logger.info("添加一条环信用户信息成功！ret = " + ret);
			logger.info("用户信息为：emu = " + emu);
		} else {
			logger.error("添加环信用户信息失败！ret = " + ret);
		}
	}
	
	@Test
	public void testQueryEmUsersByIds() {
		int[] ids = {1, 10, 9, 17};
		
		List<EmUser> emus = emUserDao.queryEmUsersByIds(ids);
		
		if(emus != null) {
			logger.info("根据id数组查询环信用户信息成功！+ emus.size() = " + emus.size());
			for(EmUser emu:emus) {
				logger.info(emu);
			}
		} else {
			logger.error("根据id数组未查询到环信用户信息！");
		}
	}
	
	@Test
	public void testUpdateEmUserById() {
		EmUser emu = new EmUser();
		emu.setId(2);
		emu.setPassword("lllkkmm99");
		int ret = emUserDao.updateEmUserById(emu);
		if(ret > 0) {
			logger.info("根据id修改环信用户信息成功！+ ret = " + ret);
		} else {
			logger.info("未修改用户信息！");
		}
	}
	
	
	
}

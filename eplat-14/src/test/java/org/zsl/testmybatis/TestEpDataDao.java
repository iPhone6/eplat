package org.zsl.testmybatis;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IEpDataDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpData;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestEpDataDao {
	
	private static Logger logger = Logger.getLogger(TestEpUserDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpDataDao epDataDao;
	
	@Test
	public void testQueryEpDataByAppidAndRoleId() {
		String appid = "10001";
		Integer role_id = Integer.valueOf(2);
		
		EpData epd = epDataDao.queryEpDataByAppidAndRoleId(appid, role_id);
		if(epd != null) {
//			System.out.println("查询成功！ + epd = " + epd);
			logger.debug("根据appid和角色id查询假数据成功！");
			logger.info("epd = " + epd);
		} else {
			logger.error("查询失败！");
		}
	}

}

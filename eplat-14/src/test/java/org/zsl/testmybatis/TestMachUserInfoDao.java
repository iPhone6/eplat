package org.zsl.testmybatis;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.dao.IMachCheckInOutDao;
import com.cn.eplat.dao.IMachUserInfoDao;
import com.cn.eplat.datasource.DataSourceContextHolder;
import com.cn.eplat.datasource.DataSourceType;
import com.cn.eplat.model.MachUserInfo;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestMachUserInfoDao {
	
	private static Logger logger = Logger.getLogger(TestMachUserInfoDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	
	@Resource
	private IMachUserInfoDao machUserInfoDao;
	
	
	
	
	
	@Test
	public void testQueryMachUserInfoNumber() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		int mui_num = machUserInfoDao.queryMachUserInfoNumber();
		
		logger.info("mui_num = " + mui_num);
		
	}
	
	
	
	@Test
	public void testQueryAllMachUserInfos() {
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		List<MachUserInfo> uis = machUserInfoDao.queryAllMachUserInfos();
		
		if(uis == null || uis.size() == 0) {
			logger.error("查询失败，或查到的用户信息条数为0");
			return;
		}
		
		int count = uis.size();
		logger.info("查询成功，uis.size() = " + count);
		
//		for(MachUserInfo mui : uis) {
//			System.out.println(mui.getBadgenumber());
//		}
		
		for(int i=0; i<count; i++) {
			if(i<50) {
				System.out.println(uis.get(i));
			} else {
				break;
			}
		}
		
	}
	
	
}

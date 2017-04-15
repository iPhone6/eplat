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
import com.cn.eplat.dao.IMachMachinesDao;
import com.cn.eplat.dao.IMachSyslogCopyDao;
import com.cn.eplat.datasource.DataSourceContextHolder;
import com.cn.eplat.datasource.DataSourceType;
import com.cn.eplat.model.MachMachines;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestMachMachinesDao {
	
	
	private static Logger logger = Logger.getLogger(TestMachMachinesDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	
	@Resource
	private IMachMachinesDao machMachinesDao;
	
	
	
	@Test
	public void testQueryAllMachineSns() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		List<String> sns = machMachinesDao.queryAllMachineSns();
		
		if(sns == null) {
			logger.error("查询失败");
			return;
		}
		
		if(sns.size() == 0) {
			logger.error("查询结果为空");
			return;
		}
		
		logger.info("查询成功，sns.size() = " + sns.size());
		for(String sn : sns) {
			System.out.println("打卡机序列号：" + sn);
		}
		
		
	}
	
	
	
	@Test
	public void testQueryAllMachMachines() {
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		List<MachMachines> machines = machMachinesDao.queryAllMachMachines();
		
		if(machines == null || machines.size() == 0) {
			logger.error("查询失败或结果为空");
			return;
		}
		
		logger.info("查询成功，machines.size() = " + machines.size());
		for(MachMachines mach : machines) {
			System.out.println(mach);
		}
		
		
	}
	
}
